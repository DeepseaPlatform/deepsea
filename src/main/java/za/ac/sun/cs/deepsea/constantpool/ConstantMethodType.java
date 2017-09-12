package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantMethodType extends Constant {

	/**
	 * TODO
	 */
	private final int descriptorIndex;

	/**
	 * TODO
	 * 
	 * @param input TODO
	 * @throws IOException TODO
	 */
	public ConstantMethodType(DataInput input) throws IOException {
		super(Constants.CONSTANT_MethodType);
		descriptorIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getDescriptorIndex() {
		return descriptorIndex;
	}

}
