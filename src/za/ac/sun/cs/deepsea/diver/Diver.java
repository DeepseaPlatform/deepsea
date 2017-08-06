package za.ac.sun.cs.deepsea.diver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import za.ac.sun.cs.deepsea.logging.LogHandler;

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
	 * 
	 */
	private List<Trigger> triggers = new LinkedList<>();

	/**
	 * 
	 */
	private boolean produceOutput = false;

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
		log.addHandler(new LogHandler(Level.ALL));
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

	/**
	 * @return
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * @return
	 */
	public String getArgs() {
		return args;
	}
	
	/**
	 * @param args
	 */
	public void setArgs(String args) {
		this.args = args;
	}

	/**
	 * @param trigger
	 */
	public void addTrigger(Trigger trigger) {
		triggers.add(trigger);
	}
	
	/**
	 * @param method
	 * @return
	 */
	public Iterator<Trigger> findTriggers(int method, String className) {
		return triggers.stream().filter(tr -> false).iterator();
//		final String n = className + "." + method.getName();
//		return triggers.stream().filter(tr -> {
//			if (!tr.getName().equals(n)) {
//				return false;
//			}
//			return true;
//		}).iterator();
	}
	
	/**
	 * @return
	 */
	public boolean isProducintOutput() {
		return produceOutput;
	}
	
	/**
	 * @param produceOutput
	 */
	public void produceOutput(boolean produceOutput) {
		this.produceOutput = produceOutput;
	}
	
	/**
	 * Run the diver.  
	 */
	public void start() {
		// Dive d = new Dive(this);
		new Dive(this).dive();
	}

}
