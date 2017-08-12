package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class CASTORE extends Instruction {

	public CASTORE(Stepper stepper, int position) {
		super(stepper, position, 85);
	}
	
}
