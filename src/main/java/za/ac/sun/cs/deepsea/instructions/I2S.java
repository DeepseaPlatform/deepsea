package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class I2S extends Instruction {

	public I2S(Stepper stepper, int position) {
		super(stepper, position, 147);
	}
	
}
