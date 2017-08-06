package za.ac.sun.cs.deepsea.instructions;

public class MULTIANEWARRAY extends Instruction {

	private final int index;

	private final int dimensions;
	
	public MULTIANEWARRAY(final int position, final int index, final int dimensions) {
		super(position, 197);
		this.index = index;
		this.dimensions = dimensions;
	}

	public int getIndex() {
		return index;
	}

	public int getDimensions() {
		return dimensions;
	}
	
	@Override
	public int getSize() {
		return 4;
	}

}
