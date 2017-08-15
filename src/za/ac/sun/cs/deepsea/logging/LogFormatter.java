package za.ac.sun.cs.deepsea.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Customized log message formatting.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class LogFormatter extends Formatter {

	/**
	 * 
	 */
	private String format = "[%1$s] %2$s%3$s%n";

	/**
	 * Formats the given log record and return the formatted string.
	 * 
	 * @param record
	 *            the log record to format
	 */
	@Override
	public synchronized String format(LogRecord record) {
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}
		return String.format(format, record.getLevel().getLocalizedName(), formatMessage(record), throwable);
	}

}
