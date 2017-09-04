package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * Base class for the representation of an entry in the constant pool.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public abstract class Constant {

	/**
	 * The constant tag.
	 */
	private final byte tag;

	/**
	 * Constructs a constant by setting its {@code tag} field.
	 * 
	 * @param tag
	 *            the tag in the constant pool
	 */
	protected Constant(byte tag) {
		this.tag = tag;
	}

	/**
	 * Returns the tag of this constant.
	 * 
	 * @return the tag of this constant
	 */
	public byte getTag() {
		return tag;
	}

	/**
	 * Utility method that reads, interprets, and constructs one constant pool
	 * entry.
	 * 
	 * @param input
	 *            the input stream of bytes that iterate over the constant pool
	 * @return the new constant as constructed from the byte stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public static Constant readConstant(DataInput input) throws IOException {
		byte b = input.readByte();
		switch (b) {
		case Constants.CONSTANT_Class:
			return new ConstantClass(input);
		case Constants.CONSTANT_Fieldref:
			return new ConstantFieldref(input);
		case Constants.CONSTANT_Methodref:
			return new ConstantMethodref(input);
		case Constants.CONSTANT_InterfaceMethodref:
			return new ConstantInterfaceMethodref(input);
		case Constants.CONSTANT_String:
			return new ConstantString(input);
		case Constants.CONSTANT_Integer:
			return new ConstantInteger(input);
		case Constants.CONSTANT_Float:
			return new ConstantFloat(input);
		case Constants.CONSTANT_Long:
			return new ConstantLong(input);
		case Constants.CONSTANT_Double:
			return new ConstantDouble(input);
		case Constants.CONSTANT_NameAndType:
			return new ConstantNameAndType(input);
		case Constants.CONSTANT_Utf8:
			return new ConstantUtf8(input);
		case Constants.CONSTANT_MethodHandle:
			return new ConstantMethodHandle(input);
		case Constants.CONSTANT_MethodType:
			return new ConstantMethodType(input);
		case Constants.CONSTANT_InvokeDynamic:
			return new ConstantInvokeDynamic(input);
		default:
			throw new ConstantPool.ClassFormatException("Invalid byte tag in constant pool: " + b);
		}
	}

}
