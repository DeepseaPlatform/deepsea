package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class POP extends Instruction {

	public POP(final int position) {
		super(position, 87);
	}
	
	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.pop();
	}
	
	@Override
	public String toString() {
		return "pop";
	}

}
