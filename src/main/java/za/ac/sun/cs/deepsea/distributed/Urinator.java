package za.ac.sun.cs.deepsea.distributed;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.explorer.Explorer;
import za.ac.sun.cs.deepsea.reporting.AbstractReporter;
import za.ac.sun.cs.deepsea.reporting.Banner;
import za.ac.sun.cs.deepsea.reporting.Reporter;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Driver for dynamic symbolic execution. When the {@link #start()} method is
 * called, it conducts one or more "dives" into the target program.
 */
public class Urinator extends AbstractReporter {

	/**
	 * The name of this instance of {@link Urinator}.
	 */
	private final String name;

	/**
	 * The logger. This is passed to dives.
	 */
	private final Logger logger;

	/**
	 * The settings that control and apply to this session.
	 */
	private final Configuration config;

	/**
	 * A list of all reports that must be called when the session terminates.
	 */
	private final List<Reporter> reporters = new LinkedList<>();

	/**
	 * Constructs a {@link Urinator} instance. Such an instance represents one
	 * "session" of DEEPSEA.
	 * 
	 * @param name
	 *            the name for this instance
	 * @param logger
	 *            the logger for this session
	 * @param config
	 *            the settings for this session
	 */
	public Urinator(final String name, Logger logger, Configuration config) {
		this.name = name;
		this.logger = logger;
		this.config = config;
		addReporter(this); // the diver is a reporter itself
	}

	/**
	 * Returns the name of this {@link Urinator}.
	 * 
	 * @return the name of this instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add a reporter to the list of reporters that are called at the end of
	 * this session.
	 * 
	 * @param reporter
	 *            the reporter to add
	 */
	public void addReporter(Reporter reporter) {
		reporters.add(0, reporter);
	}

	/**
	 * Run the diver.
	 */
	public void start() {
		Explorer explorer = config.getExplorer();
		if (explorer == null) {
			logger.fatal("No explorer specified -- terminating");
		} else {
			config.dumpConfig();
			// config.dumpProperties();
			addReporter(explorer);
			recordStartingTime();
			int diveCounter = 0;
			Map<String, Constant> concreteValues = null;
			do {
				Dive d = new Dive(name + "." + diveCounter++, logger, config, concreteValues);
				if (!d.dive()) {
					return; // A serious error has occurred.
				}
				concreteValues = explorer.refine(d);
				break;
			} while (concreteValues != null);
			recordStoppingTime();
			invokeReporters();
		}
	}

	/**
	 * Reports on the session just completed.  In this case, it merely prints the starting and stopping time, and the duration of the run.
	 * 
	 * @param out
	 *            the destination to which the report must be written
	 */
	public void report(PrintWriter out) {
		displayStartStopTimes(out);
		displayDuration(out);
	}

	//======================================================================
	//
	// Routines for invoking all reporters.
	//
	//======================================================================

	/**
	 * Give each reporter a chance to report. Note that reporters are called on
	 * in the *reverse* order of their registration.
	 */
	private void invokeReporters() {
		for (Reporter reporter : reporters) {
			report(reporter);
		}
	}

	/**
	 * Line separator
	 */
	private static final String LS = System.getProperty("line.separator");

	/**
	 * Generates and log the exporer's report.
	 * 
	 * @param reporter 
	 */
	private void report(Reporter reporter) {
		Banner.displayBannerLine(reporter, '=', logger);
		final StringWriter reportWriter = new StringWriter();
		reporter.report(new PrintWriter(reportWriter));
		String[] reportLines = reportWriter.toString().split(LS);
		for (String line : reportLines) {
			logger.info(line);
		}
	}

}
