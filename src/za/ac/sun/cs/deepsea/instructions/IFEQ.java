package za.ac.sun.cs.deepsea.instructions;

import za.ac.sun.cs.deepsea.diver.Stepper;

public class IFEQ extends Instruction {

	private final int offset;

	public IFEQ(Stepper stepper, int position, int offset) {
		super(stepper, position, 153);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("ifeq ").append(offset);
		return sb.toString();
	}

}
