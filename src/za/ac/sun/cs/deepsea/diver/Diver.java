package za.ac.sun.cs.deepsea.diver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.Map;
//import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.Method;

import za.ac.sun.cs.deepsea.explorer.Explorer;
import za.ac.sun.cs.deepsea.logging.LogHandler;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Driver for dynamic symbolic execution. It collects all the settings that
 * apply to a DEEPSEA "session" and when the {@link #start()} method is called,
 * it conducts one or more "dives" into the target program.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
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
	 * A counter of how many {@link Dive} instances have been created.
	 */
	private int diveCounter;

	/**
	 * The fully qualified of the target class. This class should contain a Java
	 * {@code main} method and it is the class that is run during each dive.
	 */
	private String target = null;

	/**
	 * Arguments pass to the target class when it is run during a dive.
	 */
	private String args = null;

	/**
	 * The database of triggers. Each trigger is a method that will switch the
	 * dive to symbolic mode. The trigger also describes which arguments are
	 * treated symbolically, and which arguments stay concrete.
	 */
	private List<Trigger> triggers = new LinkedList<>();

	/**
	 * Whether or not the output of the target program should be displayed
	 * ({@code true}) or suppressed ({@code false}).
	 */
	private boolean produceOutput = false;

	/**
	 * The Explorer that directs the investigation of target programs.
	 */
	private Explorer explorer;

	/**
	 * Constructs a {@link Diver} instance. Such an instance represents one
	 * "session" of DEEPSEA.
	 * 
	 * @param name
	 *            the name for this instance
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
	 * Returns the current value of the dive counter and increments it.
	 * 
	 * @return the current value of dive counter
	 */
	public int getDiveId() {
		return diveCounter++;
	}

	/**
	 * Returns the target class.
	 * 
	 * @return the fully qualified target class
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target class.
	 * 
	 * @param target
	 *            the fully qualified target class
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
	 * Finds the first trigger that matches the given method and class. If no
	 * matching trigger is found, the method returns {@code null}.
	 * 
	 * @param method
	 *            the method to match
	 * @param className
	 *            the name of the class to match
	 * @return the first matching trigger or {@code null}
	 */
	public Trigger findTrigger(Method method, String className) {
		for (Trigger trigger : triggers) {
			if (trigger.match(className, method)) {
				return trigger;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public boolean isProducingOutput() {
		return produceOutput;
	}

	/**
	 * @param produceOutput
	 */
	public void produceOutput(boolean produceOutput) {
		this.produceOutput = produceOutput;
	}

	/**
	 * Returns the current explorer for this session.
	 * 
	 * @return the current instance of explorer
	 */
	public Explorer getExplorer() {
		return explorer;
	}

	/**
	 * Sets a new explorer for this session. Note that there is ever only
	 * explorer for a session.
	 * 
	 * @param explorer
	 *            the new explorer
	 */
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
	}

	/**
	 * Run the diver.
	 */
	public void start() {
		if (explorer == null) {
			log.severe("No explorer specified -- terminating");
		} else {
			Map<String, Constant> concreteValues = null;
			do {
				Dive d = new Dive(this, concreteValues);
				d.dive();
				concreteValues = explorer.refine(d);
			} while (concreteValues != null);
			explorer.report();
		}
		log.info("DONE");
	}

}
