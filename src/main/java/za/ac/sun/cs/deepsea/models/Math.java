package za.ac.sun.cs.deepsea.models;

import org.apache.logging.log4j.Logger;

import com.sun.jdi.ThreadReference;

import za.ac.sun.cs.deepsea.diver.Configuration;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

/**
 * A model of some operations of {@link java.lang.Math}.
 */
public class Math {

	/**
	 * Construct an instance. Since the operations are all {@code static}, there
	 * is nothing much to construct.
	 * 
	 * @param logger
	 *            a log to write to
	 * @param config
	 *            the configuration of the session
	 */
	public Math(Logger logger, Configuration config) {
	}

	/**
	 * Abstract implementation of the {@link java.lang.Math#max(int, int)}
	 * operation.
	 * 
	 * The routine replace the top two symbolic stack entries, {@code X} and
	 * {@code Y}, with the symbolic value {@code Z}. It adds the following
	 * constraint to the path condition:
	 * 
	 * <pre>
	 * (X &gt;= Y &amp;&amp; Z == X) || (X &lt; Y &amp;&amp; Z == Y)
	 * </pre>
	 * 
	 * @param symbolizer
	 *            the symbolic state of the execution
	 * @param thread
	 *            a reference to the concrete thread
	 * @return always {@code true}
	 */
	public boolean max__II__I(Symbolizer symbolizer, ThreadReference thread) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression arg0 = frame.pop();
		Expression arg1 = frame.pop();
		Expression var = new IntVariable(Symbolizer.getNewVariableName(), -1000, 1000);
		Expression pc = new Operation(Operator.OR,
				new Operation(Operator.AND, new Operation(Operator.GE, arg0, arg1),
						new Operation(Operator.EQ, arg0, var)),
				new Operation(Operator.AND, new Operation(Operator.LT, arg0, arg1),
						new Operation(Operator.EQ, arg1, var)));
		symbolizer.pushExtraConjunct(pc);
		frame.push(var);
		return true;
	}

}
