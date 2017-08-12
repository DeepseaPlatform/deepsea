package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class DASTORE extends Instruction {

	public DASTORE(Stepper stepper, int position) {
		super(stepper, position, 82);
	}
	
}
