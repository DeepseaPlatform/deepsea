package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a {@code long} value.
 *
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantLong extends Constant {

	/**
	 * The value of the constant.
	 */
	private final long value;

	/**
	 * Constructs a {@code long} constant by reading additional information
	 * (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantLong(DataInput input) throws IOException {
		super(Constants.CONSTANT_Long);
		value = input.readLong();
	}

	/**
	 * Returns the value of the constant.
	 * 
	 * @return the constant value
	 */
	public long getValue() {
		return value;
	}

}
