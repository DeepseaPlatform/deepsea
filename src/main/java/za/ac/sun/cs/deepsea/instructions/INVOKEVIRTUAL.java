package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;

public class INVOKEVIRTUAL extends Instruction {

	private final int index;

	private String methodName = null;

	public INVOKEVIRTUAL(Stepper stepper, int position, int index) {
		super(stepper, position, 182);
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
		Location loc = event.location();
		if (methodName == null) {
			ReferenceType clas = loc.declaringType();
			methodName = stepper.getMethodName(clas, index);
		}
		SymbolicFrame frame = symbolizer.getTopFrame();
		ReferenceType clas = loc.declaringType();
		/*
		 * First we throw away the arguments. We can do this, because
		 * Stepper.getArgumentCount() will return 0 for "monitored" methods. For
		 * these routines, the MethodEntryEvent will handle the actual transfer
		 * of parameters. For unmonitored methods, the code below removes the
		 * arguments passed to the code.
		 */
//		int argumentCount = 1 + stepper.getArgumentCount(clas, index);
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
		sb.append("invokevirtual #").append(index);
		if (methodName != null) {
			sb.append(' ').append(methodName);
		}
		return sb.toString();
	}

}
