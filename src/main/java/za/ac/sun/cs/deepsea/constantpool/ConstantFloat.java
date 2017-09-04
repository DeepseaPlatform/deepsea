package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a {@code float} value.
 *
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantFloat extends Constant {

	/**
	 * The value of the constant.
	 */
	private final float value;

	/**
	 * Constructs a {@code float} constant by reading additional information
	 * (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantFloat(DataInput input) throws IOException {
		super(Constants.CONSTANT_Float);
		value = input.readFloat();
	}

	/**
	 * Returns the value of the constant.
	 * 
	 * @return the constant value
	 */
	public float getValue() {
		return value;
	}

}
