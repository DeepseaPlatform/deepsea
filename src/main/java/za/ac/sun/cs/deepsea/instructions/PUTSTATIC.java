package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class PUTSTATIC extends Instruction {

	private final int index;

	public PUTSTATIC(Stepper stepper, int position, int index) {
		super(stepper, position, 179);
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
		frame.pop();
		// TODO FIX!!
	}
	
	@Override
	public String toString() {
		return "putstatic";
	}

}
