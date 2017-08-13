package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantInvokeDynamic extends Constant {

	private final int bootstrapMethodAttrIndex;

	private final int nameAndTypeIndex;

	public ConstantInvokeDynamic(DataInput input) throws IOException {
		super(CONSTANT_InvokeDynamic);
		bootstrapMethodAttrIndex = input.readUnsignedShort();
		nameAndTypeIndex = input.readUnsignedShort();
	}

	public int getBootstrapMethodAttrIndex() {
		return bootstrapMethodAttrIndex;
	}

	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

}
