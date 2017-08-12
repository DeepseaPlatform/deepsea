package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class SALOAD extends Instruction {

	public SALOAD(Stepper stepper, int position) {
		super(stepper, position, 53);
	}
	
}
