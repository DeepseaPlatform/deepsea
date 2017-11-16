package za.ac.sun.cs.deepsea.reporting;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class with static routine to display a banner.
 */
public class Banner {

	/**
	 * Line separator.
	 */
	private static final String LS = System.getProperty("line.separator");

	/**
	 * Width of the banner.
	 */
	private static final int WIDTH = 70;

	/**
	 * Width of the side of the banner.
	 */
	private static final int SIDE_WIDTH = 4;
	
	/**
	 * Width of the spaces on the side of the banner.
	 */
	private static final int SIDE_SPACE = 2;
	
	/**
	 * Collector of banner content.
	 */
	protected final StringWriter bannerWriter = new StringWriter();
	
	/**
	 * Horizontal banner line.
	 */
	private final String borderLine;
	
	/**
	 * Left-hand side of middle of banner.
	 */
	private final String borderLeft;
	
	/**
	 * Right-hand side of middle of banner.
	 */
	private final String borderRight;
	
	/**
	 * Empty line in middle of banner.
	 */
	private final String borderEmpty;
	
	/**
	 * Auxiliary buffer used to store the banner.
	 */
	private final StringBuilder sb = new StringBuilder();
	
	/**
	 * Constructs the banner.  The only task to initialize the border lines.
	 * 
	 * @param borderChar the character used to construct the banner
	 */
	public Banner(char borderChar) {
		// Construct "====================="
		sb.setLength(0);
		while (sb.length() < WIDTH) { sb.append(borderChar); }
		borderLine = sb.toString();
		// Construct "====  "
		sb.setLength(0);
		for (int i = 0; i < SIDE_WIDTH; i++) { sb.append(borderChar); }
		for (int i = 0; i < SIDE_SPACE; i++) { sb.append(' '); }
		borderLeft = sb.toString();
		// Construct "  ===="
		sb.setLength(0);
		for (int i = 0; i < SIDE_SPACE; i++) { sb.append(' '); }
		for (int i = 0; i < SIDE_WIDTH; i++) { sb.append(borderChar); }
		borderRight = sb.toString();
		// Construct "====             ===="
		sb.setLength(0);
		sb.append(borderLeft);
		while (sb.length() < WIDTH - SIDE_WIDTH - SIDE_SPACE) { sb.append(' '); }
		sb.append(borderRight);
		borderEmpty = sb.toString();
	}
	
	/**
	 * Adds a message to the banner.
	 * 
	 * @param message banner content
	 * @return the banner instance
	 */
	public Banner println(String message) {
		sb.setLength(0);
		sb.append(borderLeft).append(message);
		if (sb.length() <= WIDTH - SIDE_WIDTH - SIDE_SPACE) {
			while (sb.length() < WIDTH - SIDE_WIDTH - SIDE_SPACE) { sb.append(' '); }
			sb.append(borderRight);
		}
		bannerWriter.append(sb.append(LS).toString());
		return this;
	}

	/**
	 * Displays the banner to the given log with the given level.
	 * 
	 * @param log destination log for the banner
	 * @param level log level for the banner
	 */
	public void display(Logger log, Level level) {
		log.log(level, borderLine);
		log.log(level, borderLine);
		log.log(level, borderEmpty);
		for (String line : bannerWriter.toString().split(LS)) {
			log.log(level, line);
		}
		log.log(level, borderEmpty);
		log.log(level, borderLine);
		log.log(level, borderLine);
	}

	/**
	 * Displays the banner to the given writer.
	 * 
	 * @param out destination for the banner
	 */
	public void display(PrintWriter out) {
		out.println(borderLine + "\n" + borderLine + "\n" + borderEmpty);
		for (String line : bannerWriter.toString().split(LS)) {
			out.println(line);
		}
		out.println(borderEmpty + "\n" + borderLine + "\n" + borderLine);
	}
	
	/**
	 * Displays the banner to the given stream.
	 * 
	 * @param out destination for the banner
	 */
	public void display(PrintStream out) {
		out.println(borderLine + "\n" + borderLine + "\n" + borderEmpty);
		for (String line : bannerWriter.toString().split(LS)) {
			out.println(line);
		}
		out.println(borderEmpty + "\n" + borderLine + "\n" + borderLine);
	}

}
