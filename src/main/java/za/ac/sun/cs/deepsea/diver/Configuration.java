package za.ac.sun.cs.deepsea.diver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.layout.PatternLayout;

import com.sun.jdi.Method;

import za.ac.sun.cs.deepsea.explorer.Explorer;
import za.ac.sun.cs.deepsea.reporting.Banner;

/**
 * A container for user-configurable parameters for analysis.
 */
public class Configuration implements Serializable {

	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = -8641663835268556564L;

	/**
	 * The default name of the detailed log file.
	 */
	private static final String DEFAULT_LOGFILE = "/tmp/deepsea.log";

	/**
	 * The minimum value that integer variables can assume by default.
	 */
	private static final int DEFAULT_MIN_INT_VALUE = 0;

	/**
	 * The maximum value that integer variables can assume by default.
	 */
	private static final int DEFAULT_MAX_INT_VALUE = 99;

	/**
	 * Properties associated with these settings.
	 */
	protected Properties properties = null;

	/**
	 * Logger to write to.
	 */
	protected Logger logger = null;

	/**
	 * The fully qualified of the target class. This class should contain a Java
	 * {@code main} method and it is the class that is run during each dive.
	 */
	protected String target = null;

	/**
	 * Arguments pass to the target class when it is run during a dive.
	 */
	private String args = null;

	/**
	 * The database of triggers. Each trigger is a method that will switch the
	 * dive to symbolic mode. The trigger also describes which arguments are
	 * treated symbolically, and which arguments stay concrete.
	 */
	private final List<Trigger> triggers = new LinkedList<>();

	/**
	 * The database of delegates. Each delegate is an object that may (or may
	 * not) contain routines that are invoked when the responding method of the
	 * class named by the key is called. In other words, if the database
	 * contains the key-value pair <C, O>, and the method C.M is invoked, the
	 * delegate O.M is invoked, if it exists.
	 */
	private final Map<String, Object> delegates = new HashMap<>();

	/**
	 * Maps variable names to lower bounds.
	 */
	private final Map<String, Integer> minBounds = new HashMap<>();

	/**
	 * Maps variable names to upper bounds.
	 */
	private final Map<String, Integer> maxBounds = new HashMap<>();

	/**
	 * Whether or not the output of the target program should be displayed
	 * ({@code true}) or suppressed ({@code false}).
	 */
	private boolean echoOutput = false;

	/**
	 * Whether or not the settings should be dumped to the log.
	 */
	private String logfile = DEFAULT_LOGFILE;

	/**
	 * Whether or not the settings should be dumped to the log.
	 */
	private boolean dumpConfig = false;

	/**
	 * Whether or not the properties should be dumped to the log.
	 */
	private boolean dumpProperties = false;

	/**
	 * Whether or not DEEPSEA will be distributed.
	 */
	private boolean distributed = false;

	/**
	 * The Explorer that directs the investigation of target programs.
	 */
	private Explorer explorer;

	/**
	 * Class loader for creating instances of user-specified objects.
	 */
	private final ClassLoader loader = Configuration.class.getClassLoader();

	//======================================================================
	//
	// Logger constructor.
	//
	//======================================================================

	/**
	 * The name of the logger. Each logger has a name so that it can be
	 * "accessed" from different contexts.
	 */
	private static final String LOGGER_NAME = "za.ac.sun.cs.deepsea.DEEPSEA";

	/**
	 * Pattern for log messages that are written to the console.
	 */
	private static final String PATTERN_1 = "%-5level %msg%n";

	/**
	 * Pattern for log messages that are written to the log file.
	 */
	private static final String PATTERN_2 = "%highlight{%d{HH:mm:ss.SSS} [%-8.8t] %-5level - %msg%n}{CONF=magenta,PROPS=magenta,TRACE=black,DEBUG=blue,INFO=blue,WARN=black,ERROR=red,FATAL=red}";

