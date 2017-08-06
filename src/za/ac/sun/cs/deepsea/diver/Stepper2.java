package za.ac.sun.cs.deepsea.diver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import com.sun.jdi.Method;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.request.StepRequest;

import za.ac.sun.cs.deepsea.agent.AbstractEventListener;
import za.ac.sun.cs.deepsea.agent.RequestManager;

public class Stepper2 extends AbstractEventListener {

	private static final int NOP = 0;
	private static final int ACONST_NULL = 1;
	private static final int ICONST_M1 = 2;
	private static final int ICONST_0 = 3;
	private static final int ICONST_1 = 4;
	private static final int ICONST_2 = 5;
	private static final int ICONST_3 = 6;
	private static final int ICONST_4 = 7;
	private static final int ICONST_5 = 8;
	private static final int LCONST_0 = 9;
	private static final int LCONST_1 = 10;
	private static final int FCONST_0 = 11;
	private static final int FCONST_1 = 12;
	private static final int FCONST_2 = 13;
	private static final int DCONST_0 = 14;
	private static final int DCONST_1 = 15;
	private static final int BIPUSH = 16;
	private static final int SIPUSH = 17;
	private static final int LDC = 18;
	private static final int LDC_W = 19;
	private static final int LDC2_W = 20;
	private static final int ILOAD = 21;
	private static final int LLOAD = 22;
	private static final int FLOAD = 23;
	private static final int DLOAD = 24;
	private static final int ALOAD = 25;
	private static final int ILOAD_0 = 26;
	private static final int ILOAD_1 = 27;
	private static final int ILOAD_2 = 28;
	private static final int ILOAD_3 = 29;
	private static final int LLOAD_0 = 30;
	private static final int LLOAD_1 = 31;
	private static final int LLOAD_2 = 32;
	private static final int LLOAD_3 = 33;
	private static final int FLOAD_0 = 34;
	private static final int FLOAD_1 = 35;
	private static final int FLOAD_2 = 36;
	private static final int FLOAD_3 = 37;
	private static final int DLOAD_0 = 38;
	private static final int DLOAD_1 = 39;
	private static final int DLOAD_2 = 40;
	private static final int DLOAD_3 = 41;
	private static final int ALOAD_0 = 42;
	private static final int ALOAD_1 = 43;
	private static final int ALOAD_2 = 44;
	private static final int ALOAD_3 = 45;
	private static final int IALOAD = 46;
	private static final int LALOAD = 47;
	private static final int FALOAD = 48;
	private static final int DALOAD = 49;
	private static final int AALOAD = 50;
	private static final int BALOAD = 51;
	private static final int CALOAD = 52;
	private static final int SALOAD = 53;
	private static final int ISTORE = 54;
	private static final int LSTORE = 55;
	private static final int FSTORE = 56;
	private static final int DSTORE = 57;
	private static final int ASTORE = 58;
	private static final int ISTORE_0 = 59;
	private static final int ISTORE_1 = 60;
	private static final int ISTORE_2 = 61;
	private static final int ISTORE_3 = 62;
	private static final int LSTORE_0 = 63;
	private static final int LSTORE_1 = 64;
	private static final int LSTORE_2 = 65;
	private static final int LSTORE_3 = 66;
	private static final int FSTORE_0 = 67;
	private static final int FSTORE_1 = 68;
	private static final int FSTORE_2 = 69;
	private static final int FSTORE_3 = 70;
	private static final int DSTORE_0 = 71;
	private static final int DSTORE_1 = 72;
	private static final int DSTORE_2 = 73;
	private static final int DSTORE_3 = 74;
	private static final int ASTORE_0 = 75;
	private static final int ASTORE_1 = 76;
	private static final int ASTORE_2 = 77;
	private static final int ASTORE_3 = 78;
	private static final int IASTORE = 79;
	private static final int LASTORE = 80;
	private static final int FASTORE = 81;
	private static final int DASTORE = 82;
	private static final int AASTORE = 83;
	private static final int BASTORE = 84;
	private static final int CASTORE = 85;
	private static final int SASTORE = 86;
	private static final int POP = 87;
	private static final int POP2 = 88;
	private static final int DUP = 89;
	private static final int DUP_X1 = 90;
	private static final int DUP_X2 = 91;
	private static final int DUP2 = 92;
	private static final int DUP2_X1 = 93;
	private static final int DUP2_X2 = 94;
	private static final int SWAP = 95;
	private static final int IADD = 96;
	private static final int LADD = 97;
	private static final int FADD = 98;
	private static final int DADD = 99;
	private static final int ISUB = 100;
	private static final int LSUB = 101;
	private static final int FSUB = 102;
	private static final int DSUB = 103;
	private static final int IMUL = 104;
	private static final int LMUL = 105;
	private static final int FMUL = 106;
	private static final int DMUL = 107;
	private static final int IDIV = 108;
	private static final int LDIV = 109;
	private static final int FDIV = 110;
	private static final int DDIV = 111;
	private static final int IREM = 112;
	private static final int LREM = 113;
	private static final int FREM = 114;
	private static final int DREM = 115;
	private static final int INEG = 116;
	private static final int LNEG = 117;
	private static final int FNEG = 118;
	private static final int DNEG = 119;
	private static final int ISHL = 120;
	private static final int LSHL = 121;
	private static final int ISHR = 122;
	private static final int LSHR = 123;
	private static final int IUSHR = 124;
	private static final int LUSHR = 125;
	private static final int IAND = 126;
	private static final int LAND = 127;
	private static final int IOR = 128;
	private static final int LOR = 129;
	private static final int IXOR = 130;
	private static final int LXOR = 131;
	private static final int IINC = 132;
	private static final int I2L = 133;
	private static final int I2F = 134;
	private static final int I2D = 135;
	private static final int L2I = 136;
	private static final int L2F = 137;
	private static final int L2D = 138;
	private static final int F2I = 139;
	private static final int F2L = 140;
	private static final int F2D = 141;
	private static final int D2I = 142;
	private static final int D2L = 143;
	private static final int D2F = 144;
	private static final int I2B = 145;
	private static final int I2C = 146;
	private static final int I2S = 147;
	private static final int LCMP = 148;
	private static final int FCMPL = 149;
	private static final int FCMPG = 150;
	private static final int DCMPL = 151;
	private static final int DCMPG = 152;
	private static final int IFEQ = 153;
	private static final int IFNE = 154;
	private static final int IFLT = 155;
	private static final int IFGE = 156;
	private static final int IFGT = 157;
	private static final int IFLE = 158;
	private static final int IF_ICMPEQ = 159;
	private static final int IF_ICMPNE = 160;
	private static final int IF_ICMPLT = 161;
	private static final int IF_ICMPGE = 162;
	private static final int IF_ICMPGT = 163;
	private static final int IF_ICMPLE = 164;
	private static final int IF_ACMPEQ = 165;
	private static final int IF_ACMPNE = 166;
	private static final int GOTO = 167;
	private static final int JSR = 168;
	private static final int RET = 169;
	private static final int TABLESWITCH = 170;
	private static final int LOOKUPSWITCH = 171;
	private static final int IRETURN = 172;
	private static final int LRETURN = 173;
	private static final int FRETURN = 174;
	private static final int DRETURN = 175;
	private static final int ARETURN = 176;
	private static final int RETURN = 177;
	private static final int GETSTATIC = 178;
	private static final int PUTSTATIC = 179;
	private static final int GETFIELD = 180;
	private static final int PUTFIELD = 181;
	private static final int INVOKEVIRTUAL = 182;
	private static final int INVOKESPECIAL = 183;
	private static final int INVOKESTATIC = 184;
	private static final int INVOKEINTERFACE = 185;
	private static final int INVOKEDYNAMIC = 186;
	private static final int NEW = 187;
	private static final int NEWARRAY = 188;
	private static final int ANEWARRAY = 189;
	private static final int ARRAYLENGTH = 190;
	private static final int ATHROW = 191;
	private static final int CHECKCAST = 192;
	private static final int INSTANCEOF = 193;
	private static final int MONITORENTER = 194;
	private static final int MONITOREXIT = 195;
	private static final int WIDE = 196;
	private static final int MULTIANEWARRAY = 197;
	private static final int IFNULL = 198;
	private static final int IFNONNULL = 199;
	private static final int GOTO_W = 200;
	private static final int JSR_W = 201;

