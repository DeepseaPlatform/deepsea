package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class RETURN extends Instruction {

	public RETURN(Stepper stepper, int position) {
		super(stepper, position, 177);
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		symbolizer.popFrame();
	}

	@Override
	public String toString() {
		return "return";
	}

}
