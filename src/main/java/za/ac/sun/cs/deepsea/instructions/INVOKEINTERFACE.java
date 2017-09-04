package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class INVOKEINTERFACE extends Instruction {

	private final int index;

	private final int count;
	
	public INVOKEINTERFACE(Stepper stepper, int position, int index, int count) {
		super(stepper, position, 185);
		this.index = index;
		this.count = count;
	}

	public int getIndex() {
		return index;
	}

	public int getCount() {
		return count;
	}
	
	@Override
	public int getSize() {
		return 5;
	}

}
