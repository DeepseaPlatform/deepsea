package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class INVOKEDYNAMIC extends Instruction {

	private final int index;

	public INVOKEDYNAMIC(Stepper stepper, int position, int index) {
		super(stepper, position, 186);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
