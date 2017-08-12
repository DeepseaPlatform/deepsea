package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class DREM extends Instruction {

	public DREM(Stepper stepper, int position) {
		super(stepper, position, 115);
	}
	
}