	/**
	 * Configures and creates the logger.
	 * 
	 * @return the new logger
	 */
	private Logger createLogger() {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		org.apache.logging.log4j.core.config.Configuration conf = context.getConfiguration();
		PatternLayout pl1 = PatternLayout.newBuilder().withConfiguration(conf).withPattern(PATTERN_1).build();
		Appender a1 = ConsoleAppender.newBuilder().setConfiguration(conf).withName("ConsoleLog")
				.setTarget(Target.SYSTEM_OUT).withLayout(pl1).build();
		a1.start();
		PatternLayout pl2 = PatternLayout.newBuilder().withConfiguration(conf).withPattern(PATTERN_2).build();
		Appender a2 = FileAppender.newBuilder().setConfiguration(conf).withName("FileLog").withFileName(getLogfile())
				.withAppend(false).withLayout(pl2).build();
		a2.start();
		AppenderRef r1 = AppenderRef.createAppenderRef("ConsoleLog", Level.INFO, null);
		AppenderRef r2 = AppenderRef.createAppenderRef("FileLog", null, null);
		AppenderRef[] refs = new AppenderRef[] { r1, r2 };
		LoggerConfig lc = LoggerConfig.createLogger(false, Level.TRACE, LOGGER_NAME, "true", refs, null, conf, null);
		lc.addAppender(a1, Level.INFO, null);
		lc.addAppender(a2, null, null);
		conf.addLogger(LOGGER_NAME, lc);
		context.updateLoggers();
		return LogManager.getLogger(LOGGER_NAME);
	}

	/**
	 * Returns the logger.
	 * 
	 * @return the logger
	 */
	public Logger getLogger() {
		if (logger == null) {
			logger = createLogger();
		}
		return logger;
	}

	//======================================================================
	//
	// Getters and setters for the settings.
	//
	//======================================================================

	/**
	 * Returns the target class.
	 * 
	 * @return the fully qualified target class
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target class.
	 * 
	 * @param target
	 *            the fully qualified target class
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Returns the command-line arguments to pass to the target program.
	 * 
	 * @return the command-line arguments for the target
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * Sets the command-line arguments to pass to the target program.
	 * 
	 * @param args
	 *            the command-line arguments for the target
	 */
	public void setArgs(String args) {
		this.args = args;
	}

	/**
	 * Finds the first trigger that matches the given method and class. If no
	 * matching trigger is found, the method returns {@code null}.
	 * 
	 * @param method
	 *            the method to match
	 * @param className
	 *            the name of the class to match
	 * @return the first matching trigger or {@code null}
	 */
	public Trigger findTrigger(Method method, String className) {
		for (Trigger trigger : triggers) {
			if (trigger.match(className, method)) {
				return trigger;
			}
		}
		return null;
	}

	/**
	 * Returns the number of triggers.
	 * 
	 * @return the number of triggers
	 */
	public int getNumberOfTriggers() {
		return triggers.size();
	}

	/**
	 * Returns an iterable over all triggers.
	 * 
	 * @return an iterable over triggers
	 */
	public Iterable<Trigger> getTriggers() {
		return triggers;
	}

	/**
	 * Adds a trigger to the database of triggers.
	 * 
	 * @param trigger
	 *            the trigger to add
	 */
	public void addTrigger(Trigger trigger) {
		triggers.add(trigger);
	}

	/**
	 * Returns the delegate object that handles the routines of the given target
	 * class.
	 * 
	 * @param target
	 *            the fully-qualified name of a class to be delegated
	 * @return the object to delegate to, or {@code null}
	 */
	public Object findDelegate(String target) {
		return delegates.get(target);
	}

	/**
	 * Returns the number of delegates.
	 * 
	 * @return the number of delegates
	 */
	public int getNumberOfDelegateTargets() {
		return delegates.keySet().size();
	}

	/**
	 * Returns an iterable over all known delegated classes.
	 * 
	 * @return an iterable over delegated classes
	 */
	public Iterable<String> getDelegateTargets() {
		return delegates.keySet();
	}

	/**
	 * Add a new entry to the registry of delegates.
	 * 
	 * @param target
	 *            the class name that is delegates
	 * @param delegate
	 *            the object that handles calls of the target class
	 */
	public void addDelegate(String target, Object delegate) {
		delegates.put(target, delegate);
	}

	/**
	 * Returns the minimum value associated with a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @return the minimum value that the integer variable can assume
	 */
	public int getMinBound(String variable) {
		return getMinBound(variable, DEFAULT_MIN_INT_VALUE);
	}

