package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Operation;

public class ACONST_NULL extends Instruction {

	public ACONST_NULL(Stepper stepper, int position) {
		super(stepper, position, 1);
	}
	
	@Override
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(Operation.ZERO);
	}
	
	@Override
	public String toString() {
		return "aconst_null";
	}

}
