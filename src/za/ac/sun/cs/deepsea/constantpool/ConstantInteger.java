package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents an {@code int} value.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantInteger extends Constant {

	/**
	 * The value of the constant.
	 */
	private final int value;

	/**
	 * Constructs an {@code int} constant by reading additional information
	 * (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantInteger(DataInput input) throws IOException {
		super(Constants.CONSTANT_Integer);
		value = input.readInt();
	}

	/**
	 * Returns the value of the constant.
	 * 
	 * @return the constant value
	 */
	public int getValue() {
		return value;
	}

}
