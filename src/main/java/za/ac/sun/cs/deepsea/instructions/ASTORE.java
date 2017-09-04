package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class ASTORE extends Instruction {

	private final int index;

	private final int size;

	public ASTORE(Stepper stepper, int position, int index) {
		super(stepper, position, 58);
		this.index = index;
		this.size = 1;
	}

	public ASTORE(Stepper stepper, int position, int index, boolean dummy) {
		super(stepper, position, 58);
		this.index = index;
		this.size = 2;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.setLocal(index, frame.pop());
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		if (size == 1) {
			sb.append("astore_").append(index);
		} else {
			sb.append("astore ").append(index);
		}
		return sb.toString();
	}

}
