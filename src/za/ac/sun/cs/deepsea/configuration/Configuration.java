package za.ac.sun.cs.deepsea.configuration;

import java.util.Properties;
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
			String[] ts = p.trim().split(";");
			for (String t : ts) {
				int i = t.indexOf('(');
				if (i == -1) {
					String methodName = t.trim();
					diver.addTrigger(new Trigger(methodName));
				} else {
					String methodName = t.substring(0, i - 1).trim();
					diver.addTrigger(new Trigger(methodName));
				}
			}
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
