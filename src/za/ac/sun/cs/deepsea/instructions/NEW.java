package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class NEW extends Instruction {

	private final int index;

	public NEW(Stepper stepper, int position, int index) {
		super(stepper, position, 187);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
