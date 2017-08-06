package za.ac.sun.cs.deepsea.instructions;

import java.util.Map;

import com.sun.jdi.Location;

import za.ac.sun.cs.deepsea.diver.Symbolizer;

public abstract class Instruction {

	protected static final StringBuilder sb = new StringBuilder();

	protected final int position;

	protected final int opcode;
	
	public Instruction(final int position, final int opcode) {
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
			return new NOP(offset);
		case 1:
			return new ACONST_NULL(offset);
		case 2:
			return new ICONST(offset, -1);
		case 3:
			return new ICONST(offset, 0);
		case 4:
			return new ICONST(offset, 1);
		case 5:
			return new ICONST(offset, 2);
		case 6:
			return new ICONST(offset, 3);
		case 7:
			return new ICONST(offset, 4);
		case 8:
			return new ICONST(offset, 5);
		case 9:
			return new LCONST(offset, 0);
		case 10:
			return new LCONST(offset, 1);
		case 11:
			return new FCONST(offset, 0);
		case 12:
			return new FCONST(offset, 1);
		case 13:
			return new FCONST(offset, 2);
		case 14:
			return new DCONST(offset, 0);
		case 15:
			return new DCONST(offset, 1);
		case 16:
			return new BIPUSH(offset, operandS1);
		case 17:
			return new SIPUSH(offset, operandS2);
		case 18:
			return new LDC(offset, operandU1);
		case 19:
			return new LDC_W(offset, operandU2);
		case 20:
			return new LDC2_W(offset, operandU2);
		case 21:
			return new ILOAD(offset, operandU1, false);
		case 22:
			return new LLOAD(offset, operandU1, false);
		case 23:
			return new FLOAD(offset, operandU1, false);
		case 24:
			return new DLOAD(offset, operandU1, false);
		case 25:
			return new ALOAD(offset, operandU1, false);
		case 26:
			return new ILOAD(offset, 0);
		case 27:
			return new ILOAD(offset, 1);
		case 28:
			return new ILOAD(offset, 2);
		case 29:
			return new ILOAD(offset, 3);
		case 30:
			return new LLOAD(offset, 0);
		case 31:
			return new LLOAD(offset, 1);
		case 32:
			return new LLOAD(offset, 2);
		case 33:
			return new LLOAD(offset, 3);
		case 34:
			return new FLOAD(offset, 0);
		case 35:
			return new FLOAD(offset, 1);
		case 36:
			return new FLOAD(offset, 2);
		case 37:
			return new FLOAD(offset, 3);
		case 38:
			return new DLOAD(offset, 0);
		case 39:
			return new DLOAD(offset, 1);
		case 40:
			return new DLOAD(offset, 2);
		case 41:
			return new DLOAD(offset, 3);
		case 42:
			return new ALOAD(offset, 0);
		case 43:
			return new ALOAD(offset, 1);
		case 44:
			return new ALOAD(offset, 2);
		case 45:
			return new ALOAD(offset, 3);
		case 46:
			return new IALOAD(offset);
		case 47:
			return new LALOAD(offset);
		case 48:
			return new FALOAD(offset);
		case 49:
			return new DALOAD(offset);
		case 50:
			return new AALOAD(offset);
		case 51:
			return new BALOAD(offset);
		case 52:
			return new CALOAD(offset);
		case 53:
			return new SALOAD(offset);
		case 54:
			return new ISTORE(offset, operandU1, false);
		case 55:
			return new LSTORE(offset, operandU1, false);
		case 56:
			return new FSTORE(offset, operandU1, false);
		case 57:
			return new DSTORE(offset, operandU1, false);
		case 58:
			return new ASTORE(offset, operandU1, false);
		case 59:
			return new ISTORE(offset, 0);
		case 60:
			return new ISTORE(offset, 1);
		case 61:
			return new ISTORE(offset, 2);
		case 62:
			return new ISTORE(offset, 3);
		case 63:
			return new LSTORE(offset, 0);
		case 64:
			return new LSTORE(offset, 1);
		case 65:
			return new LSTORE(offset, 2);
		case 66:
			return new LSTORE(offset, 3);
		case 67:
			return new FSTORE(offset, 0);
		case 68:
			return new FSTORE(offset, 1);
		case 69:
			return new FSTORE(offset, 2);
		case 70:
			return new FSTORE(offset, 3);
		case 71:
			return new DSTORE(offset, 0);
		case 72:
			return new DSTORE(offset, 1);
		case 73:
			return new DSTORE(offset, 2);
		case 74:
			return new DSTORE(offset, 3);
		case 75:
			return new ASTORE(offset, 0);
		case 76:
			return new ASTORE(offset, 1);
		case 77:
			return new ASTORE(offset, 2);
		case 78:
			return new ASTORE(offset, 3);
		case 79:
			return new IASTORE(offset);
		case 80:
			return new LASTORE(offset);
		case 81:
			return new FASTORE(offset);
		case 82:
			return new DASTORE(offset);
		case 83:
			return new AASTORE(offset);
		case 84:
			return new BASTORE(offset);
		case 85:
			return new CASTORE(offset);
		case 86:
			return new SASTORE(offset);
		case 87:
			return new POP(offset);
		case 88:
			return new POP2(offset);
		case 89:
			return new DUP(offset);
		case 90:
			return new DUP_X1(offset);
		case 91:
			return new DUP_X2(offset);
		case 92:
			return new DUP2(offset);
		case 93:
			return new DUP2_X1(offset);
		case 94:
			return new DUP2_X2(offset);
		case 95:
			return new SWAP(offset);
		case 96:
			return new IADD(offset);
		case 97:
			return new LADD(offset);
		case 98:
			return new FADD(offset);
		case 99:
			return new DADD(offset);
		case 100:
			return new ISUB(offset);
		case 101:
			return new LSUB(offset);
		case 102:
			return new FSUB(offset);
		case 103:
			return new DSUB(offset);
		case 104:
			return new IMUL(offset);
		case 105:
			return new LMUL(offset);
		case 106:
			return new FMUL(offset);
		case 107:
			return new DMUL(offset);
		case 108:
			return new IDIV(offset);
		case 109:
			return new LDIV(offset);
		case 110:
			return new FDIV(offset);
		case 111:
			return new DDIV(offset);
		case 112:
			return new IREM(offset);
		case 113:
			return new LREM(offset);
		case 114:
			return new FREM(offset);
		case 115:
			return new DREM(offset);
		case 116:
			return new INEG(offset);
		case 117:
			return new LNEG(offset);
		case 118:
			return new FNEG(offset);
		case 119:
			return new DNEG(offset);
		case 120:
			return new ISHL(offset);
		case 121:
			return new LSHL(offset);
		case 122:
			return new ISHR(offset);
		case 123:
			return new LSHR(offset);
		case 124:
			return new IUSHR(offset);
		case 125:
			return new LUSHR(offset);
		case 126:
			return new IAND(offset);
		case 127:
			return new LAND(offset);
		case 128:
			return new IOR(offset);
		case 129:
			return new LOR(offset);
		case 130:
			return new IXOR(offset);
		case 131:
			return new LXOR(offset);
		case 132:
			return new IINC(offset, operandU1, operand2S1);
		case 133:
			return new I2L(offset);
		case 134:
			return new I2F(offset);
		case 135:
			return new I2D(offset);
		case 136:
			return new L2I(offset);
		case 137:
			return new L2F(offset);
		case 138:
			return new L2D(offset);
		case 139:
			return new F2I(offset);
		case 140:
			return new F2L(offset);
		case 141:
			return new F2D(offset);
		case 142:
			return new D2I(offset);
		case 143:
			return new D2L(offset);
		case 144:
			return new D2F(offset);
		case 145:
			return new I2B(offset);
		case 146:
			return new I2C(offset);
		case 147:
			return new I2S(offset);
		case 148:
			return new LCMP(offset);
		case 149:
			return new FCMPL(offset);
		case 150:
			return new FCMPG(offset);
		case 151:
			return new DCMPL(offset);
		case 152:
			return new DCMPG(offset);
		case 153:
			return new IFEQ(offset, operandU2);
		case 154:
			return new IFNE(offset, operandU2);
		case 155:
			return new IFLT(offset, operandU2);
		case 156:
			return new IFGE(offset, operandU2);
		case 157:
			return new IFGT(offset, operandU2);
		case 158:
			return new IFLE(offset, operandU2);
		case 159:
			return new IF_ICMPEQ(offset, operandU2);
		case 160:
			return new IF_ICMPNE(offset, operandU2);
		case 161:
			return new IF_ICMPLT(offset, operandU2);
		case 162:
			return new IF_ICMPGE(offset, operandU2);
		case 163:
			return new IF_ICMPGT(offset, operandU2);
		case 164:
			return new IF_ICMPLE(offset, operandU2);
		case 165:
			return new IF_ACMPEQ(offset, operandU2);
		case 166:
			return new IF_ACMPNE(offset, operandU2);
		case 167:
			return new GOTO(offset, operandU2);
		case 168:
			return new JSR(offset, operandU2);
		case 169:
			return new RET(offset, operandU1);
		case 170: // return new TABLESWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 171: // return new LOOKUPSWITCH();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 172:
			return new IRETURN(offset);
		case 173:
			return new LRETURN(offset);
		case 174:
			return new FRETURN(offset);
		case 175:
			return new DRETURN(offset);
		case 176:
			return new ARETURN(offset);
		case 177:
			return new RETURN(offset);
		case 178:
			return new GETSTATIC(offset, operandU2);
		case 179:
			return new PUTSTATIC(offset, operandU2);
		case 180:
			return new GETFIELD(offset, operandU2);
		case 181:
			return new PUTFIELD(offset, operandU2);
		case 182:
			return new INVOKEVIRTUAL(offset, operandU2);
		case 183:
			return new INVOKESPECIAL(offset, operandU2);
		case 184:
			return new INVOKESTATIC(offset, operandU2);
		case 185:
			return new INVOKEINTERFACE(offset, operandU2, operand3U1);
		case 186:
			return new INVOKEDYNAMIC(offset, operandU2);
		case 187:
			return new NEW(offset, operandU2);
		case 188:
			return new NEWARRAY(offset, operandU1);
		case 189:
			return new ANEWARRAY(offset, operandU2);
		case 190:
			return new ARRAYLENGTH(offset);
		case 191:
			return new ATHROW(offset);
		case 192:
			return new CHECKCAST(offset, operandU2);
		case 193:
			return new INSTANCEOF(offset, operandU2);
		case 194:
			return new MONITORENTER(offset);
		case 195:
			return new MONITOREXIT(offset);
		case 196: // return new WIDE();
			throw new Error("Unhandled instruction, opcode == " + opcode);
		case 197:
			return new MULTIANEWARRAY(offset, operandU2, operand3U1);
		case 198:
			return new IFNULL(offset, operandU2);
		case 199:
			return new IFNONNULL(offset, operandU2);
		case 200:
			return new GOTO_W(offset, operandS4);
		case 201:
			return new JSR_W(offset, operandS4);
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
