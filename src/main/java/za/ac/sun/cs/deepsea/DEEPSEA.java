package za.ac.sun.cs.deepsea;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.reporting.Banner;

/**
 * Main class and launcher for the DEEPSEA project. It expects a single
 * command-line argument: the filename of a properties file. It creates a
 * logger, and an instance of {@link Configuration}, and uses these to create an
 * instance of {@link Diver}. It reads the settings from the properties file,
 * and runs the diver.
 */
public class DEEPSEA {

	public static final String VERSION = "in transition";
	
	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration();
		Logger LOGGER = config.getLogger(); // works even with partial Configuration
		if (args.length < 1) {
			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n")
					.println("USAGE: deepsea <properties file>").display(LOGGER, Level.INFO);
			return;
		}
		if (!config.processProperties(args[0])) {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"")
					.display(LOGGER, Level.INFO);
			return;
		}
		if (config.getTarget() == null) {
			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n")
					.println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(LOGGER, Level.INFO);
			return;
		}
		// Configuration has now been loaded and seems OK
		new Banner('~').println("DEEPSEA version " + VERSION).display(LOGGER, Level.INFO);
		Diver diver = new Diver("DEEPSEA", LOGGER, config);
		LOGGER.info("");
		diver.start();
		LOGGER.info("");
		new Banner('~').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
	}

}
