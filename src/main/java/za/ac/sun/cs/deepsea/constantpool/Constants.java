package za.ac.sun.cs.deepsea.constantpool;

/**
 * Container for constant values.  Could have been an {@code enum}, but itsn't.
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class Constants {

	/**
	 * Tag for an UTF8 character sequence.
	 */
	public static final byte CONSTANT_Utf8 = 1;

	/**
	 * Tag for a Java {@code int}.
	 */
	public static final byte CONSTANT_Integer = 3;

	/**
	 * Tag for a Java {@code float}.
	 */
	public static final byte CONSTANT_Float = 4;

	/**
	 * Tag for a Java {@code long}.
	 */
	public static final byte CONSTANT_Long = 5;

	/**
	 * Tag for a Java {@code double}.
	 */
	public static final byte CONSTANT_Double = 6;

	/**
	 * Tag for a class or interface.
	 */
	public static final byte CONSTANT_Class = 7;

	/**
	 * Tag for a field reference.
	 */
	public static final byte CONSTANT_Fieldref = 9;

	/**
	 * Tag for a constant string.
	 */
	public static final byte CONSTANT_String = 8;

	/**
	 * Tag for a method reference.
	 */
	public static final byte CONSTANT_Methodref = 10;

	/**
	 * Tag for a interface method reference.
	 */
	public static final byte CONSTANT_InterfaceMethodref = 11;

	/**
	 * Tag for the representation of the name and type of a field or method.
	 */
	public static final byte CONSTANT_NameAndType = 12;

	/**
	 * Tag for a method handle.
	 */
	public static final byte CONSTANT_MethodHandle = 15;

	/**
	 * Tag for the type of a method.
	 */
	public static final byte CONSTANT_MethodType = 16;

	/**
	 * Tag for information used by an {@code INVOKEDYNAMIC} instruction.
	 */
	public static final byte CONSTANT_InvokeDynamic = 18;

}
