package za.ac.sun.cs.deepsea.configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.Trigger;
import za.ac.sun.cs.deepsea.explorer.Explorer;
import za.ac.sun.cs.deepsea.logging.LogHandler;

/**
 * A {@link Configuration} takes an instance of {@link Properties} and processes
 * all the "{@code deepsea}" properties to configure a {@link Diver} instance.
 *
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class Configuration {

	/**
	 * The {@link Diver} instance to configure.
	 */
	private final Diver diver;

	/**
	 * The {@link Logger} associated with the {@link #diver}.
	 */
	private final Logger log;

	/**
	 * The {@link Properties} instance where the settings are read from.
	 */
	private final Properties properties;

	/**
	 * Classloader for creating instances of user-specified objects.
	 */
	private final ClassLoader loader = Configuration.class.getClassLoader();

	/**
	 * An internal instance used to construct log messages.
	 */
	private static final StringBuilder sb = new StringBuilder();

	/**
	 * Constructs an instance of the configuration manager.
	 * 
	 * @param diver
	 *            the {@link Diver} instance to configure
	 * @param properties
	 *            the {@link Properties} instance to read settings from
	 */
	public Configuration(final Diver diver, final Properties properties) {
		this.diver = diver;
		log = this.diver.getLog();
		this.properties = properties;
	}

	/**
	 * Reads an integer property from a {@link Properties} file.
	 * 
	 * @param properties
	 *            the {@link Properties} instance to read from
	 * @param key
	 *            the property key
	 * @param defaultValue
	 *            the default value to return if the key is not found
	 * @return the integer value associated with the key (or the default value
	 *         supplied)
	 */
	public static int getIntegerProperty(Properties properties, String key, int defaultValue) {
		String s = properties.getProperty(key, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException x) {
			// ignore
		}
		return defaultValue;
	}

	/**
	 * Reads an integer property from a {@link Properties} file.
	 * 
	 * @param properties
	 *            the {@link Properties} instance to read from
	 * @param key
	 *            the property key
	 * @return the integer value associated with the key (or {@code null} if the
	 *         key is absent or not an integer)
	 */
	public static Integer getIntegerProperty(Properties properties, String key) {
		String s = properties.getProperty(key);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException x) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * Reads a boolean property from a {@link Properties} file.
	 * 
	 * @param properties
	 *            the {@link Properties} instance to read from
	 * @param key
	 *            the property key
	 * @param defaultValue
	 *            the default value to return if the key is not found
	 * @return the boolean value associated with the key (or the default value
	 *         supplied)
	 */
	public static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
		String s = properties.getProperty(key, Boolean.toString(defaultValue)).trim();
		try {
			if (s.equalsIgnoreCase("true")) {
				return true;
			} else if (s.equalsIgnoreCase("yes")) {
				return true;
			} else if (s.equalsIgnoreCase("on")) {
				return true;
			} else if (s.equalsIgnoreCase("1")) {
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException x) {
			// ignore
		}
		return defaultValue;
	}

	/**
	 * Does the actual work of reading properties and calling the appropriate
	 * routines of the {@link Diver} instance.
	 */
	public void apply() {
		setLog();
		setTarget();
		setArgs();
		setTriggers();
		setBounds();
		setProduceOutput();
		setExplorer();
		dump();
	}

	/**
	 * Reads and sets the "{@code deepsea.log.level}" setting.
	 */
	private void setLog() {
		String p = properties.getProperty("deepsea.log.level");
		if (p != null) {
			try {
				Level l = Level.toLevel(p);
//				log.setLevel(l);
//				for (Handler h : log.getHandlers()) {
//					if (h instanceof LogHandler) {
//						h.setLevel(l);
//					}
//				}
			} catch (IllegalArgumentException x) {
				log.fatal("log level error", x);
			}
		}
//		diver.getLogHandler().setVerbose(getBooleanProperty(properties, "deepsea.log.verbose", false));
	}

	/**
	 * Reads and sets the "{@code deepsea.target}" setting.
	 */
	private void setTarget() {
		String p = properties.getProperty("deepsea.target");
		if (p != null) {
			diver.setTarget(p);
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.args}" setting.
	 */
	private void setArgs() {
		String p = properties.getProperty("deepsea.args");
		if (p != null) {
			diver.setArgs(p);
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.triggers}" setting. The value of this
	 * setting is expected to be a "<code>;</code>" separated list. Each
	 * components is the fully qualified name of a method with its parameters.
	 * The parameters is given as a "<code>,</code>" separated list of
	 * "<code>name:type</code>" and "<code>type</code>" entries. Parameters with
	 * a name is treated symbolically when the method is invoked; parameters
	 * without a name remain concrete. A trigger is only activated when the
	 * number of parameters and their types match; this caters for method
	 * overloading.
	 */
	private void setTriggers() {
		String p = properties.getProperty("deepsea.triggers");
		if (p != null) {
			String[] triggers = p.trim().split(";");
			for (String trigger : triggers) {
				processTrigger(trigger);
			}
		}
	}

	/**
	 * Processes a single method description.
	 * 
	 * @param triggerDesc
	 *            description of a triggering method
	 */
	private void processTrigger(String triggerDesc) {
		final Set<String> names = new HashSet<>();
		int paramStart = triggerDesc.indexOf('(');
		assert paramStart != -1;
		int paramEnd = triggerDesc.indexOf(')', paramStart);
		assert paramEnd != -1;
		String methodName = triggerDesc.substring(0, paramStart).trim();
		Trigger trigger = new Trigger(methodName);
		String parameterString = triggerDesc.substring(paramStart + 1, paramEnd).trim();
		if (parameterString.length() > 0) {
			String[] parameters = parameterString.split(",");
			trigger.setParameterCount(parameters.length);
			int index = 0;
			for (String parameter : parameters) {
				int colonPos = parameter.indexOf(':');
				if (colonPos == -1) {
					trigger.setParameterType(index, parseType(parameter.trim()));
				} else {
					String name = parameter.substring(0, colonPos).trim();
					if (names.contains(name)) {
						sb.setLength(0);
						sb.append("ignored trigger with duplicates, ");
						sb.append('"').append(triggerDesc).append('"');
						log.warn(sb.toString());
						return;
					}
					names.add(name);
					Object type = parseType(parameter.substring(colonPos + 1).trim());
					trigger.setParameterName(index, name);
					trigger.setParameterType(index, type);
				}
				index++;
			}
		}
		if (names.size() > 0) {
			diver.addTrigger(trigger);
		} else {
			sb.setLength(0);
			sb.append("ignored non-symbolic trigger ");
			sb.append('"').append(triggerDesc).append('"');
			log.warn(sb.toString());
		}
	}

	/**
	 * Parses a string and returns the corresponding internal Java class.
	 * 
	 * @param type
	 *            a string such as "<code>int</code>" or "<code>boolean</code>"
	 * @return the corresponding Java class of the type
	 */
	private Object parseType(String type) {
		if (type.equals("int")) {
			return Integer.class;
		} else if (type.equals("boolean")) {
			return Boolean.class;
		} else {
			return Object.class;
		}
	}

	/**
	 * Reads and set the variable bounds when specified with the
	 * "{@code deepsea.bounds...}" settings.
	 */
	private void setBounds() {
		for (Object key : properties.keySet()) {
			String k = (String) key;
			if (k.startsWith("deepsea.bounds.")) {
				String var = k.substring("deepsea.bounds.".length());
				if (var.endsWith(".min")) {
					Integer min = getIntegerProperty(properties, k);
					if (min != null) {
						diver.setMinBound(var.substring(0, var.length() - 4), min);
					}
				} else if (var.endsWith(".max")) {
					Integer max = getIntegerProperty(properties, k);
					if (max != null) {
						diver.setMaxBound(var.substring(0, var.length() - 4), max);
					}
				} else {
					String[] bounds = properties.getProperty(k).split("\\.\\.");
					assert bounds.length >= 2;
					try {
						diver.setMinBound(var, Integer.parseInt(bounds[0].trim()));
						diver.setMaxBound(var, Integer.parseInt(bounds[1].trim()));
					} catch (NumberFormatException x) {
						log.warn("Bounds in \"" + k + "\" is malformed and ignored");
					}
				}
			}
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.produceoutput}" setting.
	 */
	private void setProduceOutput() {
		diver.produceOutput(getBooleanProperty(properties, "deepsea.produceoutput", diver.isProducingOutput()));
	}

	/**
	 * Reads and sets the "{@code deepsea.explorer}" setting.
	 */
	private void setExplorer() {
		String p = properties.getProperty("deepsea.explorer");
		if (p != null) {
			Explorer explorer = (Explorer) createInstance(p);
			if (explorer != null) {
				diver.setExplorer(explorer);
			}
		}
	}

	/**
	 * Creates an instance of the specified class. Two constructor patterns are
	 * tried to create an instance of {@code X}: first {@code X(Diver)} and
	 * then, if that does not work, {@code X(Diver, Properties)}.
	 * 
	 * @param objectName
	 *            the name of the class of the instance to create
	 * @return an instance of the given class
	 */
	private Object createInstance(String objectName) {
		Class<?> classx = loadClass(objectName);
		try {
			Constructor<?> constructor = null;
			try {
				constructor = classx.getConstructor(Diver.class);
				return constructor.newInstance(diver);
			} catch (NoSuchMethodException x) {
				// ignore
			}
			try {
				constructor = classx.getConstructor(Diver.class, Properties.class);
				return constructor.newInstance(diver, properties);
			} catch (NoSuchMethodException x) {
				log.fatal("constructor not found: " + objectName, x);
			}
		} catch (SecurityException x) {
			log.fatal("constructor not found: " + objectName, x);
		} catch (IllegalArgumentException x) {
			log.fatal("constructor error: " + objectName, x);
		} catch (InstantiationException x) {
			log.fatal("constructor error: " + objectName, x);
		} catch (IllegalAccessException x) {
			log.fatal("constructor error: " + objectName, x);
		} catch (InvocationTargetException x) {
			log.fatal("constructor error: " + objectName, x);
		}
		return null;
	}

	/**
	 * Tries to load the class with the given name.
	 * 
	 * @param className
	 *            name of the class to load
	 * @return the {@link Class} instance that corresponds to the loaded class
	 */
	private Class<?> loadClass(String className) {
		if ((className != null) && (className.length() > 0)) {
			try {
				return loader.loadClass(className);
			} catch (ClassNotFoundException x) {
				log.fatal("class not found: " + className, x);
			} catch (ExceptionInInitializerError x) {
				log.fatal("class not found: " + className, x);
			}
		}
		return null;
	}

	/**
	 * Dumps all the settings in the {@link Properties} file to the log.
	 */
	private void dump() {
		if (getBooleanProperty(properties, "deepsea.log.dump", false)) {
			for (Object k : properties.keySet()) {
				log.info(k.toString() + " =  " + properties.getProperty(k.toString()));
			}
		}
	}

}
