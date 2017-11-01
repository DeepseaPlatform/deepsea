package za.ac.sun.cs.deepsea.models;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class Math {

	public Math(Diver diver) {
	}

	public boolean max_II_I(Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		Expression arg0 = frame.pop();
		Expression arg1 = frame.pop();
		Expression var = new IntVariable(Symbolizer.getNewVariableName(), -1000, 1000);
		Expression pc = new Operation(Operator.OR,
				new Operation(Operator.AND,
						new Operation(Operator.GE, arg0, arg1),
						new Operation(Operator.EQ, arg0, var)),
				new Operation(Operator.AND,
						new Operation(Operator.LT, arg0, arg1),
						new Operation(Operator.EQ, arg1, var)));
		frame.push(var);
		return true;
	}

}
