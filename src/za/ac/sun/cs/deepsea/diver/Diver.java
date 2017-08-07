package za.ac.sun.cs.deepsea.diver;

import java.util.LinkedList;
import java.util.List;
//import java.util.Map;
//import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jdi.Method;

import za.ac.sun.cs.deepsea.logging.LogHandler;
//import za.ac.sun.cs.green.Green;
//import za.ac.sun.cs.green.Instance;
//import za.ac.sun.cs.green.expr.IntVariable;
//import za.ac.sun.cs.green.util.Configuration;

/**
 * Driver for dynamic symbolic execution.
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
//		Green solver = new Green("DEEPSEA");
//		Properties props = new Properties();
//		props.setProperty("green.services", "model");
//		props.setProperty("green.service.model", "(bounder z3java)");
//		props.setProperty("green.service.model.bounder", "za.ac.sun.cs.green.service.bounder.BounderService");				
//		props.setProperty("green.service.model.z3java", "za.ac.sun.cs.green.service.z3.ModelZ3JavaService");
//		// props.setProperty("", "/Users/jaco/Documents/RESEARCH/01/SYMEXE/Z3/build/z3");
//		Configuration config = new Configuration(solver, props);
//		config.configure();

		Dive d = new Dive(this);
		d.dive();
//		Instance instance = new Instance(solver, null, d.getPathCondition());
//		@SuppressWarnings({ "unchecked", "unused" })
//		Map<IntVariable,Object> model = (Map<IntVariable,Object>) instance.request("model"); 
		log.info("%%%% " + d.getPathCondition());
		log.info("Done.");
	}

}
