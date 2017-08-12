package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class DLOAD extends Instruction {

	private final int index;

	private final int size;

	public DLOAD(Stepper stepper, int position, int index) {
		super(stepper, position, 24);
		this.index = index;
		this.size = 1;
	}

	public DLOAD(Stepper stepper, int position, int index, boolean dummy) {
		super(stepper, position, 24);
		this.index = index;
		this.size = 2;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return size;
	}

}
