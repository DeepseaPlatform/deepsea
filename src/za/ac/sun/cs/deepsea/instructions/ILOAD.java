package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

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
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(frame.getLocal(index));
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
