package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

public class ConstantDouble extends Constant {

	private final double value;

	public ConstantDouble(DataInput input) throws IOException {
		super(CONSTANT_Double);
		value = input.readDouble();
	}

	public double getValue() {
		return value;
	}

}
