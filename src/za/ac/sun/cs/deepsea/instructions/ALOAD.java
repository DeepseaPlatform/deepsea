package za.ac.sun.cs.deepsea.instructions;

public class ALOAD extends Instruction {

	private final int index;

	private final int size;

	public ALOAD(final int index) {
		super(25);
		this.index = index;
		this.size = 1;
	}

	public ALOAD(final int index, boolean dummy) {
		super(25);
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
