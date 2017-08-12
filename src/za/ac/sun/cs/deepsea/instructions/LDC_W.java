package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class LDC_W extends Instruction {

	private final int index;

	public LDC_W(Stepper stepper, int position, int index) {
		super(stepper, position, 19);
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
