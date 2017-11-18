package za.ac.sun.cs.deepsea.reporting;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class AbstractReporter implements Reporter {

	/**
	 * Time when work started.
	 */
	protected Calendar started;

	/**
	 * Time when work stopped.
	 */
	protected Calendar stopped;

	/**
	 * Private date formatter, used for reports.
	 */
	private static final DateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");

	/**
	 * Records the starting time for the reporter.
	 */
	public void recordStartingTime() {
		started = Calendar.getInstance();
	}

	/**
	 * Records the stopping time for the reporter.
	 */
	public void recordStoppingTime() {
		stopped = Calendar.getInstance();
	}

	/**
	 * Displays the starting and stopping times for the reporter.
	 * 
	 * @param out
	 *            the destination to which the report is written
	 */
	public void displayStartStopTimes(PrintWriter out) {
		out.println("Started: " + dateFormat.format(started.getTime()));
		out.println("Stopped: " + dateFormat.format(stopped.getTime()));
	}

	/**
	 * Display the duration for the reporter.
	 * 
	 * @param out
	 *            the destination to which the report is written
	 */
	public void displayDuration(PrintWriter out) {
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
	}

}
