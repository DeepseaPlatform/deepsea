package za.ac.sun.cs.deepsea.models;

import com.sun.jdi.ThreadReference;

import za.ac.sun.cs.deepsea.diver.Diver;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
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
	public boolean startsWith_TITI_Z(Symbolizer symbolizer, ThreadReference thread) {
		// Find the symbolic frame
		SymbolicFrame frame = symbolizer.getTopFrame();
		// Find the address of the first (target) array
		Expression arg0 = frame.pop();
		if (!(arg0 instanceof IntConstant)) { return false; }
		int array0 = ((IntConstant) arg0).getValue();
		// Find the address of the second (pattern) array
		Expression arg1 = frame.pop();
		if (!(arg1 instanceof IntConstant)) { return false; }
		int array1 = ((IntConstant) arg1).getValue();
		// Find the actual lengths of the arrays
		int length0 = Symbolizer.getRealArrayLength(thread, 0);
		int length1 = Symbolizer.getRealArrayLength(thread, 1);
		// If a match is possible at all...
		if (length0 >= length1) {
			// ...construct the equality constraint
			Expression guard = null;
			for (int i = 0; i < length1; i++) {
				Expression elem0 = symbolizer.getArrayValue(array0, i);
				Expression elem1 = symbolizer.getArrayValue(array1, i);
				Expression elemGuard = Operation.apply(Operator.EQ, elem0, elem1);
				if (i == 0) {
					guard = elemGuard;
				} else {
					guard = Operation.apply(Operator.AND, guard, elemGuard);
				}
			}
			if (guard == null) {
				// The pattern array is zero-length, so the result is always TRUE (=1)
				frame.push(Operation.ONE);
			} else {
				// Create a variable for the result
				Expression var = new IntVariable(Symbolizer.getNewVariableName(), 0, 1);
				// We need to construct a condition on the result variable
				Expression pc = Operation.apply(Operator.OR,
						Operation.apply(Operator.AND,
							guard,
							new Operation(Operator.EQ, var, Operation.ONE)),
						Operation.apply(Operator.AND,
							Operation.apply(Operator.NOT, guard),
							new Operation(Operator.EQ, var, Operation.ZERO)));
				// Add the condition as an additional constraint...
				symbolizer.pushExtraConjunct(pc);
				// ...and push the result onto the stack
				frame.push(var);
			}
		} else {
			// |target array| < |pattern array|, so it can never match
			frame.push(Operation.ZERO);
		}
		return true;
	}

}