	private final Diver diver;

	private final Logger log;

	private final RequestManager mgr;

//	private final Symbolizer symbolizer;

//	private final Repository repo = new ClassLoaderRepository(this.getClass().getClassLoader());

	private final Map<String, InsnList> instructionListMap = new HashMap<>();

//	private final Map<String, ClassGen> genMap = new HashMap<>();

//	private final StringBuilder sb = new StringBuilder();

	public Stepper2(final Diver diver, Symbolizer symbolizer, final RequestManager mgr) {
		this.diver = diver;
		this.log = diver.getLog();
		this.mgr = mgr;
//		this.symbolizer = symbolizer;
	}

	@Override
	public boolean step(StepEvent event) {
		log.info("STEP");
//		Location loc = event.location();
//		String clsName = loc.declaringType().name();
//		String methodName = loc.method().name();
//		long bci = loc.codeIndex();
//		InstructionHandle handle = insMap.get(clsName + ":" + methodName + ":" + bci);
//		if (handle == null) {
//			sb.setLength(0);
//			sb.append("class:" + clsName);
//			sb.append(" method:" + methodName);
//			sb.append(" bci:" + bci);
//			log.finest(sb.toString());
//		} else {
//			ClassGen cgen = genMap.get(clsName);
//			symbolizer.execute(loc, cgen, handle);
//			//			if (handle.getInstruction() instanceof InvokeInstruction) {
//			//				InvokeInstruction iins = (InvokeInstruction) handle.getInstruction();
//			//				int argc = iins.getArgumentTypes(cgen.getConstantPool()).length;
//			//			}
//			sb.setLength(0);
//			sb.append(methodName).append(' ').append(handle.toString());
//			log.finest(sb.toString());
//		}

		// ---- Schedule the next StepRequest 
		mgr.removeRequest(event.request());
		mgr.createStepRequest(event.thread(), StepRequest.STEP_MIN, StepRequest.STEP_INTO, r -> {
			mgr.filterExcludes(r);
			r.addCountFilter(1);
		});
		return true;
	}

