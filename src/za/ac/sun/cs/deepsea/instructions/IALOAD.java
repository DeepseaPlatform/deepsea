package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IALOAD extends Instruction {

	public IALOAD(Stepper stepper, int position) {
		super(stepper, position, 46);
	}
	
}
