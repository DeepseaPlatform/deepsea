package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
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
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		ReferenceType clas = loc.declaringType();
		int argumentCount = stepper.getArgumentCount(clas, index);
		if (argumentCount > 0) {
			while (argumentCount-- > 0) {
				frame.pop();
			}
		}
		String type = stepper.getReturnType(clas, index);
		char typeCh = type.charAt(0);
		if ((typeCh == 'I') || (typeCh == 'Z')) {
			frame.push(new IntVariable("$v" + variableCount++, -1000, 1000));
		} else if ((typeCh != 'V') && (typeCh != '?')) {
			frame.push(Operation.ZERO);
		}
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("invokestatic ").append(index);
		return sb.toString();
	}

}
