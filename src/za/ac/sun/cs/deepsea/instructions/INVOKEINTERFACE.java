package za.ac.sun.cs.deepsea.instructions;

public class INVOKEINTERFACE extends Instruction {

	private final int index;

	private final int count;
	
	public INVOKEINTERFACE(final int position, final int index, final int count) {
		super(position, 185);
		this.index = index;
		this.count = count;
	}

	public int getIndex() {
		return index;
	}

	public int getCount() {
		return count;
	}
	
	@Override
	public int getSize() {
		return 5;
	}

}
