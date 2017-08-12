package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class POP extends Instruction {

	public POP(Stepper stepper, int position) {
		super(stepper, position, 87);
	}
	
	@Override
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.pop();
	}
	
	@Override
	public String toString() {
		return "pop";
	}

}
