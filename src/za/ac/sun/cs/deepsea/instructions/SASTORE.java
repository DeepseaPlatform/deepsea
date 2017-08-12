package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class SASTORE extends Instruction {

	public SASTORE(Stepper stepper, int position) {
		super(stepper, position, 86);
	}
	
}
