package za.ac.sun.cs.deepsea.instructions;

public class DLOAD extends Instruction {

	private final int index;

	private final int size;

	public DLOAD(final int index) {
		super(24);
		this.index = index;
		this.size = 1;
	}

	public DLOAD(final int index, boolean dummy) {
		super(24);
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
