package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantFieldref extends Constant {

	/**
	 * TODO
	 */
	private final int classIndex;

	/**
	 * TODO
	 */
	private final int nameAndTypeIndex;

	/**
	 * TODO
	 * @param input
	 * @throws IOException
	 */
	public ConstantFieldref(DataInput input) throws IOException {
		super(Constants.CONSTANT_Fieldref);
		classIndex = input.readUnsignedShort();
		nameAndTypeIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * @return
	 */
	public int getClassIndex() {
		return classIndex;
	}

	/**
	 * TODO
	 * @param cp
	 * @return
	 */
	public String getClass(final ConstantPool cp) {
		return cp.constantToString(classIndex, Constants.CONSTANT_Class);
	}
	
	/**
	 * TODO
	 * @return
	 */
	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

}
