package za.ac.sun.cs.deepsea.configuration;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.Trigger;
import za.ac.sun.cs.deepsea.logging.LogHandler;

/**
 * A {@link Configuration} takes an instance of {@link Properties} and processes
 * all the "{@code deepsea}" properties to configure a {@link Diver} instance.
 *
 * @author Jaco Geldenhuys <geld@sun.ac.za>
 */
public class Configuration {

	/**
	 * 
	 */
	private final Diver diver;

	/**
	 * 
	 */
	private final Logger log;

	/**
	 * 
	 */
	private final Properties properties;

	private static final StringBuilder sb = new StringBuilder();

	/**
	 * @param diver
	 * @param properties
	 */
	public Configuration(final Diver diver, final Properties properties) {
		this.diver = diver;
		log = this.diver.getLog();
		this.properties = properties;
	}

	public static int getIntegerProperty(Properties properties, String key, int defaultValue) {
		String s = properties.getProperty(key, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException x) {
			// ignore
		}
		return defaultValue;
	}

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
	 * 
	 */
	public void apply() {
		setLevel();
		setTarget();
		setArgs();
		setTriggers();
		setProduceOutput();
		dump();
	}

	/**
	 * 
	 */
	private void setLevel() {
		String p = properties.getProperty("deepsea.log.level");
		if (p != null) {
			try {
				Level l = Level.parse(p);
				log.setLevel(l);
				for (Handler h : log.getHandlers()) {
					if (h instanceof LogHandler) {
						h.setLevel(l);
					}
				}
			} catch (IllegalArgumentException x) {
				log.log(Level.SEVERE, "log level error", x);
			}
		}
	}

	/**
	 * 
	 */
	private void setTarget() {
		String p = properties.getProperty("deepsea.target");
		if (p != null) {
			diver.setTarget(p);
		}
	}

	/**
	 * 
	 */
	private void setArgs() {
		String p = properties.getProperty("deepsea.args");
		if (p != null) {
			diver.setArgs(p);
		}
	}

	private void setTriggers() {
		String p = properties.getProperty("deepsea.triggers");
		if (p != null) {
			String[] triggers = p.trim().split(";");
			for (String trigger : triggers) {
				processTrigger(trigger);
			}
		}
	}

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
						log.warning(sb.toString());
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
			log.warning(sb.toString());
		}
	}

	private Object parseType(String type) {
		if (type.equals("int")) {
			return Integer.class;
		} else if (type.equals("boolean")) {
			return Boolean.class;
		} else {
			return Object.class;
		}
	}

	private void setProduceOutput() {
		diver.produceOutput(getBooleanProperty(properties, "deepsea.produceoutput", diver.isProducintOutput()));
	}

	private void dump() {
		if (getBooleanProperty(properties, "deepsea.log.dump", false)) {
			for (Object k : properties.keySet()) {
				log.config(k.toString() + " =  " + properties.getProperty(k.toString()));
			}
		}
	}

}
