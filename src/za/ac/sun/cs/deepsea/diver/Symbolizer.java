package za.ac.sun.cs.deepsea.diver;

import java.util.Stack;

//import java.util.logging.Logger;

import com.sun.jdi.Location;

import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.Operation;
import za.ac.sun.cs.green.expr.Operation.Operator;

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

	private boolean popFrame() {
		assert !frames.isEmpty();
		frames.pop();
		if (frames.isEmpty()) {
			symbolicMode = false;
		} else {
			frame = frames.peek();
		}
		return symbolicMode;
	}

//	public void execute(Location location, ClassGen classGen, InstructionHandle handle) {
//		// final String cn = location.declaringType().name();
//		// final String mn = location.method().name();
//		Instruction ins = handle.getInstruction();
//		if (symbolicMode) {
//			execute(location, classGen, ins);
//		} // SYMBOLIC FRAME
//		//		if (!symbolicMode && (ins instanceof InvokeInstruction)) {
//		//			InvokeInstruction iins = (InvokeInstruction) ins;
//		//			if (iins.)
//		//			log.finest(cn + "." + mn + " " + handle.toString());
//		//		}
//	}
	
	public void execute(Location location, int classGen, int handle) {
//		// final String cn = location.declaringType().name();
//		// final String mn = location.method().name();
//		Instruction ins = handle.getInstruction();
//		if (symbolicMode) {
//			execute(location, classGen, ins);
//		} // SYMBOLIC FRAME
//		//		if (!symbolicMode && (ins instanceof InvokeInstruction)) {
//		//			InvokeInstruction iins = (InvokeInstruction) ins;
//		//			if (iins.)
//		//			log.finest(cn + "." + mn + " " + handle.toString());
//		//		}
	}

