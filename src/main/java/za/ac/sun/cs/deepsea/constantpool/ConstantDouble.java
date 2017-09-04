package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a {@code double} value.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantDouble extends Constant {

	/**
	 * The value of the constant.
	 */
	private final double value;

	/**
	 * Constructs a {@code double} constant by reading additional information
	 * (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantDouble(DataInput input) throws IOException {
		super(Constants.CONSTANT_Double);
		value = input.readDouble();
	}

	/**
	 * Returns the value of the constant.
	 * 
	 * @return the constant value
	 */
	public double getValue() {
		return value;
	}

}
