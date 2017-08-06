package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.Symbolizer;

public class RETURN extends Instruction {

	public RETURN(final int position) {
		super(position, 177);
	}

	@Override
	public void execute(Location loc, Symbolizer symbolizer) {
		symbolizer.popFrame();
	}

	@Override
	public String toString() {
		return "return";
	}

}
