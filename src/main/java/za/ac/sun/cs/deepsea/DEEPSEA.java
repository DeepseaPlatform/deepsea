package za.ac.sun.cs.deepsea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.distributed.Master;
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

	/**
	 * The main function.
	 * 
	 * @param args
	 *            command-line arguments.
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration();
		if (args.length < 1) {
			new Banner('@').println("DEEPSEA PROBLEM\nMISSING PROPERTIES FILE\n")
					.println("USAGE: deepsea <properties file>").display(System.out);
			return;
		}
		if (!config.processProperties(args[0])) {
			new Banner('@').println("DEEPSEA PROBLEM\n").println("COULD NOT READ PROPERTY FILE \"" + args[0] + "\"")
					.display(System.out);
			return;
		}
		if (config.getTarget() == null) {
			new Banner('@').println("SUSPICIOUS PROPERTIES FILE\n")
					.println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(System.out);
			return;
		}
		// Configuration has now been loaded and seems OK
		Logger logger = config.getLogger();
		if (config.getDistributed()) {
			new Banner('#').println("DEEPSEA version " + getVersion() + " DISTRIBUTED").display(logger, Level.INFO);
			Master.executeJob(logger, config);
			new Banner('#').println("DEEPSEA DONE").display(logger, Level.INFO);
		} else {
			new Banner('~').println("DEEPSEA version " + getVersion()).display(logger, Level.INFO);
			Diver diver = new Diver("DEEPSEA", logger, config);
			logger.info("");
			diver.start();
			logger.info("");
			new Banner('~').println("DEEPSEA DONE").display(logger, Level.INFO);
		}
	}

	/**
	 * Tries to read the DEEPSEA version from a file.
	 * 
	 * @return a string that contains the version number of
	 *         "{@code unspecified}"
	 */
	private static String getVersion() {
		InputStream in = DEEPSEA.class.getResourceAsStream("/VERSION");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line = br.readLine();
			if (line != null) {
				return line;
			}
		} catch (IOException x) {
		}
		return "unspecified";
	}

}
