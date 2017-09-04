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
	 * @param input
	 * @throws IOException
	 */
	public ConstantMethodType(DataInput input) throws IOException {
		super(Constants.CONSTANT_MethodType);
		descriptorIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * @return
	 */
	public int getDescriptorIndex() {
		return descriptorIndex;
	}

}
