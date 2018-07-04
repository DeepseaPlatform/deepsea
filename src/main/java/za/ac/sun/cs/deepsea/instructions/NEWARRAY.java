package za.ac.sun.cs.deepsea.instructions;

import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.SymbolicFrame;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;

/**
 * UNIMPLEMENTED &amp; BROKEN
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class NEWARRAY extends Instruction {

	private final int index;

	public NEWARRAY(Stepper stepper, int position, int index) {
		super(stepper, position, 188);
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
	public void execute(StepEvent event, Symbolizer symbolizer) {
		int objectId = symbolizer.createArray();
		SymbolicFrame frame = symbolizer.getTopFrame();
		int n = ((IntConstant) frame.pop()).getValue();
		for (int i = 0; i < n; i++) {
			symbolizer.addArrayValue(objectId, i, Operation.ZERO);
		}
		frame.push(new IntConstant(objectId));
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("newarray #").append(index);
		return sb.toString();
	}

}
