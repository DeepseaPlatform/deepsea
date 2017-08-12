package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class NEWARRAY extends Instruction {

	private final int index;

	public NEWARRAY(Stepper stepper, int position, int index) {
		super(stepper, position, 188);
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
