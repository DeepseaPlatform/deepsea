package za.ac.sun.cs.deepsea.instructions;

public class ICONST extends Instruction {

	private final int index;

	public ICONST(final int index) {
		super(3);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("iconst_");
		if (index == -1) {
			sb.append("m1");
		} else {
			sb.append(index);
		}
		return sb.toString();
	}

}
