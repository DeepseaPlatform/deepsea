package za.ac.sun.cs.deepsea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.Configuration;
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
		Logger logger = LogManager.getLogger(DEEPSEA.class);
		Configuration config = new Configuration(logger);
		Diver diver = new Diver("DEEPSEA", logger, config);
		new Banner('~').println("DEEPSEA version " + getVersion()).display(logger, Level.INFO);
		if (args.length < 1) {
			new Banner('@').println("MISSING PROPERTIES FILE").println("").println("USAGE: deepsea <properties file>")
					.display(logger, Level.FATAL);
		} else {
			config.processProperties(args[0]);
			if (config.getTarget() != null) {
				logger.info("");
				diver.start();
				logger.info("");
			} else {
				new Banner('@').println("SUSPICIOUS PROPERTIES FILE").println("")
						.println("ARE YOU SURE THAT THE ARGUMENT IS A .properties FILE?").display(logger, Level.FATAL);
			}
		}
		new Banner('~').println("DEEPSEA DONE").display(logger, Level.INFO);
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
