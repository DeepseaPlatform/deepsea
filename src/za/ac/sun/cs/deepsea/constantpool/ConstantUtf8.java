package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantUtf8 extends Constant {

	private final String value;

	ConstantUtf8(DataInput file) throws IOException {
		super(CONSTANT_Utf8);
		value = file.readUTF();
	}

	public String getValue() {
		return value;
	}

}
