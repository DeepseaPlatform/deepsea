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

/**
 * A model of some operations of {@link java.lang.String}. This implementation
 * exploits the fact that string instances enjoy a special status in Java and
 * are also treated specially by DEEPSEA. Wherever possible, DEEPSEA mirrors
 * Java string instances with much simpler array-like symbolic values.
 */
public class String {

	/**
	 * Construct an instance. Despite the fact that the operations are not all
	 * {@code static}, the current set of routines do not need to construct any
	 * additional information and are implemented in a {@code static} fashion.
	 * 
	 * @param diver
	 *            the {@link Diver} to which the model belongs
	 */
	public String(Diver diver) {
	}

	/**
	 * Abstract implementation of the
	 * {@link java.lang.String#startsWith(String)} operation.
	 * 
	 * @param symbolizer
	 *            the symbolic state of the execution
	 * @param thread
	 *            a reference to the concrete thread
	 * @return always {@code true}
	 */
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
						Operation.apply(Operator.AND, guard, Operation.apply(Operator.EQ, var, Operation.ONE)),
						Operation.apply(Operator.AND, Operation.apply(Operator.NOT, guard),
								Operation.apply(Operator.EQ, var, Operation.ZERO)));
				symbolizer.pushExtraConjunct(pc);
				frame.push(var);
			}
		} else {
			frame.push(Operation.ZERO); // |this| < |prefix|, so result is always FALSE (=0)
		}
		return true;
	}

	/**
	 * Abstract implementation of the {@link java.lang.String#endsWith(String)}
	 * operation.
	 * 
	 * @param symbolizer
	 *            the symbolic state of the execution
	 * @param thread
	 *            a reference to the concrete thread
	 * @return always {@code true}
	 */
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
						Operation.apply(Operator.AND, guard, Operation.apply(Operator.EQ, var, Operation.ONE)),
						Operation.apply(Operator.AND, Operation.apply(Operator.NOT, guard),
								Operation.apply(Operator.EQ, var, Operation.ZERO)));
				symbolizer.pushExtraConjunct(pc);
				frame.push(var);
			}
		} else {
			frame.push(Operation.ZERO); // |this| < |prefix|, so result is always FALSE (=0)
		}
		return true;
	}

	/**
	 * Return the native value (i.e., {@code int} value) of a Green integer
	 * constant.
	 * 
	 * @param expr
	 *            the integer constant instance
	 * @return the value of the constant
	 */
	private int intConstantValue(Expression expr) {
		assert expr instanceof IntConstant;
		return ((IntConstant) expr).getValue();
	}

}
