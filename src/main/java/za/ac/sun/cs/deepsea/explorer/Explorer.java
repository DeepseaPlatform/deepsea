package za.ac.sun.cs.deepsea.explorer;

import java.util.Map;

import za.ac.sun.cs.deepsea.diver.Dive;
import za.ac.sun.cs.deepsea.reporting.Reporter;
import za.ac.sun.cs.green.expr.Constant;

/**
 * Controls how a target program is explored by repeatedly proposing new
 * concrete inputs in response to the path conditions encountered during
 * previous runs.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public interface Explorer extends Reporter {

	/**
	 * Returns the name of this explorer.
	 * 
	 * @return the name of the explorer
	 */
	public String getName();

	/**
	 * Proposes new concrete values in response to a newly discovered path
	 * condition.
	 * 
	 * <p>
	 * DEEPSEA will create a singleton instance of this class. The "constrained" run
	 * of the target program will produce an initial path condition. This path
	 * condition is passed to the {@link Explorer} which is expected to propose
	 * concrete values for symbolic variables; the values will be used during the
	 * next invocation of the target program.
	 * </p>
	 * 
	 * <p>
	 * Perhaps the most important support routine is
	 * {@link Dive#getSegmentedPathCondition()}: it returns a complex path condition
	 * structure that includes a string containing the characters "{@code 0}" and
	 * "{@code 1}" to describe the {@code false}/{@code true} decisions made along
	 * the path. There is a one-to-one mapping between such strings and the
	 * execution paths of the program. It is often more convenient to manipulate
	 * such path signatures than it is to manipulate path conditions. The path
	 * condition structures also includes the path condition as a Green expression.
	 * </p>
	 * 
	 * <p>
	 * The concrete values takes the form of a mapping from variable names to Green
	 * constants. The variable names correspond to the names given in the
	 * "{@code deepsea.triggers}" setting in the properties file that defines the
	 * parameter of the DEEPSEA run. Given such a mapping, DEEPSEA will perform
	 * another invocation of the target program, again and again, until the
	 * {@link #refine(Dive)} method returns {@code null}. DEEPSEA does not keep a
	 * record of path conditions or of mappings and it is only this method which can
	 * determine when to terminate the exploration.
	 * </p>
	 * 
	 * @param dive
	 *            the newly completed dive
	 * @return a mapping from variables names to values, or {@code null} if there
	 *         are no further unexplored mappings
	 */
	public Map<String, Constant> refine(Dive dive);

}
