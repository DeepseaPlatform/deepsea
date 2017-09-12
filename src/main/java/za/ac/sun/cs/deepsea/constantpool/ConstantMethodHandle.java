package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantMethodHandle extends Constant {

	/**
	 * TODO
	 */
	private final int referenceKind;

	/**
	 * TODO
	 */
	private final int referenceIndex;

	/**
	 * TODO
	 * 
	 * @param input TODO
	 * @throws IOException TODO
	 */
	public ConstantMethodHandle(DataInput input) throws IOException {
		super(Constants.CONSTANT_MethodHandle);
		referenceKind = input.readUnsignedByte();
		referenceIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getReferenceKind() {
		return referenceKind;
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getReferenceIndex() {
		return referenceIndex;
	}
	
}
