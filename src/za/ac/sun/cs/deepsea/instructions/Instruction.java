package za.ac.sun.cs.deepsea.instructions;

import java.util.Map;
import java.util.Properties;

import com.sun.jdi.Location;
import com.sun.jdi.event.StepEvent;

import za.ac.sun.cs.deepsea.diver.Stepper;
import za.ac.sun.cs.deepsea.diver.Symbolizer;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.util.Configuration;

public abstract class Instruction {

	protected static int variableCount = 0;

	protected static final StringBuilder sb = new StringBuilder();

	protected final Stepper stepper;

	protected final int position;
	
	protected final int opcode;
	
	public Instruction(Stepper stepper, int position, int opcode) {
		this.stepper = stepper;
		this.position = position;
		this.opcode = opcode;
	}

	public int getPosition() {
		return position;
	}
	
	public int getOpcode() {
		return opcode;
	}

	public int getSize() {
		return 1;
	}

	public void execute(StepEvent event, Location loc, Symbolizer symbolizer) {
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("op:").append(opcode);
		sb.append(" [").append(getSize()).append(']');
		return sb.toString();
	}

	public static Instruction createInstruction(Stepper stepper, int offset, byte[] bytecodes) {
		int u0 = bytecodes[offset] & 0xFF;
		int u1 = (offset + 1 < bytecodes.length) ? (bytecodes[offset + 1] & 0xFF) : 0;
		int u2 = (offset + 2 < bytecodes.length) ? (bytecodes[offset + 2] & 0xFF) : 0;
		int u3 = (offset + 3 < bytecodes.length) ? (bytecodes[offset + 3] & 0xFF) : 0;
		int u4 = (offset + 4 < bytecodes.length) ? (bytecodes[offset + 4] & 0xFF) : 0;
		int s1 = (offset + 1 < bytecodes.length) ? bytecodes[offset + 1] : 0;
		int s2 = (offset + 2 < bytecodes.length) ? bytecodes[offset + 2] : 0;
		int opcode = u0;
		int operandU1 = u1;
		int operand3U1 = u3;
		int operandS1 = s1;
		int operand2S1 = s2;
		int operandU2 = (u1 << 8) | u2;
		int operandS2 = (s1 << 8) | u2;
		int operandS4 = (s1 << 24) | (u2 << 16) | (u3 << 8) | u4;
		switch (opcode) {
		case 0:
			return new NOP(stepper, offset);
		case 1:
			return new ACONST_NULL(stepper, offset);
		case 2:
			return new ICONST(stepper, offset, -1);
		case 3:
			return new ICONST(stepper, offset, 0);
		case 4:
			return new ICONST(stepper, offset, 1);
		case 5:
			return new ICONST(stepper, offset, 2);
		case 6:
			return new ICONST(stepper, offset, 3);
		case 7:
			return new ICONST(stepper, offset, 4);
		case 8:
			return new ICONST(stepper, offset, 5);
		case 9:
			return new LCONST(stepper, offset, 0);
		case 10:
			return new LCONST(stepper, offset, 1);
		case 11:
			return new FCONST(stepper, offset, 0);
		case 12:
			return new FCONST(stepper, offset, 1);
		case 13:
			return new FCONST(stepper, offset, 2);
		case 14:
			return new DCONST(stepper, offset, 0);
		case 15:
			return new DCONST(stepper, offset, 1);
		case 16:
			return new BIPUSH(stepper, offset, operandS1);
		case 17:
			return new SIPUSH(stepper, offset, operandS2);
		case 18:
			return new LDC(stepper, offset, operandU1);
		case 19:
			return new LDC_W(stepper, offset, operandU2);
		case 20:
			return new LDC2_W(stepper, offset, operandU2);
		case 21:
			return new ILOAD(stepper, offset, operandU1, false);
		case 22:
			return new LLOAD(stepper, offset, operandU1, false);
		case 23:
			return new FLOAD(stepper, offset, operandU1, false);
		case 24:
			return new DLOAD(stepper, offset, operandU1, false);
		case 25:
			return new ALOAD(stepper, offset, operandU1, false);
		case 26:
			return new ILOAD(stepper, offset, 0);
		case 27:
			return new ILOAD(stepper, offset, 1);
		case 28:
			return new ILOAD(stepper, offset, 2);
		case 29:
			return new ILOAD(stepper, offset, 3);
		case 30:
			return new LLOAD(stepper, offset, 0);
		case 31:
			return new LLOAD(stepper, offset, 1);
		case 32:
			return new LLOAD(stepper, offset, 2);
		case 33:
			return new LLOAD(stepper, offset, 3);
		case 34:
			return new FLOAD(stepper, offset, 0);
		case 35:
			return new FLOAD(stepper, offset, 1);
		case 36:
			return new FLOAD(stepper, offset, 2);
		case 37:
			return new FLOAD(stepper, offset, 3);
		case 38:
			return new DLOAD(stepper, offset, 0);
		case 39:
			return new DLOAD(stepper, offset, 1);
		case 40:
			return new DLOAD(stepper, offset, 2);
		case 41:
			return new DLOAD(stepper, offset, 3);
		case 42:
			return new ALOAD(stepper, offset, 0);
		case 43:
			return new ALOAD(stepper, offset, 1);
		case 44:
			return new ALOAD(stepper, offset, 2);
		case 45:
			return new ALOAD(stepper, offset, 3);
		case 46:
			return new IALOAD(stepper, offset);
		case 47:
			return new LALOAD(stepper, offset);
		case 48:
			return new FALOAD(stepper, offset);
		case 49:
			return new DALOAD(stepper, offset);
		case 50:
			return new AALOAD(stepper, offset);
		case 51:
			return new BALOAD(stepper, offset);
		case 52:
			return new CALOAD(stepper, offset);
		case 53:
			return new SALOAD(stepper, offset);
		case 54:
			return new ISTORE(stepper, offset, operandU1, false);
		case 55:
			return new LSTORE(stepper, offset, operandU1, false);
		case 56:
			return new FSTORE(stepper, offset, operandU1, false);
		case 57:
			return new DSTORE(stepper, offset, operandU1, false);
		case 58:
			return new ASTORE(stepper, offset, operandU1, false);
		case 59:
			return new ISTORE(stepper, offset, 0);
		case 60:
			return new ISTORE(stepper, offset, 1);
		case 61:
			return new ISTORE(stepper, offset, 2);
		case 62:
			return new ISTORE(stepper, offset, 3);
		case 63:
			return new LSTORE(stepper, offset, 0);
		case 64:
			return new LSTORE(stepper, offset, 1);
		case 65:
			return new LSTORE(stepper, offset, 2);
		case 66:
			return new LSTORE(stepper, offset, 3);
		case 67:
			return new FSTORE(stepper, offset, 0);
		case 68:
			return new FSTORE(stepper, offset, 1);
		case 69:
			return new FSTORE(stepper, offset, 2);
		case 70:
			return new FSTORE(stepper, offset, 3);
		case 71:
			return new DSTORE(stepper, offset, 0);
		case 72:
			return new DSTORE(stepper, offset, 1);
		case 73:
			return new DSTORE(stepper, offset, 2);
		case 74:
			return new DSTORE(stepper, offset, 3);
		case 75:
			return new ASTORE(stepper, offset, 0);
		case 76:
			return new ASTORE(stepper, offset, 1);
		case 77:
			return new ASTORE(stepper, offset, 2);
		case 78:
			return new ASTORE(stepper, offset, 3);
		case 79:
			return new IASTORE(stepper, offset);
		case 80:
			return new LASTORE(stepper, offset);
		case 81:
			return new FASTORE(stepper, offset);
		case 82:
			return new DASTORE(stepper, offset);
		case 83:
			return new AASTORE(stepper, offset);
		case 84:
			return new BASTORE(stepper, offset);
		case 85:
			return new CASTORE(stepper, offset);
		case 86:
			return new SASTORE(stepper, offset);
		case 87:
			return new POP(stepper, offset);
		case 88:
			return new POP2(stepper, offset);
		case 89:
			return new DUP(stepper, offset);
		case 90:
			return new DUP_X1(stepper, offset);
		case 91:
			return new DUP_X2(stepper, offset);
		case 92:
			return new DUP2(stepper, offset);
		case 93:
			return new DUP2_X1(stepper, offset);
		case 94:
			return new DUP2_X2(stepper, offset);
		case 95:
			return new SWAP(stepper, offset);
		case 96:
			return new IADD(stepper, offset);
		case 97:
			return new LADD(stepper, offset);
		case 98:
			return new FADD(stepper, offset);
		case 99:
			return new DADD(stepper, offset);
		case 100:
			return new ISUB(stepper, offset);
		case 101:
			return new LSUB(stepper, offset);
		case 102:
			return new FSUB(stepper, offset);
		case 103:
			return new DSUB(stepper, offset);
		case 104:
			return new IMUL(stepper, offset);
		case 105:
			return new LMUL(stepper, offset);
		case 106:
			return new FMUL(stepper, offset);
		case 107:
			return new DMUL(stepper, offset);
		case 108:
			return new IDIV(stepper, offset);
		case 109:
			return new LDIV(stepper, offset);
		case 110:
			return new FDIV(stepper, offset);
		case 111:
			return new DDIV(stepper, offset);
		case 112:
			return new IREM(stepper, offset);
		case 113:
			return new LREM(stepper, offset);
		case 114:
			return new FREM(stepper, offset);
		case 115:
			return new DREM(stepper, offset);
		case 116:
			return new INEG(stepper, offset);
		case 117:
			return new LNEG(stepper, offset);
		case 118:
			return new FNEG(stepper, offset);
		case 119:
			return new DNEG(stepper, offset);
		case 120:
			return new ISHL(stepper, offset);
		case 121:
			return new LSHL(stepper, offset);
		case 122:
			return new ISHR(stepper, offset);
		case 123:
			return new LSHR(stepper, offset);
		case 124:
			return new IUSHR(stepper, offset);
		case 125:
			return new LUSHR(stepper, offset);
		case 126:
			return new IAND(stepper, offset);
		case 127:
			return new LAND(stepper, offset);
		case 128:
			return new IOR(stepper, offset);
		case 129:
			return new LOR(stepper, offset);
		case 130:
			return new IXOR(stepper, offset);
		case 131:
			return new LXOR(stepper, offset);
		case 132:
			return new IINC(stepper, offset, operandU1, operand2S1);
		case 133:
			return new I2L(stepper, offset);
		case 134:
			return new I2F(stepper, offset);
		case 135:
			return new I2D(stepper, offset);
		case 136:
			return new L2I(stepper, offset);
		case 137:
			return new L2F(stepper, offset);
		case 138:
			return new L2D(stepper, offset);
		case 139:
			return new F2I(stepper, offset);
		case 140:
			return new F2L(stepper, offset);
		case 141:
			return new F2D(stepper, offset);
		case 142:
			return new D2I(stepper, offset);
		case 143:
			return new D2L(stepper, offset);
		case 144:
			return new D2F(stepper, offset);
		case 145:
			return new I2B(stepper, offset);
		case 146:
			return new I2C(stepper, offset);
		case 147:
			return new I2S(stepper, offset);
		case 148:
			return new LCMP(stepper, offset);
		case 149:
			return new FCMPL(stepper, offset);
		case 150:
			return new FCMPG(stepper, offset);
		case 151:
			return new DCMPL(stepper, offset);
		case 152:
			return new DCMPG(stepper, offset);
		case 153:
			return new IFEQ(stepper, offset, operandU2);
		case 154:
			return new IFNE(stepper, offset, operandU2);
		case 155:
			return new IFLT(stepper, offset, operandU2);
		case 156:
			return new IFGE(stepper, offset, operandU2);
		case 157:
			return new IFGT(stepper, offset, operandU2);
		case 158:
			return new IFLE(stepper, offset, operandU2);
		case 159:
			return new IF_ICMPEQ(stepper, offset, operandU2);
		case 160:
			return new IF_ICMPNE(stepper, offset, operandU2);
		case 161:
			return new IF_ICMPLT(stepper, offset, operandU2);
		case 162:
			return new IF_ICMPGE(stepper, offset, operandU2);
		case 163:
			return new IF_ICMPGT(stepper, offset, operandU2);
		case 164:
			return new IF_ICMPLE(stepper, offset, operandU2);
		case 165:
			return new IF_ACMPEQ(stepper, offset, operandU2);
		case 166:
			return new IF_ACMPNE(stepper, offset, operandU2);
		case 167:
			return new GOTO(stepper, offset, operandU2);
		case 168:
			return new JSR(stepper, offset, operandU2);
		case 169:
			return new RET(stepper, offset, operandU1);
		case 170: // return new TABLESWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 171: // return new LOOKUPSWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 172:
			return new IRETURN(stepper, offset);
		case 173:
			return new LRETURN(stepper, offset);
		case 174:
			return new FRETURN(stepper, offset);
		case 175:
			return new DRETURN(stepper, offset);
		case 176:
			return new ARETURN(stepper, offset);
		case 177:
			return new RETURN(stepper, offset);
		case 178:
			return new GETSTATIC(stepper, offset, operandU2);
		case 179:
			return new PUTSTATIC(stepper, offset, operandU2);
		case 180:
			return new GETFIELD(stepper, offset, operandU2);
		case 181:
			return new PUTFIELD(stepper, offset, operandU2);
		case 182:
			return new INVOKEVIRTUAL(stepper, offset, operandU2);
		case 183:
			return new INVOKESPECIAL(stepper, offset, operandU2);
		case 184:
			return new INVOKESTATIC(stepper, offset, operandU2);
		case 185:
			return new INVOKEINTERFACE(stepper, offset, operandU2, operand3U1);
		case 186:
			return new INVOKEDYNAMIC(stepper, offset, operandU2);
		case 187:
			return new NEW(stepper, offset, operandU2);
		case 188:
			return new NEWARRAY(stepper, offset, operandU1);
		case 189:
			return new ANEWARRAY(stepper, offset, operandU2);
		case 190:
			return new ARRAYLENGTH(stepper, offset);
		case 191:
			return new ATHROW(stepper, offset);
		case 192:
			return new CHECKCAST(stepper, offset, operandU2);
		case 193:
			return new INSTANCEOF(stepper, offset, operandU2);
		case 194:
			return new MONITORENTER(stepper, offset);
		case 195:
			return new MONITOREXIT(stepper, offset);
		case 196: // return new WIDE();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 197:
			return new MULTIANEWARRAY(stepper, offset, operandU2, operand3U1);
		case 198:
			return new IFNULL(stepper, offset, operandU2);
		case 199:
			return new IFNONNULL(stepper, offset, operandU2);
		case 200:
			return new GOTO_W(stepper, offset, operandS4);
		case 201:
			return new JSR_W(stepper, offset, operandS4);
		default:
			throw new Error("Unhandled instruction, opcode == " + opcode);
		}
	}

	public static void map(Stepper stepper, byte[] bytecodes, String key, Map<String, Instruction> map) {
		int offset = 0;
		while (offset < bytecodes.length) {
			Instruction ins = createInstruction(stepper, offset, bytecodes);
			map.put(key + "." + offset, ins);
			offset += ins.getSize();
		}
	}

	private static Green solver = null;

	public static Expression simplify(Expression expression) {
		if (solver == null) {
			solver = new Green("DS-SIMPLIFIER");
			Properties properties = new Properties();
			properties.setProperty("green.log.level", "OFF");
			properties.setProperty("green.services", "simplify");
			properties.setProperty("green.service.simplify", "canonizer)");
			properties.setProperty("green.service.simplify.canonizer", "za.ac.sun.cs.green.service.canonizer.SATLeafCanonizerService");				
			Configuration config = new Configuration(solver, properties);
			config.configure();
		}
		return (Expression) new Instance(solver, null, expression).request("simplify");
	}

}
