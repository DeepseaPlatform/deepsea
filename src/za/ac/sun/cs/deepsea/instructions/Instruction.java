package za.ac.sun.cs.deepsea.instructions;

import java.util.Map;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.Symbolizer;

public abstract class Instruction {

	protected static final StringBuilder sb = new StringBuilder();

	private final int opcode;

	public Instruction(int opcode) {
		this.opcode = opcode;
	}

	public int getOpcode() {
		return opcode;
	}

	public int getSize() {
		return 1;
	}

	public void execute(Location loc, Symbolizer symbolizer) {
	}

	@Override
	public String toString() {
		sb.setLength(0);
		sb.append("op:").append(opcode);
		sb.append(" [").append(getSize()).append(']');
		return sb.toString();
	}

	public static Instruction createInstruction(int offset, byte[] bytecodes) {
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
			return new NOP();
		case 1:
			return new ACONST_NULL();
		case 2:
			return new ICONST(-1);
		case 3:
			return new ICONST(0);
		case 4:
			return new ICONST(1);
		case 5:
			return new ICONST(2);
		case 6:
			return new ICONST(3);
		case 7:
			return new ICONST(4);
		case 8:
			return new ICONST(5);
		case 9:
			return new LCONST(0);
		case 10:
			return new LCONST(1);
		case 11:
			return new FCONST(0);
		case 12:
			return new FCONST(1);
		case 13:
			return new FCONST(2);
		case 14:
			return new DCONST(0);
		case 15:
			return new DCONST(1);
		case 16:
			return new BIPUSH(operandS1);
		case 17:
			return new SIPUSH(operandS2);
		case 18:
			return new LDC(operandU1);
		case 19:
			return new LDC_W(operandU2);
		case 20:
			return new LDC2_W(operandU2);
		case 21:
			return new ILOAD(operandU1, false);
		case 22:
			return new LLOAD(operandU1, false);
		case 23:
			return new FLOAD(operandU1, false);
		case 24:
			return new DLOAD(operandU1, false);
		case 25:
			return new ALOAD(operandU1, false);
		case 26:
			return new ILOAD(0);
		case 27:
			return new ILOAD(1);
		case 28:
			return new ILOAD(2);
		case 29:
			return new ILOAD(3);
		case 30:
			return new LLOAD(0);
		case 31:
			return new LLOAD(1);
		case 32:
			return new LLOAD(2);
		case 33:
			return new LLOAD(3);
		case 34:
			return new FLOAD(0);
		case 35:
			return new FLOAD(1);
		case 36:
			return new FLOAD(2);
		case 37:
			return new FLOAD(3);
		case 38:
			return new DLOAD(0);
		case 39:
			return new DLOAD(1);
		case 40:
			return new DLOAD(2);
		case 41:
			return new DLOAD(3);
		case 42:
			return new ALOAD(0);
		case 43:
			return new ALOAD(1);
		case 44:
			return new ALOAD(2);
		case 45:
			return new ALOAD(3);
		case 46:
			return new IALOAD();
		case 47:
			return new LALOAD();
		case 48:
			return new FALOAD();
		case 49:
			return new DALOAD();
		case 50:
			return new AALOAD();
		case 51:
			return new BALOAD();
		case 52:
			return new CALOAD();
		case 53:
			return new SALOAD();
		case 54:
			return new ISTORE(operandU1, false);
		case 55:
			return new LSTORE(operandU1, false);
		case 56:
			return new FSTORE(operandU1, false);
		case 57:
			return new DSTORE(operandU1, false);
		case 58:
			return new ASTORE(operandU1, false);
		case 59:
			return new ISTORE(0);
		case 60:
			return new ISTORE(1);
		case 61:
			return new ISTORE(2);
		case 62:
			return new ISTORE(3);
		case 63:
			return new LSTORE(0);
		case 64:
			return new LSTORE(1);
		case 65:
			return new LSTORE(2);
		case 66:
			return new LSTORE(3);
		case 67:
			return new FSTORE(0);
		case 68:
			return new FSTORE(1);
		case 69:
			return new FSTORE(2);
		case 70:
			return new FSTORE(3);
		case 71:
			return new DSTORE(0);
		case 72:
			return new DSTORE(1);
		case 73:
			return new DSTORE(2);
		case 74:
			return new DSTORE(3);
		case 75:
			return new ASTORE(0);
		case 76:
			return new ASTORE(1);
		case 77:
			return new ASTORE(2);
		case 78:
			return new ASTORE(3);
		case 79:
			return new IASTORE();
		case 80:
			return new LASTORE();
		case 81:
			return new FASTORE();
		case 82:
			return new DASTORE();
		case 83:
			return new AASTORE();
		case 84:
			return new BASTORE();
		case 85:
			return new CASTORE();
		case 86:
			return new SASTORE();
		case 87:
			return new POP();
		case 88:
			return new POP2();
		case 89:
			return new DUP();
		case 90:
			return new DUP_X1();
		case 91:
			return new DUP_X2();
		case 92:
			return new DUP2();
		case 93:
			return new DUP2_X1();
		case 94:
			return new DUP2_X2();
		case 95:
			return new SWAP();
		case 96:
			return new IADD();
		case 97:
			return new LADD();
		case 98:
			return new FADD();
		case 99:
			return new DADD();
		case 100:
			return new ISUB();
		case 101:
			return new LSUB();
		case 102:
			return new FSUB();
		case 103:
			return new DSUB();
		case 104:
			return new IMUL();
		case 105:
			return new LMUL();
		case 106:
			return new FMUL();
		case 107:
			return new DMUL();
		case 108:
			return new IDIV();
		case 109:
			return new LDIV();
		case 110:
			return new FDIV();
		case 111:
			return new DDIV();
		case 112:
			return new IREM();
		case 113:
			return new LREM();
		case 114:
			return new FREM();
		case 115:
			return new DREM();
		case 116:
			return new INEG();
		case 117:
			return new LNEG();
		case 118:
			return new FNEG();
		case 119:
			return new DNEG();
		case 120:
			return new ISHL();
		case 121:
			return new LSHL();
		case 122:
			return new ISHR();
		case 123:
			return new LSHR();
		case 124:
			return new IUSHR();
		case 125:
			return new LUSHR();
		case 126:
			return new IAND();
		case 127:
			return new LAND();
		case 128:
			return new IOR();
		case 129:
			return new LOR();
		case 130:
			return new IXOR();
		case 131:
			return new LXOR();
		case 132:
			return new IINC(operandU1, operand2S1);
		case 133:
			return new I2L();
		case 134:
			return new I2F();
		case 135:
			return new I2D();
		case 136:
			return new L2I();
		case 137:
			return new L2F();
		case 138:
			return new L2D();
		case 139:
			return new F2I();
		case 140:
			return new F2L();
		case 141:
			return new F2D();
		case 142:
			return new D2I();
		case 143:
			return new D2L();
		case 144:
			return new D2F();
		case 145:
			return new I2B();
		case 146:
			return new I2C();
		case 147:
			return new I2S();
		case 148:
			return new LCMP();
		case 149:
			return new FCMPL();
		case 150:
			return new FCMPG();
		case 151:
			return new DCMPL();
		case 152:
			return new DCMPG();
		case 153:
			return new IFEQ(operandU2);
		case 154:
			return new IFNE(operandU2);
		case 155:
			return new IFLT(operandU2);
		case 156:
			return new IFGE(operandU2);
		case 157:
			return new IFGT(operandU2);
		case 158:
			return new IFLE(operandU2);
		case 159:
			return new IF_ICMPEQ(operandU2);
		case 160:
			return new IF_ICMPNE(operandU2);
		case 161:
			return new IF_ICMPLT(operandU2);
		case 162:
			return new IF_ICMPGE(operandU2);
		case 163:
			return new IF_ICMPGT(operandU2);
		case 164:
			return new IF_ICMPLE(operandU2);
		case 165:
			return new IF_ACMPEQ(operandU2);
		case 166:
			return new IF_ACMPNE(operandU2);
		case 167:
			return new GOTO(operandU2);
		case 168:
			return new JSR(operandU2);
		case 169:
			return new RET(operandU1);
		case 170: // return new TABLESWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 171: // return new LOOKUPSWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 172:
			return new IRETURN();
		case 173:
			return new LRETURN();
		case 174:
			return new FRETURN();
		case 175:
			return new DRETURN();
		case 176:
			return new ARETURN();
		case 177:
			return new RETURN();
		case 178:
			return new GETSTATIC(operandU2);
		case 179:
			return new PUTSTATIC(operandU2);
		case 180:
			return new GETFIELD(operandU2);
		case 181:
			return new PUTFIELD(operandU2);
		case 182:
			return new INVOKEVIRTUAL(operandU2);
		case 183:
			return new INVOKESPECIAL(operandU2);
		case 184:
			return new INVOKESTATIC(operandU2);
		case 185:
			return new INVOKEINTERFACE(operandU2, operand3U1);
		case 186:
			return new INVOKEDYNAMIC(operandU2);
		case 187:
			return new NEW(operandU2);
		case 188:
			return new NEWARRAY(operandU1);
		case 189:
			return new ANEWARRAY(operandU2);
		case 190:
			return new ARRAYLENGTH();
		case 191:
			return new ATHROW();
		case 192:
			return new CHECKCAST(operandU2);
		case 193:
			return new INSTANCEOF(operandU2);
		case 194:
			return new MONITORENTER();
		case 195:
			return new MONITOREXIT();
		case 196: // return new WIDE();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 197:
			return new MULTIANEWARRAY(operandU2, operand3U1);
		case 198:
			return new IFNULL(operandU2);
		case 199:
			return new IFNONNULL(operandU2);
		case 200:
			return new GOTO_W(operandS4);
		case 201:
			return new JSR_W(operandS4);
		default:
			throw new Error("Unhandled instruction, opcode == " + opcode);
		}
	}

	public static void map(byte[] bytecodes, String key, Map<String, Instruction> map) {
		int offset = 0;
		while (offset < bytecodes.length) {
			Instruction ins = createInstruction(offset, bytecodes);
			map.put(key + "." + offset, ins);
			offset += ins.getSize();
		}
	}

}
