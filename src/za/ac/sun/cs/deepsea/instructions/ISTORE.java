package za.ac.sun.cs.deepsea.instructions;

public class ISTORE extends Instruction {

	private final int index;

	private final int size;

	public ISTORE(final int index) {
		super(54);
		this.index = index;
		this.size = 1;
	}

	public ISTORE(final int index, boolean dummy) {
		super(54);
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
