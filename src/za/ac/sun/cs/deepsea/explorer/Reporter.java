package za.ac.sun.cs.deepsea.explorer;

import java.io.PrintWriter;

public interface Reporter {

	public String getName();
	
	public void report(PrintWriter out);

}
