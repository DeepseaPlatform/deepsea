package za.ac.sun.cs.deepsea.instructions;

public class ASTORE extends Instruction {

	private final int index;

	private final int size;

	public ASTORE(final int position, final int index) {
		super(position, 58);
		this.index = index;
		this.size = 1;
	}

	public ASTORE(final int position, final int index, boolean dummy) {
		super(position, 58);
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
