package za.ac.sun.cs.deepsea.instructions;

public class DSTORE extends Instruction {

	private final int index;

	private final int size;

	public DSTORE(final int index) {
		super(57);
		this.index = index;
		this.size = 1;
	}

	public DSTORE(final int index, boolean dummy) {
		super(57);
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
