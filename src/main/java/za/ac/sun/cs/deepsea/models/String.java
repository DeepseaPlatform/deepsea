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

public class String {

	public String(Diver diver) {
	}

	public boolean startsWith__Ljava_1lang_1String_2__Z(Symbolizer symbolizer, ThreadReference thread) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		int prefixAddress = intConstantValue(frame.pop());
		int thisAddress = intConstantValue(frame.pop());
		int prefixLength = intConstantValue(symbolizer.getField(prefixAddress, "length"));
		int thisLength = intConstantValue(symbolizer.getField(thisAddress, "length"));
		if (thisLength >= prefixLength) {
			Expression guard = null;
			for (int i = 0; i < prefixLength; i++) {
				Expression prefixChar = symbolizer.getField(prefixAddress, "" + i);
				Expression thisChar = symbolizer.getField(thisAddress, "" + i);
				Expression eq = Operation.apply(Operator.EQ, prefixChar, thisChar);
				if (i == 0) {
					guard = eq;
				} else {
					guard = Operation.apply(Operator.AND, guard, eq);
				}
			}
			if (guard == null) {
				frame.push(Operation.ONE); // |prefix| == 0, so result is always TRUE (=1)
			} else {
				Expression var = new IntVariable(Symbolizer.getNewVariableName(), 0, 1);
				Expression pc = Operation.apply(Operator.OR,
						Operation.apply(Operator.AND,
							guard,
							Operation.apply(Operator.EQ, var, Operation.ONE)),
						Operation.apply(Operator.AND,
							Operation.apply(Operator.NOT, guard),
							Operation.apply(Operator.EQ, var, Operation.ZERO)));
				symbolizer.pushExtraConjunct(pc);
				frame.push(var);
			}
		} else {
			// |target array| < |pattern array|, so it can never match
			frame.push(Operation.ZERO);
		}
		return true;
	}

	public boolean endsWith__Ljava_1lang_1String_2__Z(Symbolizer symbolizer, ThreadReference thread) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		int prefixAddress = intConstantValue(frame.pop());
		int thisAddress = intConstantValue(frame.pop());
		int prefixLength = intConstantValue(symbolizer.getField(prefixAddress, "length"));
		int thisLength = intConstantValue(symbolizer.getField(thisAddress, "length"));
		if (thisLength >= prefixLength) {
			Expression guard = null;
			for (int i = 0; i < prefixLength; i++) {
				Expression prefixChar = symbolizer.getField(prefixAddress, "" + i);
				Expression thisChar = symbolizer.getField(thisAddress, "" + (i + thisLength - prefixLength));
				Expression eq = Operation.apply(Operator.EQ, prefixChar, thisChar);
				if (i == 0) {
					guard = eq;
				} else {
					guard = Operation.apply(Operator.AND, guard, eq);
				}
			}
			if (guard == null) {
				frame.push(Operation.ONE); // |prefix| == 0, so result is always TRUE (=1)
			} else {
				Expression var = new IntVariable(Symbolizer.getNewVariableName(), 0, 1);
				Expression pc = Operation.apply(Operator.OR,
						Operation.apply(Operator.AND,
								guard,
								Operation.apply(Operator.EQ, var, Operation.ONE)),
						Operation.apply(Operator.AND,
								Operation.apply(Operator.NOT, guard),
								Operation.apply(Operator.EQ, var, Operation.ZERO)));
				symbolizer.pushExtraConjunct(pc);
				frame.push(var);
			}
		} else {
			// |target array| < |pattern array|, so it can never match
			frame.push(Operation.ZERO);
		}
		return true;
	}
	
	private int intConstantValue(Expression expr) {
		assert expr instanceof IntConstant;
		return ((IntConstant) expr).getValue();
	}

}
