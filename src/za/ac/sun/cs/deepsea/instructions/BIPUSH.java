package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class BIPUSH extends Instruction {

	private final int value;

	public BIPUSH(final int position, final int value) {
		super(position, 16);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(new IntConstant(value));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("bipush ").append(value);
		return sb.toString();
	}

}
