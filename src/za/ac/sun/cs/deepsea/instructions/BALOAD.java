package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class BALOAD extends Instruction {

	public BALOAD(Stepper stepper, int position) {
		super(stepper, position, 51);
	}
	
}
