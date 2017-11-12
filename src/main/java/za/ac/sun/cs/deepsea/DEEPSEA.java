package za.ac.sun.cs.deepsea;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.logging.log4j.Level;

import za.ac.sun.cs.deepsea.configuration.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.reporting.Banner;

/**
 * Main class and launcher for the DEEPSEA project. It expects a single
 * command-line argument: the filename of a properties file. The properties are
 * processed by an instance of {@link Configuration} which is in turn linked to
 * an instance of {@link Diver}.  Once configured, the diver is run. 
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class DEEPSEA {

	/**
	 * The main function.
	 * 
	 * @param args command-line arguments.
	 */
	public static void main(String[] args) {
		Properties pr = new Properties();
		try {
			pr.load(new FileInputStream(args[0]));
		} catch (FileNotFoundException x) {
			x.printStackTrace();
		} catch (IOException x) {
			x.printStackTrace();
		}
		Diver dv = new Diver("DEEPSEA");
		new Configuration(dv, pr).apply();
		new Banner('~').println("DEEPSEA version " + getVersion()).display(dv.getLog(), Level.INFO);
		dv.getLog().info("");
		dv.start();
		dv.getLog().info("");
		new Banner('~').println("DEEPSEA DONE").display(dv.getLog(), Level.INFO);
	}

	private static String getVersion() {
		InputStream in = DEEPSEA.class.getResourceAsStream("/VERSION");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line = br.readLine();
			if  (line != null) {
				return line;
			}
		} catch (IOException x) {
		}
		return "unspecified";
	}

}
