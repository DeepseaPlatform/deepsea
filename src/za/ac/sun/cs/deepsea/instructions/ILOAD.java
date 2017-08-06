package za.ac.sun.cs.deepsea.instructions;

public class ILOAD extends Instruction {

	private final int index;

	private final int size;

	public ILOAD(final int index) {
		super(21);
		this.index = index;
		this.size = 1;
	}

	public ILOAD(final int index, boolean dummy) {
		super(21);
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

	@Override
	public String toString() {
		sb.setLength(0);
		if (size == 1) {
			sb.append("iload_").append(index);
		} else {
			sb.append("iload ").append(index);
		}
		return sb.toString();
	}

}
