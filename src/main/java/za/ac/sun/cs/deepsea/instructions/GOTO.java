package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class GOTO extends Instruction {

	private final int offset;

	public GOTO(Stepper stepper, int position, int offset) {
		super(stepper, position, 167);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}
	
	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		// Do nothing
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("goto -> ").append(position + offset);
		return sb.toString();
	}

}