//	private void execute(Location location, ClassGen classGen, Instruction ins) {
//		if (ins instanceof ACONST_NULL) {
//			execute(location, (ACONST_NULL) ins);
//		} else if (ins instanceof ArithmeticInstruction) {
//			execute(location, (ArithmeticInstruction) ins);
//		} else if (ins instanceof ArrayInstruction) {
//			execute(location, (ArrayInstruction) ins);
//		} else if (ins instanceof ARRAYLENGTH) {
//			execute(location, (ARRAYLENGTH) ins);
//		} else if (ins instanceof ATHROW) {
//			execute(location, (ATHROW) ins);
//		} else if (ins instanceof ConstantPushInstruction) {
//			execute(location, (ConstantPushInstruction) ins);
//		} else if (ins instanceof GotoInstruction) {
//			execute(location, (GotoInstruction) ins);
//		} else if (ins instanceof IfInstruction) {
//			execute(location, (IfInstruction) ins);
//		} else if (ins instanceof JsrInstruction) {
//			execute(location, (JsrInstruction) ins);
//		} else if (ins instanceof Select) {
//			execute(location, (Select) ins);
//		} else if (ins instanceof BREAKPOINT) {
//			execute(location, (BREAKPOINT) ins);
//		} else if (ins instanceof ConversionInstruction) {
//			execute(location, (ConversionInstruction) ins);
//		} else if (ins instanceof FieldInstruction) {
//			execute(location, (FieldInstruction) ins);
//		} else if (ins instanceof InvokeInstruction) {
//			execute(location, classGen, (InvokeInstruction) ins);
//		} else if (ins instanceof ANEWARRAY) {
//			execute(location, (ANEWARRAY) ins);
//		} else if (ins instanceof CHECKCAST) {
//			execute(location, (CHECKCAST) ins);
//		} else if (ins instanceof INSTANCEOF) {
//			execute(location, (INSTANCEOF) ins);
//		} else if (ins instanceof LDC) {
//			execute(location, (LDC) ins);
//		} else if (ins instanceof LDC2_W) {
//			execute(location, (LDC2_W) ins);
//		} else if (ins instanceof MULTIANEWARRAY) {
//			execute(location, (MULTIANEWARRAY) ins);
//		} else if (ins instanceof NameSignatureInstruction) {
//			execute(location, (NameSignatureInstruction) ins);
//		} else if (ins instanceof NEW) {
//			execute(location, (NEW) ins);
//		} else if (ins instanceof DCMPG) {
//			execute(location, (DCMPG) ins);
//		} else if (ins instanceof DCMPL) {
//			execute(location, (DCMPL) ins);
//		} else if (ins instanceof FCMPG) {
//			execute(location, (FCMPG) ins);
//		} else if (ins instanceof FCMPL) {
//			execute(location, (FCMPL) ins);
//		} else if (ins instanceof IMPDEP1) {
//			execute(location, (IMPDEP1) ins);
//		} else if (ins instanceof IMPDEP2) {
//			execute(location, (IMPDEP2) ins);
//		} else if (ins instanceof LCMP) {
//			execute(location, (LCMP) ins);
//		} else if (ins instanceof IINC) {
//			execute(location, (IINC) ins);
//		} else if (ins instanceof LoadInstruction) {
//			execute(location, (LoadInstruction) ins);
//		} else if (ins instanceof StoreInstruction) {
//			execute(location, (StoreInstruction) ins);
//		} else if (ins instanceof MONITORENTER) {
//			execute(location, (MONITORENTER) ins);
//		} else if (ins instanceof MONITOREXIT) {
//			execute(location, (MONITOREXIT) ins);
//		} else if (ins instanceof NEWARRAY) {
//			execute(location, (NEWARRAY) ins);
//		} else if (ins instanceof NOP) {
//			execute(location, (NOP) ins);
//		} else if (ins instanceof RET) {
//			execute(location, (RET) ins);
//		} else if (ins instanceof ReturnInstruction) {
//			execute(location, (ReturnInstruction) ins);
//		} else if (ins instanceof StackInstruction) {
//			execute(location, (StackInstruction) ins);
//		}
//	}
//
//	private void execute(Location location, ArithmeticInstruction ains) {
//		if (ains instanceof IADD) {
//			Expression e1 = frame.pop();
//			Expression e0 = frame.pop();
//			frame.push(new Operation(Operator.ADD, e0, e1));
//		} else {
//			throw new Error(); // <--- !!!
//			// DADD DDIV DMUL DNEG DREM DSUB
//			// FADD FDIV FMUL FNEG FREM FSUB
//			//      IAND IDIV IMUL INEG IOR IREM ISHL ISHR ISUB IUSHR IXOR
//			// LADD LAND LDIV LMUL LNEG LOR LREM LSHL LSHR LSUB LUSHR LXOR
//		}
//	}
//
//	private void execute(Location location, ConstantPushInstruction cpins) {
//		if ((cpins instanceof BIPUSH) || (cpins instanceof ICONST)) {
//			frame.push(new IntConstant(cpins.getValue().intValue()));
//		} else  {
//			throw new Error(); // <--- !!!
//			// DCONST FCONST LCONST SIPUSH
//		}
//	}
//	
//	private void execute(Location location, IfInstruction iins) {
//		// TODO <<<implement>>>
//		// IF_ACMPEQ IF_ACMPNE IF_ICMPEQ IF_ICMPGE IF_ICMPGT IF_ICMPLE IF_ICMPLT IF_ICMPNE
//		// IFEQ IFGE IFGT IFLE IFLT IFNE
//		// IFNONNULL IFNULL
//	}
//
//	private void execute(Location location, ClassGen classGen, InvokeInstruction iins) {
//		if (iins instanceof INVOKESTATIC) {
//			// TODO <<<implement>>>
//		} else {
//			throw new Error(); // <--- !!!
//			// INVOKEDYNAMIC INVOKEINTERFACE INVOKESPECIAL INVOKEVIRTUAL
//		}
//	}
//
//	private void execute(final Location location, final LoadInstruction lins) {
//		if (lins instanceof ILOAD) {
//			frame.push(frame.getLocal(lins.getIndex()));
//		} else {
//			throw new Error(); // <--- !!!
//			// ALOAD DLOAD FLOAD LLOAD
//		}
//	}
//
//	private void execute(final Location location, final StoreInstruction sins) {
//		throw new Error(); // <--- !!!
//		// ASTORE DSTORE FSTORE ISTORE LSTORE
//	}
//
//	private void execute(final Location location, final MONITORENTER monitorenter) {
//		throw new Error(); // <--- !!!
//	}
//
//	private void execute(final Location location, final MONITOREXIT monitorexit) {
//		throw new Error(); // <--- !!!
//	}
//
//	private void execute(final Location location, final NEWARRAY newarray) {
//		throw new Error(); // <--- !!!
//	}
//
//	private void execute(final Location location, final NOP nop) {
//		throw new Error(); // <--- !!!
//	}
//
//	private void execute(final Location location, final RET ret) {
//		throw new Error(); // <--- !!!
//	}
//
//	private void execute(final Location location, final ReturnInstruction rins) {
//		if (rins instanceof IRETURN) {
//			Expression e = frame.pop();
//			if (popFrame()) {
//				frame.push(e);
//			}
//		} else if (rins instanceof RETURN) {
//			popFrame();
//		} else {
//			throw new Error(); // <--- !!!
//			// ARETURN DRETURN FRETURN LRETURN
//		}
//	}
//
//	private void execute(final Location location, final StackInstruction sins) {
//		if (sins instanceof POP) {
//			frame.pop();
//		} else {
//			throw new Error(); // <--- !!!
//			// DPU DUP_X1 DUP_X2 DUP2 DUP2_X1 DUP2_X2 POP2 SWAP
//		}
//	}

}
