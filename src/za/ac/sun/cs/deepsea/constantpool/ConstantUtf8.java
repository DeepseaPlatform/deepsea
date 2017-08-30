package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents a UTF8 character sequence.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantUtf8 extends Constant {

	/**
	 * The value of the character sequences.
	 */
	private final String value;

	/**
	 * Constructs a UTF8 character sequence constant by reading additional
	 * information (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantUtf8(DataInput input) throws IOException {
		super(Constants.CONSTANT_Utf8);
		value = input.readUTF();
	}

	/**
	 * Returns the UTF8 character sequence as a string.
	 * 
	 * @return the UTF8 string
	 */
	public String getValue() {
		return value;
	}

}