	@Override
	public boolean classPrepare(ClassPrepareEvent event) {
		final String className = event.referenceType().name();
		try {
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(className);
			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
			@SuppressWarnings("unchecked")
			List<MethodNode> methods = classNode.methods;
			for (MethodNode methodNode : methods) {
				String key = methodNode.name + methodNode.desc;
				instructionListMap.put(key, methodNode.instructions);
			}
			log.finer(">>>> loaded " + className);
		} catch (IOException e) {
			log.finer(">>>> could not load " + className);
		}
		return true;
	}
	
	@Override
	public boolean methodEntry(MethodEntryEvent event) {
		Method method = event.method();
		log.info("ENTRY " + method.signature());
		byte[] bytecodes = method.bytecodes();
		StringBuilder sb = new StringBuilder();
		sb.append(bytecodes[0]);
		for (int i = 1; i < 10 && i < bytecodes.length; i++) {
			sb.append(' ').append(bytecodes[i]);
		}
		log.info(sb.toString());
		return true;
	}

//		for (Method method : event.referenceType().allMethods()) {
//			log.info(method.genericSignature());
//		}
//		final String className = event.referenceType().name();
//		try {
//			ClassNode classNode = new ClassNode();
//			ClassReader classReader = new ClassReader(className);
//			classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
//			@SuppressWarnings("unchecked")
//			List<MethodNode> methods = classNode.methods;
//			for (MethodNode methodNode : methods) {
//				if (methodNode.instructions.size() <= 0) {
//					continue;
//				}
//				String methodName = className + "." + methodNode.name + ".";
//				int offset = 0;
////				for (AbstractInsnNode ins = methodNode.instructions.getFirst(); ins != null; ins = ins.getNext()) {
////					if (ins.getOpcode() == -1) {
////						continue;
////					}
////					insMap.put(methodName + offset, ins);
////					switch (ins.getOpcode()) {
////					case AALOAD:
////					case AASTORE:
////					case ACONST_NULL:
////					case ALOAD_0:
////					case ALOAD_1:
////					case ALOAD_2:
////					case ALOAD_3:
////					case ARETURN:
////					case ARRAYLENGTH:
////					case ASTORE_0:
////					case ASTORE_1:
////					case ASTORE_2:
////					case ASTORE_3:
////					case ATHROW:
////					case BALOAD:
////					case BASTORE:
////					case CALOAD:
////					case CASTORE:
////					case D2F:
////					case D2I:
////					case D2L:
////					case DADD:
////					case DALOAD:
////					case DASTORE:
////					case DCMPG:
////					case DCMPL:
////					case DCONST_0:
////					case DCONST_1:
////					case DDIV:
////					case DLOAD_0:
////					case DLOAD_1:
////					case DLOAD_2:
////					case DLOAD_3:
////					case DMUL:
////					case DNEG:
////					case DREM:
////					case DRETURN:
////					case DSTORE_0:
////					case DSTORE_1:
////					case DSTORE_2:
////					case DSTORE_3:
////					case DSUB:
////					case DUP:
////					case DUP2:
////					case DUP2_X1:
////					case DUP2_X2:
////					case DUP_X1:
////					case DUP_X2:
////					case F2D:
////					case F2I:
////					case F2L:
////					case FADD:
////					case FALOAD:
////					case FASTORE:
////					case FCMPG:
////					case FCMPL:
////					case FCONST_0:
////					case FCONST_1:
////					case FCONST_2:
////					case FDIV:
////					case FLOAD_0:
////					case FLOAD_1:
////					case FLOAD_2:
////					case FLOAD_3:
////					case FMUL:
////					case FNEG:
////					case FREM:
////					case FRETURN:
////					case FSTORE_0:
////					case FSTORE_1:
////					case FSTORE_2:
////					case FSTORE_3:
////					case FSUB:
////					case I2B:
////					case I2C:
////					case I2D:
////					case I2F:
////					case I2L:
////					case I2S:
////					case IADD:
////					case IALOAD:
////					case IAND:
////					case IASTORE:
////					case ICONST_0:
////					case ICONST_1:
////					case ICONST_2:
////					case ICONST_3:
////					case ICONST_4:
////					case ICONST_5:
////					case ICONST_M1:
////					case IDIV:
////					case ILOAD_0:
////					case ILOAD_1:
////					case ILOAD_2:
////					case ILOAD_3:
////					case IMUL:
////					case INEG:
////					case IOR:
////					case IREM:
////					case IRETURN:
////					case ISHL:
////					case ISHR:
////					case ISTORE_0:
////					case ISTORE_1:
////					case ISTORE_2:
////					case ISTORE_3:
////					case ISUB:
////					case IUSHR:
////					case IXOR:
////					case L2D:
////					case L2F:
////					case L2I:
////					case LADD:
////					case LALOAD:
////					case LAND:
////					case LASTORE:
////					case LCMP:
////					case LCONST_0:
////					case LCONST_1:
////					case LDIV:
////					case LLOAD_0:
////					case LLOAD_1:
////					case LLOAD_2:
////					case LLOAD_3:
////					case LMUL:
////					case LNEG:
////					case LOR:
////					case LREM:
////					case LRETURN:
////					case LSHL:
////					case LSHR:
////					case LSTORE_0:
////					case LSTORE_1:
////					case LSTORE_2:
////					case LSTORE_3:
////					case LSUB:
////					case LUSHR:
////					case LXOR:
////					case MONITORENTER:
////					case MONITOREXIT:
////					case NOP:
////					case POP:
////					case POP2:
////					case RETURN:
////					case SALOAD:
////					case SASTORE:
////					case SWAP:
////						offset += 1;
////						break;
////					case ALOAD:
////					case ASTORE:
////					case BIPUSH:
////					case DLOAD:
////					case DSTORE:
////					case FLOAD:
////					case FSTORE:
////					case ILOAD:
////					case ISTORE:
////					case LDC:
////					case LLOAD:
////					case LSTORE:
////					case NEWARRAY:
////					case RET:
////						offset += 2;
////						break;
////					case ANEWARRAY:
////					case CHECKCAST:
////					case GETFIELD:
////					case GETSTATIC:
////					case GOTO:
////					case IFEQ:
////					case IFGE:
////					case IFGT:
////					case IFLE:
////					case IFLT:
////					case IFNE:
////					case IFNONNULL:
////					case IFNULL:
////					case IF_ACMPEQ:
////					case IF_ACMPNE:
////					case IF_ICMPEQ:
////					case IF_ICMPGE:
////					case IF_ICMPGT:
////					case IF_ICMPLE:
////					case IF_ICMPLT:
////					case IF_ICMPNE:
////					case IINC:
////					case INSTANCEOF:
////					case INVOKESPECIAL:
////					case INVOKESTATIC:
////					case INVOKEVIRTUAL:
////					case JSR:
////					case LDC2_W:
////					case LDC_W:
////					case NEW:
////					case PUTFIELD:
////					case PUTSTATIC:
////					case SIPUSH:
////						offset += 3;
////						break;
////					case MULTIANEWARRAY:
////						offset += 4;
////						break;
////					case GOTO_W:
////					case JSR_W:
////					case INVOKEDYNAMIC:
////					case INVOKEINTERFACE:
////						offset += 5;
////						break;
//////					case LOOKUPSWITCH:
//////					case TABLESWITCH:
//////					case WIDE:
////					default:
////						throw new Error("Unhandled instruction, opcode == " + ins.getOpcode());
////					}
////				}
//			}
//			log.finer(">>>> loaded " + className);
////			ClassReader classReader = new ClassReader(className);
////			ClassWriter classWriter = new ClassWriter(ClassReader.EXPAND_FRAMES);
////			ClassVisitor classVisitor = new ClassAdaptor(classWriter);
////			classReader.accept(classVisitor, 0);
////			// classWriter.toByteArray();
////			log.finer(">>>> loaded " + className);
//		} catch (IOException e) {
//			log.finer(">>>> could not load " + className);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//	private class ClassAdaptor extends ClassVisitor {
//
//		public ClassAdaptor(final ClassVisitor classVisitor) {
//			super(Opcodes.ASM5, classVisitor);
//		}
//
//		@Override
//		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//			MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
//			return methodVisitor;
//		}
//
//	}

//		try {
//			/*
//			 * Load the class locally.
//			 */
//			JavaClass cls = repo.loadClass(className);
//			/*
//			 * For each method, obtain its bytecode, and store it in insMap.
//			 */
//			for (Method mth : cls.getMethods()) {
//				String prefix = className + ":" + mth.getName();
//				InstructionList insList = new InstructionList(mth.getCode().getCode());
//				int[] insOfs = insList.getInstructionPositions();
//				InstructionHandle[] insHdl = insList.getInstructionHandles();
//				int n = insList.getLength();
//				for (int i = 0; i < n; i++) {
//					insMap.put(prefix + ":" + insOfs[i], insHdl[i]);
//				}
//			}
//			/*
//			 * Create a ClassGen object based on this class and store it in
//			 * genMap. This is used to obtain a reference to its ConstantPoolGen
//			 * component which is needed for determining the number of arguments
//			 * passed during function calls. The whole ClassGen is stored in
//			 * case it is needed for future extensions.
//			 */
//			ClassGen cg = new ClassGen(cls);
//			genMap.put(className, cg);
//			/*
//			 * Add a static field to the class to signal that method arguments
//			 * must be modified.
//			 */
//			ConstantPoolGen cpg = cg.getConstantPool();
////			int af = Const.ACC_PRIVATE | Const.ACC_STATIC;
////			FieldGen bypassFlag = new FieldGen(af, BasicType.BOOLEAN, "$dp$bypassFlag", cpg);
////			// bypassFlag.setInitValue(true);
////			cg.addField(bypassFlag.getField());
//			/*
//			 * For each method, check if it is a trigger. If so, add
//			 * instructions
//			 */
//			for (Method m : cg.getMethods()) {
//				MethodGen mg = modifyMethod(m, className, cpg);
//				if (mg != null) {
//					cg.replaceMethod(m, mg.getMethod());
//				}
//			}
//			/*
//			 * Refine the class on the target machine.
//			 */
//			try {
//				FileOutputStream fos = new FileOutputStream("/tmp/x.class");
//				fos.write(cg.getJavaClass().getBytes());
//				fos.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			Map<ReferenceType, byte[]> map = new HashMap<>();
//			map.put(event.referenceType(), cg.getJavaClass().getBytes());
//			VirtualMachine vm = mgr.getVM();
//			vm.redefineClasses(map);
//			/*
//			 * Log that the class has been loaded.
//			 */
//			log.finer(">>>> loaded " + className);
//		} catch (ClassNotFoundException e) {
//			log.finer(">>>> could not load " + className);
//		}
//		return true;
//	}

//	private MethodGen modifyMethod(Method m, String className, ConstantPoolGen cpg) {
//		Iterator<Trigger> ti = diver.findTriggers(m, className);
//		while (ti.hasNext()) {
//			Trigger tr = ti.next();
//			MethodGen mg = new MethodGen(m, className, cpg);
////			InstructionList ol = new InstructionList(mg.getInstructionList().getByteCode());
//			// InstructionList il = new InstructionList(new NOP());
//			// InstructionFactory f = new InstructionFactory(cpg);
////			ol.insert(new NOP());
////			ol.setPositions(true);
//			// il.append(new NOP());
////			// il.append(f.createGetStatic(className, "$dp$bypassFlag", Type.BOOLEAN));
////			il.append(f.createConstant(0));
////			BranchInstruction b = new IFEQ(null); 
////			il.append(b);
////			il.append(f.createConstant(22));
////			il.append(InstructionFactory.createStore(Type.INT, 0));
////			b.setTarget(ol.getStart());
//			// ol.insert(il);
////			mg.setInstructionList(ol);
//			// il.dispose();
//			// ol.dispose();
////			mg.setMaxLocals();
////			mg.setMaxStack();
//			Attribute[] as = mg.getCodeAttributes();
//			StackMap sm = null; 
//			for (Attribute a : as) {
//				if (a instanceof StackMap) {
//					sm = (StackMap) a;
//					break;
//				}
//			}
////			StackMapEntry sme = sm.getStackMap()[0];
////			sme.setByteCodeOffset(18);
//			System.out.println(sm);
//			return mg;
//		}
//		return null;
//	}

}
