package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantNameAndType extends Constant {

	private final int nameIndex;

	private final int signatureIndex;

	public ConstantNameAndType(DataInput input) throws IOException {
		super(CONSTANT_NameAndType);
		nameIndex = input.readUnsignedShort();
		signatureIndex = input.readUnsignedShort();
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public String getName(final ConstantPool cp) {
		return cp.constantToString(nameIndex, CONSTANT_Utf8);
	}
	
	public int getSignatureIndex() {
		return signatureIndex;
	}

	public String getSignature(final ConstantPool cp) {
		return cp.constantToString(signatureIndex, CONSTANT_Utf8);
	}
	
}
