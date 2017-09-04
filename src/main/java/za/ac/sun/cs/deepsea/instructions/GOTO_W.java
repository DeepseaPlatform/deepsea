package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class GOTO_W extends Instruction {

	private final int offset;

	public GOTO_W(Stepper stepper, int position, int offset) {
		super(stepper, position, 200);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 5;
	}

}
