package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class DALOAD extends Instruction {

	public DALOAD(Stepper stepper, int position) {
		super(stepper, position, 49);
	}
	
}
