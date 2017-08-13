package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantLong extends Constant {

	private final long value;

	public ConstantLong(DataInput input) throws IOException {
		super(CONSTANT_Long);
		value = input.readLong();
	}

	public long getValue() {
		return value;
	}

}
