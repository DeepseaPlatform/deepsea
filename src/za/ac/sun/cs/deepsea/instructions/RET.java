package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class RET extends Instruction {

	private final int index;

	public RET(Stepper stepper, int position, int index) {
		super(stepper, position, 169);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 2;
	}

}
