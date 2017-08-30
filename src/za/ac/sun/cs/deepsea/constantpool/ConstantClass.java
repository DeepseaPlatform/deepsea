package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a class or interface.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantClass extends Constant {

	/**
	 * The index (in the constant pool) of the class name.
	 */
	private final int nameIndex;

	/**
	 * Constructs a class constant by reading additional information (excluding
	 * the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantClass(DataInput input) throws IOException {
		super(Constants.CONSTANT_Class);
		nameIndex = input.readUnsignedShort();
	}

	/**
	 * Returns the number of constant pool entry that contains the name of the
	 * class or interface.
	 * 
	 * @return the constant pool entry index for the class or interface name
	 */
	public int getNameIndex() {
		return nameIndex;
	}

	/**
	 * Returns the name of the class as a string.
	 * 
	 * @param pool
	 *            the constant pool containing the constant
	 * @return the name of the class
	 */
	public Object getValue(ConstantPool pool) {
		Constant c = pool.getConstant(nameIndex, Constants.CONSTANT_Utf8);
		return ((ConstantUtf8) c).getValue();
	}

}
