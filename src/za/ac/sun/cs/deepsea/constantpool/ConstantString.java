package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantString extends Constant {

	private final int stringIndex;

	public ConstantString(DataInput input) throws IOException {
		super(CONSTANT_String);
		stringIndex = input.readUnsignedShort();
	}

	public int getStringIndex() {
		return stringIndex;
	}

	public String getString(ConstantPool pool) {
		Constant c = pool.getConstant(stringIndex, CONSTANT_Utf8);
		return ((ConstantUtf8) c).getValue();
	}

}
