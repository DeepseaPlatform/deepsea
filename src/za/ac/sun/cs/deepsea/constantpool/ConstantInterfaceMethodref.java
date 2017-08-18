package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantInterfaceMethodref extends Constant {

	private final int classIndex;

	private final int nameAndTypeIndex;

	public ConstantInterfaceMethodref(DataInput input) throws IOException {
		super(CONSTANT_InterfaceMethodref);
		classIndex = input.readUnsignedShort();
		nameAndTypeIndex = input.readUnsignedShort();
	}

	public int getClassIndex() {
		return classIndex;
	}

	public String getClass(final ConstantPool cp) {
		return cp.constantToString(classIndex, CONSTANT_Class);
	}
	
	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

}