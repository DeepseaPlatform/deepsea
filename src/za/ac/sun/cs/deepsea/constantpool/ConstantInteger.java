package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantInteger extends Constant {

	private final int value;

	public ConstantInteger(DataInput input) throws IOException {
		super(CONSTANT_Integer);
		value = input.readInt();
	}

	public int getValue() {
		return value;
	}

}
