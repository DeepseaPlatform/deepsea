package za.ac.sun.cs.deepsea.reporting;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Banner {

	/**
	 * Line separator
	 */
	private static final String LS = System.getProperty("line.separator");

	protected final StringWriter bannerWriter = new StringWriter();
	
	private final String borderLine;
	
	private final String borderLeft;
	
	private final String borderRight;
	
	private final String borderEmpty;
	
	private final StringBuilder sb = new StringBuilder();
	
	public Banner(char borderChar) {
		// Construct "====================="
		sb.setLength(0);
		while (sb.length() < 70) { sb.append(borderChar); }
		borderLine = sb.toString();
		// Construct "====  "
		sb.setLength(0);
		for (int i = 0; i < 4; i++) { sb.append(borderChar); }
		for (int i = 0; i < 2; i++) { sb.append(' '); }
		borderLeft = sb.toString();
		// Construct "  ===="
		sb.setLength(0);
		for (int i = 0; i < 2; i++) { sb.append(' '); }
		for (int i = 0; i < 4; i++) { sb.append(borderChar); }
		borderRight = sb.toString();
		// Construct "====             ===="
		sb.setLength(0);
		sb.append(borderLeft);
		while (sb.length() < 64) { sb.append(' '); }
		sb.append(borderRight);
		borderEmpty = sb.toString();
	}
	
	public Banner println(String message) {
		sb.setLength(0);
		sb.append(borderLeft).append(message);
		if (sb.length() <= 64) {
			while (sb.length() < 64) { sb.append(' '); }
			sb.append(borderRight);
		}
		bannerWriter.append(sb.append(LS).toString());
		return this;
	}

	public void display(PrintWriter out) {
		out.println(borderLine);
		out.println(borderLine);
		out.println(borderEmpty);
		for (String line : bannerWriter.toString().split(LS)) {
			out.println(line);
		}
		out.println(borderEmpty);
		out.println(borderLine);
		out.println(borderLine);
	}
	
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

}
