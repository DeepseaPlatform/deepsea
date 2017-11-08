package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

public class IINC extends Instruction {

	private final int index;

	private final int value;
	
	public IINC(Stepper stepper, int position, int index, int value) {
		super(stepper, position, 132);
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
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
		Expression e1 = new IntConstant(value);
		Expression e0 = frame.getLocal(index);
		frame.setLocal(index, Operation.apply(Operator.ADD, e0, e1));
	}
	
	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("iinc #").append(index).append(' ').append(value);
		return sb.toString();
	}

}
