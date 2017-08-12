package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class INSTANCEOF extends Instruction {

	private final int index;

	public INSTANCEOF(Stepper stepper, int position, int index) {
		super(stepper, position, 193);
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
