package za.ac.sun.cs.deepsea.models;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class StringMethods {

	public StringMethods(Diver diver) {
	}

	/*
	 * assume length is N
	 * true -> a[0] = b[0] && a[1] = b[1] && .... && a[N-1] = b[N-1]  
	 * 
	 * 
	 * 
	 */
	public boolean startsWith__TITI_Z(Symbolizer symbolizer) {
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
		symbolizer.pushExtraConjunct(pc);
		frame.push(var);
		return true;
	}

}
