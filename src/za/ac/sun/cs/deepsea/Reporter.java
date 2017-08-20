package za.ac.sun.cs.deepsea;

import java.io.PrintWriter;

/**
 * Contract that is followed by all component that writes a report at the end of
 * a DEEPSEA run.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public interface Reporter {

	/**
	 * Returns a name for this reporter.
	 * 
	 * @return the name of the reporter
	 */
	public String getName();

	/**
	 * Writes a report that summarizes the execution of a component at the end
	 * of a DEEPSEA run.
	 * 
	 * @param out
	 *            the destination to which the report must be written
	 */
	public void report(PrintWriter out);

}
