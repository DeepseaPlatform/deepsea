package za.ac.sun.cs.deepsea.instructions;

public class INVOKEVIRTUAL extends Instruction {

	private final int index;

	public INVOKEVIRTUAL(final int position, final int index) {
		super(position, 182);
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
		sb.append("invokevirtual ").append(index);
		return sb.toString();
	}

}
