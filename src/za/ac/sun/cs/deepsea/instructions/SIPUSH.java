package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;

public class SIPUSH extends Instruction {

	private final int value;

	public SIPUSH(Stepper stepper, int position, int value) {
		super(stepper, position, 17);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public void execute(StepEvent event, Symbolizer symbolizer) {
		SymbolicFrame frame = symbolizer.getTopFrame();
		frame.push(new IntConstant(value));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("sipush ").append(value);
		return sb.toString();
	}

}
