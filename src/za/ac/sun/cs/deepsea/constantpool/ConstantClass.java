package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantClass extends Constant {

	private final int stringIndex;

	public ConstantClass(DataInput input) throws IOException {
		super(CONSTANT_Class);
		stringIndex = input.readUnsignedShort();
	}

	public int getNameIndex() {
		return stringIndex;
	}

	public Object getValue(ConstantPool pool) {
		Constant c = pool.getConstant(stringIndex, CONSTANT_Utf8);
		return ((ConstantUtf8) c).getValue();
	}

}
