package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;

public class INVOKESTATIC extends Instruction {

	private final int index;

	public INVOKESTATIC(Stepper stepper, int position, int index) {
		super(stepper, position, 184);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		ReferenceType clas = event.location().declaringType();
		/*
		 * First check if we are invoking a delegated method. If so, and if it
		 * executes successfully, it will take care of the stack (popping
		 * arguments and pushing the result), and it will return -2.
		 * 
		 * If the method is not delegated, the method will return one of two
		 * values:
		 * 
		 * - For monitored methods, the method returns -1. The MethodEntryEvent
		 * handler will take care of the actual transfer of parameters.
		 *
		 * - For unmonitored methods, the method return the actual number of
		 * arguments and the code below removes the arguments passed to the
		 * code.
		 */
		int argumentCount = stepper.delegateMethod(clas, index, symbolizer, event.thread());
		if (argumentCount > 0) {
			while (argumentCount-- > 0) {
				frame.pop();
			}
		}
		/*
		 * As above, Stepper.getReturnType() will return '?' for monitored
		 * methods and we do not place any return value on the stack. Instead,
		 * the RETURN (or IRETURN or whatever) instruction will put the return
		 * value on the current stack. In other cases (unmonitored methods),
		 * this code places an appropriate symbolic value or zero on the stack.
		 */
		if (argumentCount != -2) {
			String type = stepper.getReturnType(clas, index);
			char typeCh = type.charAt(0);
			if ((typeCh == 'I') || (typeCh == 'Z')) {
				frame.push(new IntVariable(Symbolizer.getNewVariableName(), -1000, 1000));
			} else if ((typeCh != 'V') && (typeCh != '?')) {
				frame.push(Operation.ZERO);
			}
		}
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("invokestatic ").append(index);
		return sb.toString();
	}

}
