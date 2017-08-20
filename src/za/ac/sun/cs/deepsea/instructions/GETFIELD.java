package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class GETFIELD extends Instruction {

	private final int index;

	private String fieldName = null;

	public GETFIELD(Stepper stepper, int position, int index) {
		super(stepper, position, 180);
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
		if (fieldName == null) {
			ReferenceType clas = event.location().declaringType();
			fieldName = stepper.getFieldName(clas, index);
		}
		SymbolicFrame frame = symbolizer.getTopFrame();
		int objectId = ((IntConstant) frame.pop()).getValue();
		frame.push(symbolizer.getField(objectId, fieldName));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("getfield #").append(index);
		if (fieldName != null) {
			sb.append(' ').append(fieldName);
		}
		return sb.toString();
	}

}
