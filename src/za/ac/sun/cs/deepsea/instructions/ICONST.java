package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class ICONST extends Instruction {

	private final int value;

	public ICONST(Stepper stepper, int position, int value) {
		super(stepper, position, 3);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(new IntConstant(value));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("iconst_");
		if (value == -1) {
			sb.append("m1");
		} else {
			sb.append(value);
		}
		return sb.toString();
	}

}
