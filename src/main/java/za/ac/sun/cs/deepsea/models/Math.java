package za.ac.sun.cs.deepsea.models;

import com.sun.jdi.ThreadReference;

import za.ac.sun.cs.deepsea.diver.Diver;
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
	 * Construct a {@link Math} model. Since the operations are all
	 * {@code static}, there is nothing much to construct.
	 * 
	 * @param diver
	 *            the {@link Diver} to which the model belongs
	 */
	public Math(Diver diver) {
	}

	/**
	 * Abstract implementation of the {@link java.lang.Math#max(int, int)}
	 * operation.
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
