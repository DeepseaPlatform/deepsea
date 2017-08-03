package za.ac.sun.cs.deepsea.configuration;

import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.diver.Diver;
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

	/**
	 * 
	 */
	public void apply() {
		setLevel();
		setTarget();
		setArgs();
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

}
