package za.ac.sun.cs.deepsea.diver;

import java.util.Stack;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.instructions.Instruction;

//import java.util.logging.Logger;

public class Symbolizer {

//	private final Diver diver;
//	private final Logger log;

	private boolean symbolicMode;

	private final Stack<SymbolicFrame> frames = new Stack<>();

	private SymbolicFrame frame;

	public Symbolizer(final Diver diver) {
//		this.diver = diver;
//		this.log = this.diver.getLog();
		symbolicMode = false;
	}

	public boolean popFrame() {
		assert !frames.isEmpty();
		frames.pop();
		if (frames.isEmpty()) {
			symbolicMode = false;
		} else {
			frame = frames.peek();
		}
		return symbolicMode;
	}

	public SymbolicFrame getTopFrame() {
		return frame;
	}

	public void execute(Location loc, Instruction ins) {
		if (symbolicMode) {
			ins.execute(loc, this);
		}
//		if (!symbolicMode) {
//		}
	}

}
