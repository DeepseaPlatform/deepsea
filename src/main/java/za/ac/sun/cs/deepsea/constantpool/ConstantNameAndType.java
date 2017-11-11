package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * A constant that represents the name and the type of a member (field or
 * method) or a variable (local or parameter) or anything that can have a name
 * and a type.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantNameAndType extends Constant {

	/**
	 * The index (in the constant pool) of the UTF8 character sequence for the
	 * name.
	 */
	private final int nameIndex;

	/**
	 * The index (in the constant pool) of the UTF8 character sequence for the
	 * type.
	 */
	private final int signatureIndex;

	/**
	 * Constructs a name-and-type constant by reading additional information
	 * (excluding the tag) from the input stream.
	 * 
	 * @param input
	 *            the input stream
	 * @throws IOException
	 *             if the input stream cannot be read
	 */
	public ConstantNameAndType(DataInput input) throws IOException {
		super(Constants.CONSTANT_NameAndType);
		nameIndex = input.readUnsignedShort();
		signatureIndex = input.readUnsignedShort();
	}

	/**
	 * Returns an index in the constant pool where the name is stored.
	 * 
	 * @return constant pool index of the name
	 */
	public int getNameIndex() {
		return nameIndex;
	}

	/**
	 * TODO
	 * 
	 * @param cp TODO
	 * @return TODO
	 */
	public String getName(final ConstantPool cp) {
		return cp.constantToString(nameIndex, Constants.CONSTANT_Utf8);
	}

	/**
	 * Returns an index in the constant pool where the type is stored as a
	 * signature.
	 * 
	 * @return constant pool index of the type
	 */
	public int getSignatureIndex() {
		return signatureIndex;
	}

	/**
	 * TODO
	 * 
	 * @param cp TODO
	 * @return TODO
	 */
	public String getSignature(final ConstantPool cp) {
		return cp.constantToString(signatureIndex, Constants.CONSTANT_Utf8);
	}

	/**
	 * TODO
	 * 
	 * @param cp TODO
	 * @return TODO
	 */
	public String getAsciiSignature(final ConstantPool cp) {
		return cp.constantToString(signatureIndex, Constants.CONSTANT_Utf8)
				.replace('/', '_')
				.replace("_", "_1")
				.replace(";", "_2")
				.replace("[", "_3")
				.replace("(", "__")
				.replace(")", "__");
	}
	
}
