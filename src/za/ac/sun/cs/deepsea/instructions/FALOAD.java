package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class FALOAD extends Instruction {

	public FALOAD(Stepper stepper, int position) {
		super(stepper, position, 48);
	}
	
}