	/**
	 * Returns the minimum value associated with a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @param defaultValue
	 *            the value to return if there is no bound available
	 * @return the minimum value that the integer variable can assume
	 */
	public int getMinBound(String variable, int defaultValue) {
		Integer min = minBounds.get(variable);
		if (min == null) {
			min = defaultValue;
		}
		return min;
	}

	/**
	 * Sets the minimum value of a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @param min
	 *            the minimum value
	 */
	public void setMinBound(String variable, int min) {
		minBounds.put(variable, min);
	}

	/**
	 * Returns the maximum value associated with a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @return the maximum value that the integer variable can assume
	 */
	public int getMaxBound(String variable) {
		return getMaxBound(variable, DEFAULT_MAX_INT_VALUE);
	}

	/**
	 * Returns the maximum value associated with a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @param defaultValue
	 *            the value to return if there is no bound available
	 * @return the maximum value that the integer variable can assume
	 */
	public int getMaxBound(String variable, int defaultValue) {
		Integer max = maxBounds.get(variable);
		if (max == null) {
			max = defaultValue;
		}
		return max;
	}

	/**
	 * Sets the maximum value of a variable.
	 * 
	 * @param variable
	 *            the variable name
	 * @param max
	 *            the maximum value
	 */
	public void setMaxBound(String variable, int max) {
		maxBounds.put(variable, max);
	}

	/**
	 * Returns whether or not the target program's output should be reproduced.
	 * 
	 * @return whether target program output should be echoed
	 */
	public boolean getEchoOutput() {
		return echoOutput;
	}

	/**
	 * Sets whether the target program's output should be reproduced (echoed).
	 * 
	 * @param echoOutput
	 *            whether or not to reproduce output
	 */
	public void setEchoOutput(boolean echoOutput) {
		this.echoOutput = echoOutput;
	}

	/**
	 * Returns the name of the log file.
	 * 
	 * @return the name of the log file
	 */
	public String getLogfile() {
		return logfile;
	}

	/**
	 * Sets the name of the log file.
	 * 
	 * @param logfile
	 *            the new name of the log file
	 */
	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	/**
	 * Returns whether or not the settings should be dumped to the log.
	 * 
	 * @return whether settings should be dumped
	 */
	public boolean getDumpConfig() {
		return dumpConfig;
	}

	/**
	 * Sets whether the settings should be dumped to the log.
	 * 
	 * @param dumpConfig
	 *            whether settings should be dumped
	 */
	public void setDumpConfig(boolean dumpConfig) {
		this.dumpConfig = dumpConfig;
	}

	/**
	 * Returns whether or not the properties should be dumped to the log.
	 * 
	 * @return whether properties should be dumped
	 */
	public boolean getDumpProperties() {
		return dumpProperties;
	}

	/**
	 * Sets whether DEEPSEA will be distributed.
	 * 
	 * @param distributed
	 *            whether DEEPSEA will be distributed
	 */
	public void setDistributed(boolean distributed) {
		this.distributed = distributed;
	}

	/**
	 * Returns whether or not DEEPSEA will be distributed.
	 * 
	 * @return whether DEEPSEA will be distributed
	 */
	public boolean getDistributed() {
		return distributed;
	}

	/**
	 * Sets whether the properties should be dumped to the log.
	 * 
	 * @param dumpProperties
	 *            whether properties should be dumped
	 */
	public void setDumpProperties(boolean dumpProperties) {
		this.dumpProperties = dumpProperties;
	}

	/**
	 * Returns the current explorer for this session.
	 * 
	 * @return the current instance of explorer
	 */
	public Explorer getExplorer() {
		return explorer;
	}

