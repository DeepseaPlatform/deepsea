package za.ac.sun.cs.deepsea;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		dv.start();
	}

}