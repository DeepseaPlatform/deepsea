package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantFloat extends Constant {

	private final float value;

	public ConstantFloat(DataInput input) throws IOException {
		super(CONSTANT_Float);
		value = input.readFloat();
	}

	public float getValue() {
		return value;
	}

}
