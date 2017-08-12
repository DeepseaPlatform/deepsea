package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class FASTORE extends Instruction {

	public FASTORE(Stepper stepper, int position) {
		super(stepper, position, 81);
	}
	
}
