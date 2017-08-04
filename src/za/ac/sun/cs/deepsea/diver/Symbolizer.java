package za.ac.sun.cs.deepsea.diver;

import java.util.Stack;

//import java.util.logging.Logger;

import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.BREAKPOINT;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.DCMPG;
import org.apache.bcel.generic.DCMPL;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMPDEP1;
import org.apache.bcel.generic.IMPDEP2;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.IRETURN;
//import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.LCMP;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.MONITORENTER;
import org.apache.bcel.generic.MONITOREXIT;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.NameSignatureInstruction;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.StoreInstruction;

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

	public void execute(Location location, ClassGen classGen, InstructionHandle handle) {
		// final String cn = location.declaringType().name();
		// final String mn = location.method().name();
		Instruction ins = handle.getInstruction();
		if (symbolicMode) {
			execute(location, classGen, ins);
		} // SYMBOLIC FRAME
		//		if (!symbolicMode && (ins instanceof InvokeInstruction)) {
		//			InvokeInstruction iins = (InvokeInstruction) ins;
		//			if (iins.)
		//			log.finest(cn + "." + mn + " " + handle.toString());
		//		}
	}

	private void execute(Location location, ClassGen classGen, Instruction ins) {
		if (ins instanceof ACONST_NULL) {
			execute(location, (ACONST_NULL) ins);
		} else if (ins instanceof ArithmeticInstruction) {
			execute(location, (ArithmeticInstruction) ins);
		} else if (ins instanceof ArrayInstruction) {
			execute(location, (ArrayInstruction) ins);
		} else if (ins instanceof ARRAYLENGTH) {
			execute(location, (ARRAYLENGTH) ins);
		} else if (ins instanceof ATHROW) {
			execute(location, (ATHROW) ins);
		} else if (ins instanceof ConstantPushInstruction) {
			execute(location, (ConstantPushInstruction) ins);
		} else if (ins instanceof GotoInstruction) {
			execute(location, (GotoInstruction) ins);
		} else if (ins instanceof IfInstruction) {
			execute(location, (IfInstruction) ins);
		} else if (ins instanceof JsrInstruction) {
			execute(location, (JsrInstruction) ins);
		} else if (ins instanceof Select) {
			execute(location, (Select) ins);
		} else if (ins instanceof BREAKPOINT) {
			execute(location, (BREAKPOINT) ins);
		} else if (ins instanceof ConversionInstruction) {
			execute(location, (ConversionInstruction) ins);
		} else if (ins instanceof FieldInstruction) {
			execute(location, (FieldInstruction) ins);
		} else if (ins instanceof InvokeInstruction) {
			execute(location, classGen, (InvokeInstruction) ins);
		} else if (ins instanceof ANEWARRAY) {
			execute(location, (ANEWARRAY) ins);
		} else if (ins instanceof CHECKCAST) {
			execute(location, (CHECKCAST) ins);
		} else if (ins instanceof INSTANCEOF) {
			execute(location, (INSTANCEOF) ins);
		} else if (ins instanceof LDC) {
			execute(location, (LDC) ins);
		} else if (ins instanceof LDC2_W) {
			execute(location, (LDC2_W) ins);
		} else if (ins instanceof MULTIANEWARRAY) {
			execute(location, (MULTIANEWARRAY) ins);
		} else if (ins instanceof NameSignatureInstruction) {
			execute(location, (NameSignatureInstruction) ins);
		} else if (ins instanceof NEW) {
			execute(location, (NEW) ins);
		} else if (ins instanceof DCMPG) {
			execute(location, (DCMPG) ins);
		} else if (ins instanceof DCMPL) {
			execute(location, (DCMPL) ins);
		} else if (ins instanceof FCMPG) {
			execute(location, (FCMPG) ins);
		} else if (ins instanceof FCMPL) {
			execute(location, (FCMPL) ins);
		} else if (ins instanceof IMPDEP1) {
			execute(location, (IMPDEP1) ins);
		} else if (ins instanceof IMPDEP2) {
			execute(location, (IMPDEP2) ins);
		} else if (ins instanceof LCMP) {
			execute(location, (LCMP) ins);
		} else if (ins instanceof IINC) {
			execute(location, (IINC) ins);
		} else if (ins instanceof LoadInstruction) {
			execute(location, (LoadInstruction) ins);
		} else if (ins instanceof StoreInstruction) {
			execute(location, (StoreInstruction) ins);
		} else if (ins instanceof MONITORENTER) {
			execute(location, (MONITORENTER) ins);
		} else if (ins instanceof MONITOREXIT) {
			execute(location, (MONITOREXIT) ins);
		} else if (ins instanceof NEWARRAY) {
			execute(location, (NEWARRAY) ins);
		} else if (ins instanceof NOP) {
			execute(location, (NOP) ins);
		} else if (ins instanceof RET) {
			execute(location, (RET) ins);
		} else if (ins instanceof ReturnInstruction) {
			execute(location, (ReturnInstruction) ins);
		} else if (ins instanceof StackInstruction) {
			execute(location, (StackInstruction) ins);
		}
	}

	private void execute(Location location, ACONST_NULL aconst_null) {
		throw new Error(); // <--- !!!
		// Maybe not symbolic?
	}

	private void execute(Location location, ArithmeticInstruction ains) {
		if (ains instanceof IADD) {
			Expression e1 = frame.pop();
			Expression e0 = frame.pop();
			frame.push(new Operation(Operator.ADD, e0, e1));
		} else {
			throw new Error(); // <--- !!!
			// DADD DDIV DMUL DNEG DREM DSUB
			// FADD FDIV FMUL FNEG FREM FSUB
			//      IAND IDIV IMUL INEG IOR IREM ISHL ISHR ISUB IUSHR IXOR
			// LADD LAND LDIV LMUL LNEG LOR LREM LSHL LSHR LSUB LUSHR LXOR
		}
	}

	private void execute(Location location, ArrayInstruction ains) {
		throw new Error(); // <--- !!!
		// AALOAD AASTORE BALOAD BASTORE
		// CALOAD CASTORE DALOAD DASTORE
		// FALOAD FASTORE IALOAD IASTORE
		// LALOAD LASTORE SALOAD SASTORE
	}

	private void execute(Location location, ARRAYLENGTH arraylength) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, ATHROW athrow) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, ConstantPushInstruction cpins) {
		if ((cpins instanceof BIPUSH) || (cpins instanceof ICONST)) {
			frame.push(new IntConstant(cpins.getValue().intValue()));
		} else  {
			throw new Error(); // <--- !!!
			// DCONST FCONST LCONST SIPUSH
		}
	}
	
	private void execute(Location location, GotoInstruction gins) {
		throw new Error(); // <--- !!!
		// GOTO GOTO_W
	}

	private void execute(Location location, IfInstruction iins) {
		// TODO <<<implement>>>
		// IF_ACMPEQ IF_ACMPNE IF_ICMPEQ IF_ICMPGE IF_ICMPGT IF_ICMPLE IF_ICMPLT IF_ICMPNE
		// IFEQ IFGE IFGT IFLE IFLT IFNE
		// IFNONNULL IFNULL
	}

	private void execute(Location location, JsrInstruction jins) {
		throw new Error(); // <--- !!!
		// JSR JSR_W
	}

	private void execute(Location location, Select sins) {
		throw new Error(); // <--- !!!
		// LOOPUPSWITCH TABLESWITCH
	}

	private void execute(Location location, BREAKPOINT breakpoint) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, ConversionInstruction cins) {
		throw new Error(); // <--- !!!
		// D2F D2I D2L
		// F2D F2I F2L
		// I2B I2C I2D I2F I2L I2S
		// L2D L2F L2I
	}

	private void execute(Location location, FieldInstruction fins) {
		throw new Error(); // <--- !!!
		// GETFIELD GETSTATIC PUTFIELD PUTSTATIC
	}

	private void execute(Location location, ClassGen classGen, InvokeInstruction iins) {
		if (iins instanceof INVOKESTATIC) {
			// TODO <<<implement>>>
		} else {
			throw new Error(); // <--- !!!
			// INVOKEDYNAMIC INVOKEINTERFACE INVOKESPECIAL INVOKEVIRTUAL
		}
	}

	private void execute(Location location, ANEWARRAY anewarray) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, CHECKCAST checkcast) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, INSTANCEOF instanceoff) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, LDC ldc) {
		throw new Error(); // <--- !!!
		// LDC_W
	}

	private void execute(Location location, LDC2_W ldc2_w) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, MULTIANEWARRAY multianewarray) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, NameSignatureInstruction nins) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, NEW neww) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, DCMPG dcmpg) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, DCMPL dcmpl) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, FCMPG fcmpg) {
		throw new Error(); // <--- !!!
	}

	private void execute(Location location, FCMPL fcmpl) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final IMPDEP1 impdep1) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final IMPDEP2 impdep2) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final LCMP lcmp) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final IINC iinc) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final LoadInstruction lins) {
		if (lins instanceof ILOAD) {
			frame.push(frame.getLocal(lins.getIndex()));
		} else {
			throw new Error(); // <--- !!!
			// ALOAD DLOAD FLOAD LLOAD
		}
	}

	private void execute(final Location location, final StoreInstruction sins) {
		throw new Error(); // <--- !!!
		// ASTORE DSTORE FSTORE ISTORE LSTORE
	}

	private void execute(final Location location, final MONITORENTER monitorenter) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final MONITOREXIT monitorexit) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final NEWARRAY newarray) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final NOP nop) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final RET ret) {
		throw new Error(); // <--- !!!
	}

	private void execute(final Location location, final ReturnInstruction rins) {
		if (rins instanceof IRETURN) {
			Expression e = frame.pop();
			if (popFrame()) {
				frame.push(e);
			}
		} else if (rins instanceof RETURN) {
			popFrame();
		} else {
			throw new Error(); // <--- !!!
			// ARETURN DRETURN FRETURN LRETURN
		}
	}

	private void execute(final Location location, final StackInstruction sins) {
		if (sins instanceof POP) {
			frame.pop();
		} else {
			throw new Error(); // <--- !!!
			// DPU DUP_X1 DUP_X2 DUP2 DUP2_X1 DUP2_X2 POP2 SWAP
		}
	}

}
