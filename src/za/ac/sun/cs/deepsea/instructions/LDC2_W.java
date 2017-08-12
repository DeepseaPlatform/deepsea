package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class LDC2_W extends Instruction {

	private final int index;

	public LDC2_W(Stepper stepper, int position, int index) {
		super(stepper, position, 20);
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
