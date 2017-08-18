package za.ac.sun.cs.deepsea.explorer;

import java.util.Map;

import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Controls how DEEPSEA "explores" a target program by repeatedly proposing new
 * concrete inputs in response to the path conditions encountered during
 * previous runs.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public interface Explorer extends Reporter {

	/**
	 * Return the name of this explorer.
	 * 
	 * @return the name of the explorer
	 */
	public String getName();

	/**
	 * Propose new concrete values in response to a newly discovered path
	 * condition.
	 * 
	 * DEEPSEA will create a singleton instance of this class. The "constrained"
	 * run of the target program will produce an initial path condition. This
	 * path condition is passed to the {@link Explorer} which is expected to
	 * propose concrete values for symbolic variables; the values will be used
	 * during the next invocation of the target program.
	 *
	 * The concrete values takes the form of a mapping from variable names to
	 * Green constants. The variable names correspond to the names given in the
	 * "{@code deepsea.triggers}" setting in the properties file that defines
	 * the parameter of the DEEPSEA run.
	 * 
	 * Given such a mapping, DEEPSEA will perform another invocation of the
	 * target program, again and again, until the {@link #refine(Expression)}
	 * method returns {@code null}. DEEPSEA does not keep a record of path
	 * conditions or of mappings and it is only this method which can determine
	 * when to terminate the exploration.
	 * 
	 * @param dive
	 *            the newly completed dive
	 * @return a mapping from variables names to values, or {@code null}
	 */
	public Map<String, Constant> refine(Dive dive);

}
