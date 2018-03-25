package za.ac.sun.cs.deepsea.distributed;

import java.lang.management.ManagementFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.BuildConfig;
import za.ac.sun.cs.deepsea.distributed.Master;
import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.reporting.Banner;

/**
 * Master controller for the DEEPSEA project distributed version. It expects a single
 * command-line argument: the filename of a properties file. It creates a
 * logger, and an instance of {@link Configuration}, and uses these to create an
 * instance of {@link Diver}. It reads the settings from the properties file,
 * and runs the diver.
 */
public class Master {

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 * @throws InterruptedException
	 *             if the 1-second delay is interrupted
	 */
	public static void main(String[] args) throws InterruptedException {
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		Logger LOGGER = LogManager.getLogger(jvmName);
		if (args.length < 1) {
			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n").println("USAGE: deepsea <properties file>").display(LOGGER, Level.FATAL);
			return;
		}
		Configuration config = new Configuration();
		if (!config.processProperties(args[0])) {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"").display(LOGGER, Level.FATAL);
			return;
		}
		if (config.getTarget() == null) {
			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n")
			.println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(System.out);
			return;
		}
		// Configuration has now been loaded and seems OK
		new Banner('#').println("DEEPSEA version " + BuildConfig.VERSION + " DISTRIBUTED MASTER").display(LOGGER, Level.INFO);
		LOGGER.info("");
		//Urinator urinator = new Urinator("DEEPSEA", logger, config);
		//urinator.start();
		LOGGER.info("");
		new Banner('#').println("DEEPSEA DONE").display(LOGGER, Level.INFO);
	}

}
