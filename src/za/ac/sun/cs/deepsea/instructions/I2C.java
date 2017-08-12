package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class I2C extends Instruction {

	public I2C(Stepper stepper, int position) {
		super(stepper, position, 146);
	}
	
}
