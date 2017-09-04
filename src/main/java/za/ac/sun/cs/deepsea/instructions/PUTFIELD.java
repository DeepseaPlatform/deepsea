package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;

public class PUTFIELD extends Instruction {

	private final int index;

	private String fieldName = null;

	public PUTFIELD(Stepper stepper, int position, int index) {
		super(stepper, position, 181);
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
		Expression value = frame.pop();
		int objectId = ((IntConstant) frame.pop()).getValue();
		symbolizer.putField(objectId, fieldName, value);
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("putfield #").append(index);
		if (fieldName != null) {
			sb.append(' ').append(fieldName);
		}
		return sb.toString();
	}

}
