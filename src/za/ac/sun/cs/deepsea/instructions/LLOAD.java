package za.ac.sun.cs.deepsea.instructions;

public class LLOAD extends Instruction {

	private final int index;

	private final int size;

	public LLOAD(final int index) {
		super(22);
		this.index = index;
		this.size = 1;
	}

	public LLOAD(final int index, boolean dummy) {
		super(22);
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
