package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantMethodHandle extends Constant {

	private final int referenceKind;

	private final int referenceIndex;

	public ConstantMethodHandle(DataInput input) throws IOException {
		super(CONSTANT_MethodHandle);
		referenceKind = input.readUnsignedByte();
		referenceIndex = input.readUnsignedShort();
	}

	public int getReferenceKind() {
		return referenceKind;
	}

	public int getReferenceIndex() {
		return referenceIndex;
	}
	
}
