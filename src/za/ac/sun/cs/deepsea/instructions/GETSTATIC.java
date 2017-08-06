package za.ac.sun.cs.deepsea.instructions;

public class GETSTATIC extends Instruction {

	private final int index;

	public GETSTATIC(final int index) {
		super(178);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("getstatic ").append(index);
		return sb.toString();
	}

}
