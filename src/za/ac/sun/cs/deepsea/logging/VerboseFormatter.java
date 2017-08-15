package za.ac.sun.cs.deepsea.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Customized log message formatting.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class VerboseFormatter extends Formatter {

	/**
	 * A private instance used for formatting timestamps.
	 */
	private static Date date = new Date();

	/**
	 * 
	 */
	private String format = "[%1$tY%1$tm%1$td-%1$tH:%1$tM:%1$tS %2$s %4$s] %5$s%6$s%n";

	/**
	 * Formats the given log record and return the formatted string.
	 * 
	 * @param record
	 *            the log record to format
	 */
	@Override
	public synchronized String format(LogRecord record) {
		date.setTime(record.getMillis());
		StringBuffer source = new StringBuffer();
		if (record.getSourceClassName() != null) {
			source.append(record.getSourceClassName());
			if (record.getSourceMethodName() != null) {
				source.append("::");
				source.append(record.getSourceMethodName());
			}
		} else {
			source.append(record.getLoggerName());
		}
		String message = formatMessage(record);
		String throwable = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println();
			record.getThrown().printStackTrace(pw);
			pw.close();
			throwable = sw.toString();
		}
		return String.format(format, date, source.toString().replace("za.ac.sun.cs.deepsea.", ""),
				record.getLoggerName(), record.getLevel().getLocalizedName(), message, throwable);
	}

}