	/**
	 * Sets a new explorer for this session. Note that there is ever only
	 * explorer for a session.
	 * 
	 * @param explorer
	 *            the new explorer
	 */
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
	}

	/**
	 * The configuration logging level.
	 */
	private static final Level CONF = Level.forName("CONF", 350);

	/**
	 * Dumps all the settings if the flag is set.
	 */
	public void dumpConfig() {
		if (getDumpConfig() && (logger != null)) {
			// --- TARGET & ARGS ---
			if (getTarget() != null) {
				logger.log(CONF, "deepsea.target = {}", getTarget());
			}
			if (getArgs() != null) {
				logger.log(CONF, "deepsea.args = {}", getArgs());
			}
			// --- TRIGGERS ---
			int t = getNumberOfTriggers(), i = t;
			for (Trigger trigger : getTriggers()) {
				String pre = (i == t) ? "deepsea.triggers = " : "\t";
				String post = (i > 1) ? ";\\" : "";
				logger.log(CONF, "{}{}{}", pre, trigger.toString(), post);
				i--;

			}
			// --- DELEGATES ---
			int d = getNumberOfDelegateTargets(), j = d;
			for (String target : getDelegateTargets()) {
				Object delegate = findDelegate(target);
				String pre = (j == d) ? "deepsea.delegate = " : "\t";
				String post = (j > 1) ? ";\\" : "";
				logger.log(CONF, "{}{}:{}{}", pre, target, delegate.getClass().getName(), post);
				j--;
			}
			// --- BOUNDS ---
			Set<String> vars = new TreeSet<>(minBounds.keySet());
			vars.addAll(maxBounds.keySet());
			for (String var : vars) {
				if (!minBounds.containsKey(var)) {
					logger.log(CONF, "deepsea.bounds.{}.max = {}", var, maxBounds.get(var));
				} else if (!maxBounds.containsKey(var)) {
					logger.log(CONF, "deepsea.bounds.{}.min = {}", var, minBounds.get(var));
				} else {
					logger.log(CONF, "deepsea.bounds.{} = {}..{}", var, minBounds.get(var), maxBounds.get(var));
				}
			}
			// --- SOME BOOLEAN SETTINGS ---
			logger.log(CONF, "deepsea.echooutput = {}", getEchoOutput());
			logger.log(CONF, "deepsea.dumpconfig = {}", getDumpConfig());
			logger.log(CONF, "deepsea.dumpproperties = {}", getDumpProperties());
			logger.log(CONF, "deepsea.distributed = {}", getDistributed());
			// --- EXPLORER ---
			Explorer e = getExplorer();
			if (e != null) {
				logger.log(CONF, "deepsea.explorer = {}", e.getClass().getName());
			}
		}
	}

	/**
	 * The properties logging level.
	 */
	private static final Level PROPS = Level.forName("PROPS", 350);

	/**
	 * Dumps all the properties if the flag is set and they exist.
	 */
	public void dumpProperties() {
		if (getDumpProperties() && (properties != null) && (logger != null)) {
			SortedSet<Object> sortedKeys = new TreeSet<>(properties.keySet());
			for (Object key : sortedKeys) {
				assert key instanceof String;
				String k = key.toString();
				logger.log(PROPS, "{} = {}", k, properties.getProperty(k));
			}
		}
	}

	//======================================================================
	//
	// The rest of this file contains routines that reads and writes
	// properties from and to a Java properties file.
	//
	//======================================================================

	/**
	 * Write settings to a Java properties file.
	 * 
	 * @return a Java properties file that reflects the current configuration
	 */
	public Properties produceProperties() {
		StringBuilder sb = new StringBuilder();
		Properties properties = new Properties();
		if (getTarget() != null) {
			properties.setProperty("deepsea.target", getTarget());
		}
		if (getArgs() != null) {
			properties.setProperty("deepsea.args", getArgs());
		}
		sb.setLength(0);
		boolean isFirst = true;
		for (Trigger trigger : getTriggers()) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(';');
			}
			sb.append(trigger.toString());
		}
		if (!isFirst) {
			properties.setProperty("deepsea.triggers", sb.toString());
		}
		sb.setLength(0);
		isFirst = true;
		for (String target : getDelegateTargets()) {
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(';');
			}
			Object delegate = findDelegate(target);
			sb.append(target).append(':').append(delegate.getClass().getName());
		}
		if (!isFirst) {
			properties.setProperty("deepsea.delegates", sb.toString());
		}
		Set<String> vars = new TreeSet<>(minBounds.keySet());
		vars.addAll(maxBounds.keySet());
		for (String var : vars) {
			if (!minBounds.containsKey(var)) {
				properties.setProperty("deepsea.bounds." + var + ".max", "" + maxBounds.get(var));
			} else if (!maxBounds.containsKey(var)) {
				properties.setProperty("deepsea.bounds." + var + ".min", "" + minBounds.get(var));
			} else {
				properties.setProperty("deepsea.bounds." + var, minBounds.get(var) + ".." + maxBounds.get(var));
			}
		}
		properties.setProperty("deepsea.echooutput", "" + getEchoOutput());
		properties.setProperty("deepsea.dumpconfig", "" + getDumpConfig());
		properties.setProperty("deepsea.dumpproperties", "" + getDumpProperties());
		properties.setProperty("deepsea.distributed", "" + getDistributed());
		Explorer e = getExplorer();
		if (e != null) {
			properties.setProperty("deepsea.explorer", e.getClass().getName());
		}
		return properties;
	}

	/**
	 * Reads settings from a Java properties file.
	 * 
	 * @param filename
	 *            the name of the file to read from
	 * @return {@code true} if and only if properties were read successfully
	 */
	public boolean processProperties(String filename) {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(filename));
		} catch (IOException x) {
			return false;
		}
		processTarget();
		processArgs();
		processTriggers();
		processBounds();
		processEchoOutput();
		processLogfile();
		processDumpConfig();
		processDumpProperties();
		processDistributed();
		logger = getLogger(); // Need logger for following routines
		processDelegates();
		processExplorer();
		return true;
	}

	/**
	 * Reads settings from a Java properties object.
	 * 
	 * @param filename
	 *            the name of the file to read from
	 * @return {@code true} if and only if properties were read successfully
	 */
	public boolean processProperties(Properties properties) {
		this.properties = properties;
		processTarget();
		processArgs();
		processTriggers();
		processBounds();
		processEchoOutput();
		processLogfile();
		processDumpConfig();
		processDumpProperties();
		logger = getLogger(); // Need logger for following routines
		processDelegates();
		processExplorer();
		return true;
	}

	/**
	 * Reads and sets the "{@code deepsea.target}" setting.
	 * 
	 * @return {@code true} if and only if the property was read successfully
	 */
	private void processTarget() {
		String p = properties.getProperty("deepsea.target");
		if (p != null) {
			setTarget(p);
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.args}" setting.
	 * 
	 * @return {@code true} if and only if the property was read successfully
	 */
	private void processArgs() {
		String p = properties.getProperty("deepsea.args");
		if (p != null) {
			setArgs(p);
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.triggers}" setting. The value of this
	 * setting is expected to be a "<code>;</code>" separated list. Each
	 * component is the fully qualified name of a method with its parameters.
	 * The parameters is given as a "<code>,</code>" separated list of
	 * "<code>name:type</code>" and "<code>type</code>" entries. Parameters with
	 * a name is treated symbolically when the method is invoked; parameters
	 * without a name remain concrete. A trigger is only activated when the
	 * number of parameters and their types match; this caters for method
	 * overloading.
	 * 
	 * @return {@code true} if and only if the property was read successfully
	 */
	private void processTriggers() {
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
						new Banner('@').println("DEEPSEA PROBLEM\n")
								.println("IGNORED TRIGGER WITH DUPLICATES \"" + triggerDesc + "\"").display(System.out);
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
			addTrigger(trigger);
		} else {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("IGNORED NON-SYMBOLIC TRIGGER \"" + triggerDesc + "\"")
					.display(System.out);
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
		if (type.startsWith("int[")) {
			int closeBracket = type.indexOf(']');
			int len = 0;
			if (closeBracket > 4) {
				len = Integer.parseInt(type.substring(4, closeBracket));
			}
			return new Stepper.IntArray(len);
		} else if (type.equals("int")) {
			return Integer.class;
		} else if (type.equals("boolean")) {
			return Boolean.class;
		} else if (type.equals("string")) {
			return String.class;
		} else {
			return Object.class;
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.delegates}" setting. The value of this
	 * setting is expected to be a "<code>;</code>" separated list. Each
	 * component is a two-part mapping "X:Y", where both "X" and "Y" are fully
	 * qualified names of classes. When the system calls a method of class "X",
	 * a corresponding method of class "Y" is invoked.
	 * 
	 * @return {@code true} if and only if the property was read successfully
	 */
	private void processDelegates() {
		String p = properties.getProperty("deepsea.delegates");
		if (p != null) {
			String[] delegates = p.trim().split(";");
			for (String delegate : delegates) {
				String[] pair = delegate.split(":");
				Object to = createInstance(pair[1].trim());
				if (to != null) {
					addDelegate(pair[0].trim(), to);
				}
			}
		}
	}

	/**
	 * Reads and set the variable bounds when specified with the
	 * "{@code deepsea.bounds...}" settings.
	 * 
	 * @return {@code true} if and only if the property was read successfully
	 */
	private void processBounds() {
		for (Object key : properties.keySet()) {
			String k = (String) key;
			if (k.startsWith("deepsea.bounds.")) {
				String var = k.substring("deepsea.bounds.".length());
				if (var.endsWith(".min")) {
					Integer min = getIntegerProperty(properties, k);
					if (min != null) {
						setMinBound(var.substring(0, var.length() - 4), min);
					}
				} else if (var.endsWith(".max")) {
					Integer max = getIntegerProperty(properties, k);
					if (max != null) {
						setMaxBound(var.substring(0, var.length() - 4), max);
					}
				} else {
					String[] bounds = properties.getProperty(k).split("\\.\\.");
					assert bounds.length >= 2;
					try {
						setMinBound(var, Integer.parseInt(bounds[0].trim()));
						setMaxBound(var, Integer.parseInt(bounds[1].trim()));
					} catch (NumberFormatException x) {
						logger.warn("Bounds in \"" + k + "\" is malformed and ignored");
					}
				}
			}
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.echooutput}" setting.
	 */
	private void processEchoOutput() {
		setEchoOutput(getBooleanProperty(properties, "deepsea.echooutput", getEchoOutput()));
	}

	/**
	 * Reads and sets the "{@code deepsea.explorer}" setting.
	 */
	private void processExplorer() {
		String p = properties.getProperty("deepsea.explorer");
		if (p != null) {
			Explorer explorer = (Explorer) createInstance(p);
			if (explorer != null) {
				setExplorer(explorer);
			}
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.logfile}" setting.
	 */
	private void processLogfile() {
		String p = properties.getProperty("deepsea.logfile");
		if (p != null) {
			setLogfile(p);
		}
	}

	/**
	 * Reads and sets the "{@code deepsea.dumpconfig}" setting.
	 */
	private void processDumpConfig() {
		setDumpConfig(getBooleanProperty(properties, "deepsea.dumpconfig", getDumpConfig()));
	}

	/**
	 * Reads and sets the "{@code deepsea.dumpproperties}" setting.
	 */
	private void processDumpProperties() {
		setDumpProperties(getBooleanProperty(properties, "deepsea.dumpproperties", getDumpProperties()));
	}

	/**
	 * Reads and sets the "{@code deepsea.distributed}" setting.
	 */
	private void processDistributed() {
		setDistributed(getBooleanProperty(properties, "deepsea.distributed", getDistributed()));
	}

	/**
	 * Creates an instance of the specified class. For now, only one constructor
	 * pattern is tried to create an instance of {@code X}:
	 * {@code X(Logger, Configuration)}. In the future alternative constructors
	 * might be supported.
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
				constructor = classx.getConstructor(Logger.class, Configuration.class);
				return constructor.newInstance(logger, this);
			} catch (NoSuchMethodException x) {
				logger.fatal("constructor not found: " + objectName, x);
			}
		} catch (SecurityException x) {
			logger.fatal("constructor security error: " + objectName, x);
		} catch (IllegalArgumentException x) {
			logger.fatal("constructor argument error: " + objectName, x);
		} catch (InstantiationException x) {
			logger.fatal("constructor instantiation error: " + objectName, x);
		} catch (IllegalAccessException x) {
			logger.fatal("constructor access error: " + objectName, x);
		} catch (InvocationTargetException x) {
			logger.fatal("constructor invocation error: " + objectName, x);
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
				logger.fatal("class not found: " + className, x);
			} catch (ExceptionInInitializerError x) {
				logger.fatal("class not found: " + className, x);
			}
		}
		return null;
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

}
