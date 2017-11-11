package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.constantpool.Constant;
import za.ac.sun.cs.deepsea.constantpool.ConstantInteger;
import za.ac.sun.cs.deepsea.constantpool.ConstantString;
import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;

public class LDC extends Instruction {

	private final int index;

	public LDC(Stepper stepper, int position, int index) {
		super(stepper, position, 18);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		ReferenceType clas = event.location().declaringType();
		Constant constant = stepper.getConstant(clas, index);
		if (constant instanceof ConstantInteger) {
			int value = ((ConstantInteger) constant).getValue();
			frame.push(new IntConstant(value));
		} else if (constant instanceof ConstantString) {
			String value = stepper.getConstantString(clas, index);
			int stringId = symbolizer.createArray();
			symbolizer.putField(stringId, "length", new IntConstant(value.length()));
			for (int i = 0; i < value.length(); i++) {
				symbolizer.putField(stringId, "" + i, new IntConstant(value.charAt(i)));
			}
			frame.push(new IntConstant(stringId));
		} else {
			frame.push(Operation.ZERO);
		}
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("ldc ").append(index);
		return sb.toString();
	}

}
