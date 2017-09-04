package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class NEW extends Instruction {

	private final int index;

	public NEW(Stepper stepper, int position, int index) {
		super(stepper, position, 187);
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
		int objectId = symbolizer.incrAndGetNewObjectId();
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(new IntConstant(objectId));
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("new #").append(index);
		return sb.toString();
	}

}
