package za.ac.sun.cs.deepsea.instructions;

public class ISUB extends Instruction {

	public ISUB(final int position) {
		super(position, 100);
	}

	@Override
	public String toString() {
		return "isub";
	}

}
