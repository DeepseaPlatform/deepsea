package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantInvokeDynamic extends Constant {

	/**
	 * TODO
	 */
	private final int bootstrapMethodAttrIndex;

	/**
	 * TODO
	 */
	private final int nameAndTypeIndex;

	/**
	 * TODO
	 * 
	 * @param input TODO
	 * @throws IOException TODO
	 */
	public ConstantInvokeDynamic(DataInput input) throws IOException {
		super(Constants.CONSTANT_InvokeDynamic);
		bootstrapMethodAttrIndex = input.readUnsignedShort();
		nameAndTypeIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getBootstrapMethodAttrIndex() {
		return bootstrapMethodAttrIndex;
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

}
