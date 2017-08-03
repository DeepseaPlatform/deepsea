package za.ac.sun.cs.deepsea;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import za.ac.sun.cs.deepsea.diver.Diver;

/**
 * Main class and launcher for the DEEPSEA project. It
 * 
 * <ul>
 * <li>creates a JVM to run the target program in,</li>
 * <li>redirects its input and error streams to the standard output and standard error,</li>
 * <li>sets up the monitoring requests,</li>
 * <li>sets up the event monitoring,</li>
 * <li>activates the JVM,</li>
 * <li>waits for the action to dissipate, and</li>
 * <li>shuts down everything.</li> 
 * </ul>
 * 
 * @author jaco
 */
public class DEEPSEA {

	/**
	 * The main function.
	 * 
	 * TODO: command-line arguments are still in a state of flux
	 * 
	 * @param args
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
		dv.start();
	}

}
