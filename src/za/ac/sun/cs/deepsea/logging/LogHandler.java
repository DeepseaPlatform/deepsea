package za.ac.sun.cs.deepsea.logging;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * DEEPSEA log handler that outputs all log messages to the standard output.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class LogHandler extends StreamHandler {

	/**
	 * The terse formatter used by this {@link LogHandler}.
	 * Note that it is static and therefore shared by all instances.
	 */
	private static final Formatter logFormatter = new LogFormatter();

	/**
	 * The verbose formatter used by this {@link LogHandler}.
	 * Note that it is static and therefore shared by all instances.
	 */
	private static final Formatter verboseFormatter = new VerboseFormatter();

	/**
	 * Constructs a {@link StreamHandler} that publishes log records to
	 * {@link System#out} using {@link LogFormatter} to reformat the message.
	 */
	public LogHandler() {
		super(System.out, logFormatter);
	}

	/**
	 * Constructs a {@link StreamHandler} that publishes log records to
	 * {@link System#out} using {@link LogFormatter} to reformat the message,
	 * and setting the handler's level to {@code level}.
	 * 
	 * @param level
	 *            the log level
	 */
	public LogHandler(Level level) {
		super(System.out, logFormatter);
		setLevel(level);
	}

	/**
	 * Switches to either the verbose of the terse formatter, based on the parameter.
	 * 
	 * @param isVerbose flag to select verbose ({@code true}) or terse ({@code false}) formatter
	 */
	public void setVerbose(boolean isVerbose) {
		if (isVerbose) {
			setFormatter(verboseFormatter);
		} else {
			setFormatter(logFormatter);
		}
	}

	/**
	 * Switches to the verbose formatter.
	 */
	public void setVerbose() {
		setVerbose(true);
	}

	/**
	 * Switches to the terse formatter.
	 */
	public void setTerse() {
		setVerbose(false);
	}

	/**
	 * Forces any data that may have been buffered to the underlying output
	 * device, but does <i>not</i> close {@link System#out}.
	 */
	@Override
	public void close() {
		flush();
	}

	/**
	 * Publishes a {@link LogRecord} to the console, provided the record passes
	 * all tests for being loggable. Most applications do not need to call this
	 * method directly. Instead, they will use use a {@link Logger}, which will
	 * create LogRecords and distribute them to registered handlers.
	 * 
	 * @param record
	 *            the log event to be published.
	 */
	@Override
	public void publish(LogRecord record) {
		super.publish(record);
		flush();
	}

}
