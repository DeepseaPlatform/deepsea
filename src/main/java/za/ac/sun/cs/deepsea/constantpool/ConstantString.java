package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a string.
 *
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantString extends Constant {

	/**
	 * The index (in the constant pool) of the UTF8 character sequence for the
	 * string.
	 */
	private final int stringIndex;

	/**
	 * Constructs a string constant by reading additional information (excluding
	 * the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantString(DataInput input) throws IOException {
		super(Constants.CONSTANT_String);
		stringIndex = input.readUnsignedShort();
	}

	/**
	 * Returns the index of string's UTF8 character sequence.
	 * 
	 * @return the index of the string's UTF8 character sequence
	 */
	public int getStringIndex() {
		return stringIndex;
	}

	/**
	 * Returns the value of the string.
	 * 
	 * @param pool
	 *            the constant pool containing the constant
	 * @return the string represented by this constant
	 */
	public String getString(ConstantPool pool) {
		Constant c = pool.getConstant(stringIndex, Constants.CONSTANT_Utf8);
		return ((ConstantUtf8) c).getValue();
	}

}
