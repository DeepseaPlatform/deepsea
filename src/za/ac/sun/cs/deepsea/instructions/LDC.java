package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class LDC extends Instruction {

	private final int index;

	public LDC(final int position, final int index) {
		super(position, 18);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		// frame.push(new IntConstant(value));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("ldc ").append(index);
		return sb.toString();
	}

}
