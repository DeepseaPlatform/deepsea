package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Operation;

public class GETSTATIC extends Instruction {

	private final int index;

	public GETSTATIC(Stepper stepper, int position, int index) {
		super(stepper, position, 178);
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
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(Operation.ZERO);
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("getstatic ").append(index);
		return sb.toString();
	}

}
