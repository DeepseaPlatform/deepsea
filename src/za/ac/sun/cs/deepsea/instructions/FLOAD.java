package za.ac.sun.cs.deepsea.instructions;

public class FLOAD extends Instruction {

	private final int index;

	private final int size;

	public FLOAD(final int index) {
		super(23);
		this.index = index;
		this.size = 1;
	}

	public FLOAD(final int index, boolean dummy) {
		super(23);
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