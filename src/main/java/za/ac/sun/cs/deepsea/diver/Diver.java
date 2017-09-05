package za.ac.sun.cs.deepsea.diver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jdi.Method;

import za.ac.sun.cs.deepsea.Reporter;
import za.ac.sun.cs.deepsea.explorer.Explorer;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Driver for dynamic symbolic execution. It collects all the settings that
 * apply to a DEEPSEA "session" and when the {@link #start()} method is called,
 * it conducts one or more "dives" into the target program.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class Diver implements Reporter {

	/**
	 * The minimum value that integer variables can assume by default.
	 */
	private static final int DEFAULT_MIN_INT_VALUE = 0;

	/**
	 * The maximum value that integer variables can assume by default.
	 */
	private static final int DEFAULT_MAX_INT_VALUE = 99;
	
	/**
	 * The name of this instance of {@link Diver}.
	 */
	private final String name;

	/**
	 * The log handler associated with this {@link Diver} instance.
	 */
	// private final LogHandler logHandler;

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
	private final List<Trigger> triggers = new LinkedList<>();

	/**
	 * Maps variable names to lower bounds. 
	 */
	private final Map<String, Integer> minBounds = new HashMap<>();

	/**
	 * Maps variable names to upper bounds. 
	 */
	private final Map<String, Integer> maxBounds = new HashMap<>();
	
	/**
	 * Whether or not the output of the target program should be displayed
	 * ({@code true}) or suppressed ({@code false}).
	 */
	private boolean produceOutput = false;

	/**
	 * The Explorer that directs the investigation of target programs.
	 */
	private Explorer explorer;

	private List<Reporter> reporters = new LinkedList<>();

	/**
	 * Constructs a {@link Diver} instance. Such an instance represents one
	 * "session" of DEEPSEA.
	 * 
	 * @param name
	 *            the name for this instance
	 */
	public Diver(final String name) {
		this.name = name;
		this.log = LogManager.getLogger(Diver.class);
		//this.log = Logger.getLogger(getClass().getCanonicalName() + "[" + name + "]");
		// log.setUseParentHandlers(false);
		// log.setLevel(Level.ALL);
		// logHandler = new LogHandler(Level.ALL);
		// log.addHandler(logHandler);
		diveCounter = 0;
		addReporter(this);
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
	 * Return the {@link LogHandler} associated with this instance of
	 * {@link Diver}.
	 * 
	 * @return the log handler associated with this instance
	 */
//	public LogHandler getLogHandler() {
//		return logHandler;
//	}

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

	public void addReporter(Reporter reporter) {
		reporters.add(0, reporter);
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
	 * Sets the minimum value of a variable.
	 * 
	 * @param variable the variable name
	 * @param min the minimum value
	 */
	public void setMinBound(String variable, int min) {
		minBounds.put(variable, min);
	}

	/**
	 * Returns the minimum value associated with a variable.
	 * 
	 * @param variable the variable name
	 * @return the minimum value that the integer variable can assume
	 */
	public int getMinBound(String variable) {
		Integer min = minBounds.get(variable);
		if (min == null) {
			min = DEFAULT_MIN_INT_VALUE;
		}
		return min;
	}

	/**
	 * Sets the maximum value of a variable.
	 * 
	 * @param variable the variable name
	 * @param max the maximum value
	 */
	public void setMaxBound(String variable, int max) {
		maxBounds.put(variable, max);
	}

	/**
	 * Returns the maximum value associated with a variable.
	 * 
	 * @param variable the variable name
	 * @return the maximum value that the integer variable can assume
	 */
	public int getMaxBound(String variable) {
		Integer max = maxBounds.get(variable);
		if (max == null) {
			max = DEFAULT_MAX_INT_VALUE;
		}
		return max;
	}

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
	 * Time when work started.
	 */
	private Calendar started;

	/**
	 * Time when work stopped.
	 */
	private Calendar stopped;

	/**
	 * Run the diver.
	 */
	public void start() {
		if (explorer == null) {
			log.fatal("No explorer specified -- terminating");
		} else {
			started = Calendar.getInstance();
			Map<String, Constant> concreteValues = null;
			do {
				Dive d = new Dive(this, concreteValues);
				d.dive();
				concreteValues = explorer.refine(d);
			} while (concreteValues != null);
			stopped = Calendar.getInstance();
			/*
			 * Give each reporter a chance to report. Note that reporters are
			 * called on in the *reverse* order of their registration.
			 */
			for (Reporter reporter : reporters) {
				report(reporter);
			}
		}
	}

	public void report(PrintWriter out) {
		out.println("Started: " + dateFormat.format(started.getTime()));
		out.println("Stopped: " + dateFormat.format(stopped.getTime()));
		long diff = stopped.getTimeInMillis() - started.getTimeInMillis();
		long milli = diff % 1000;
		long sec = (diff / 1000) % 60;
		long min = (diff / 60000) % 60;
		long hour = diff / 3600000;
		if (hour > 0) {
			out.printf("Duration: %d:%02d:%02d.%03d hrs\n", hour, min, sec, milli);
		} else if (min > 0) {
			out.printf("Duration: %d:%02d.%03d min\n", min, sec, milli);
		} else if (sec > 0) {
			out.printf("Duration: %d.%03d sec\n", sec, milli);
		} else {
			out.printf("Duration: %d ms\n", milli);
		}
		out.println("~~~ DONE ~~~");
	}

	/**
	 * Line separator
	 */
	static final String LS = System.getProperty("line.separator");

	/**
	 * Private date formatter, used for reports.
	 */
	private static final DateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");

	/**
	 * Generate and log the exporer's report.
	 * 
	 * @param reporter
	 */
	private void report(Reporter reporter) {
		log.info("");
		log.info("======================================================================");
		log.info("== " + reporter.getName());
		final StringWriter reportWriter = new StringWriter();
		reporter.report(new PrintWriter(reportWriter));
		String[] reportLines = reportWriter.toString().split(LS);
		for (String line : reportLines) {
			log.info(line);
		}
	}
	
	
}
