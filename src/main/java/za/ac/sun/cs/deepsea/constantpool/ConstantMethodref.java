package za.ac.sun.cs.deepsea.constantpool;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantMethodref extends Constant {

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
	 * 
	 * @param input TODO
	 * @throws IOException TODO
	 */
	public ConstantMethodref(DataInput input) throws IOException {
		super(Constants.CONSTANT_Methodref);
		classIndex = input.readUnsignedShort();
		nameAndTypeIndex = input.readUnsignedShort();
	}

	/**
	 * TODO
	 * 
	 * @return TODO
	 */
	public int getClassIndex() {
		return classIndex;
	}

	/**
	 * TODO
	 * 
	 * @param cp TODO
	 * @return TODO
	 */
	public String getClass(ConstantPool cp) {
		return cp.constantToString(classIndex, Constants.CONSTANT_Class);
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
