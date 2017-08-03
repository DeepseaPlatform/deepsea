package za.ac.sun.cs.deepsea.diver;

import java.util.logging.Level;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.logging.SEALogHandler;

/**
 * Driver for dynamic symbolic execution.
 * 
 * @author Jaco Geldenhuys <geld@sun.ac.za>
 */
public class Diver {

	/**
	 * The name of this instance of {@link Diver}.
	 */
	private final String name;

	/**
	 * The log associated with this {@link Diver} instance.
	 */
	private final Logger log;

	/**
	 * 
	 */
	private int diveCounter;

	/**
	 * 
	 */
	private String target = null;

	/**
	 * 
	 */
	private String args = null;
	
	/**
	 * Constructs a {@link Diver} instance.
	 * 
	 * @param name the name for this instance
	 */
	public Diver(final String name) {
		this.name = name;
		this.log = Logger.getLogger(getClass().getCanonicalName() + "[" + name + "]");
		log.setUseParentHandlers(false);
		log.setLevel(Level.ALL);
		log.addHandler(new SEALogHandler(Level.ALL));
		diveCounter = 0;
	}

	/**
	 * Returns the name of this {@link Diver}.
	 * 
	 * @return the name of this instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the {@link Logger} associated with this instance of {@link Diver}.
	 * 
	 * @return the log associated with this instance
	 */
	public Logger getLog() {
		return log;
	}

	/**
	 * @return
	 */
	public int getDiveId() {
		return diveCounter++;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	public String getArgs() {
		return args;
	}
	
	public void setArgs(String args) {
		this.args = args;
	}
	
	/**
	 * Run the diver.  
	 */
	public void start() {
		// Dive d = new Dive(this);
		new Dive(this).dive();
	}

}
