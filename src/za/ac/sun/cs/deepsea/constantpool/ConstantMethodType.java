package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantMethodType extends Constant {

	private final int descriptorIndex;

	public ConstantMethodType(DataInput input) throws IOException {
		super(CONSTANT_MethodType);
		descriptorIndex = input.readUnsignedShort();
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}

}
