package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public abstract class Constant {

	public static final byte CONSTANT_Utf8 = 1;

	public static final byte CONSTANT_Integer = 3;

	public static final byte CONSTANT_Float = 4;

	public static final byte CONSTANT_Long = 5;

	public static final byte CONSTANT_Double = 6;

	public static final byte CONSTANT_Class = 7;

	public static final byte CONSTANT_Fieldref = 9;

	public static final byte CONSTANT_String = 8;

	public static final byte CONSTANT_Methodref = 10;

	public static final byte CONSTANT_InterfaceMethodref = 11;

	public static final byte CONSTANT_NameAndType = 12;

	public static final byte CONSTANT_MethodHandle = 15;

	public static final byte CONSTANT_MethodType = 16;

	public static final byte CONSTANT_InvokeDynamic = 18;

	private final byte tag;

	protected Constant(byte tag) {
		this.tag = tag;
	}

	public static Constant readConstant(DataInput input) throws IOException {
		byte b = input.readByte();
		switch (b) {
		case CONSTANT_Class:
			return new ConstantClass(input);
		case CONSTANT_Fieldref:
			return new ConstantFieldref(input);
		case CONSTANT_Methodref:
			return new ConstantMethodref(input);
		case CONSTANT_InterfaceMethodref:
			return new ConstantInterfaceMethodref(input);
		case CONSTANT_String:
			return new ConstantString(input);
		case CONSTANT_Integer:
			return new ConstantInteger(input);
		case CONSTANT_Float:
			return new ConstantFloat(input);
		case CONSTANT_Long:
			return new ConstantLong(input);
		case CONSTANT_Double:
			return new ConstantDouble(input);
		case CONSTANT_NameAndType:
			return new ConstantNameAndType(input);
		case CONSTANT_Utf8:
			return new ConstantUtf8(input);
		case CONSTANT_MethodHandle:
			return new ConstantMethodHandle(input);
		case CONSTANT_MethodType:
			return new ConstantMethodType(input);
		case CONSTANT_InvokeDynamic:
			return new ConstantInvokeDynamic(input);
		default:
			throw new ConstantPool.ClassFormatException("Invalid byte tag in constant pool: " + b);
		}
	}

	public final byte getTag() {
		return tag;
	}

}
