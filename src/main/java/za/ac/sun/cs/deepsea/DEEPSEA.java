package za.ac.sun.cs.deepsea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import za.ac.sun.cs.deepsea.configuration.Configuration;
import za.ac.sun.cs.deepsea.diver.Diver;

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
		dv.getLog().info("");
		dv.getLog().info("~~~ DEEPSEA version {} ~~~", getVersion());
		dv.getLog().info("");
		dv.start();
		dv.getLog().info("");
		dv.getLog().info("~~~ DEEPSEA DONE ~~~");
		dv.getLog().info("");
	}

	private static String getVersion() {
		ClassLoader classLoader = DEEPSEA.class.getClassLoader();
		URL url = classLoader.getResource("VERSION");
		if (url == null) {
			return "unspecified";
		}
		File file = new File(url.getFile());
		if (!file.exists()) {
			return "unspecified";
		}
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			if  (line != null) {
				return line;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return "unspecified";
	}

}
