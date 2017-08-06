package za.ac.sun.cs.deepsea.instructions;

public class FSTORE extends Instruction {

	private final int index;

	private final int size;

	public FSTORE(final int index) {
		super(56);
		this.index = index;
		this.size = 1;
	}

	public FSTORE(final int index, boolean dummy) {
		super(56);
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
