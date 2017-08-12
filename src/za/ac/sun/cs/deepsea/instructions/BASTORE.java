package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class BASTORE extends Instruction {

	public BASTORE(Stepper stepper, int position) {
		super(stepper, position, 84);
	}
	
}
