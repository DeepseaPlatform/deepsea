package za.ac.sun.cs.deepsea.constantpool;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 * 
 * @author Jaco Geldenhuys (geld@sun.ac.za)
 */
public class ConstantPool {

	/**
	 * TODO
	 */
	private Constant[] pool;

	/**
	 * TODO
	 * 
	 * @param poolCount
	 * @param poolBytes
	 * @throws IOException
	 */
	public ConstantPool(int poolCount, byte[] poolBytes) throws IOException {
		InputStream is = new ByteArrayInputStream(poolBytes);
		DataInput input = new DataInputStream(is);
		byte tag;
		pool = new Constant[poolCount];
		for (int i = 1; i < poolCount; i++) {
			pool[i] = Constant.readConstant(input);
			tag = pool[i].getTag();
			if ((tag == Constants.CONSTANT_Double) || (tag == Constants.CONSTANT_Long)) {
				i++;
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @param index
	 * @param tag
	 * @return
	 */
	public Constant getConstant(int index, byte tag) {
		Constant c = getConstant(index);
		if (c == null) {
			throw new ClassFormatException("Constant pool at index " + index + " is null");
		}
		if (c.getTag() != tag) {
			throw new ClassFormatException(
					"Expected class tag " + tag + ", but found " + c.getTag() + " at index " + index);
		}
		return c;
	}

	/**
	 * TODO
	 * 
	 * @param index
	 * @return
	 */
	public Constant getConstant(final int index) {
		if (index >= pool.length || index < 0) {
			throw new ClassFormatException(
					"Invalid constant pool reference: " + index + ". Constant pool size is: " + pool.length);
		}
		return pool[index];
	}

	/**
	 * TODO
	 * 
	 * @param index
	 * @param tag
	 * @return
	 */
	public String constantToString(int index, byte tag) {
		return constantToString(getConstant(index, tag));
	}

	/**
	 * TODO
	 * 
	 * @param c
	 * @return
	 */
	private String constantToString(Constant c) {
		int i;
		final byte tag = c.getTag();
		switch (tag) {
		case Constants.CONSTANT_Utf8:
			return ((ConstantUtf8) c).getValue();
		case Constants.CONSTANT_Integer:
			return String.valueOf(((ConstantInteger) c).getValue());
		case Constants.CONSTANT_Float:
			return String.valueOf(((ConstantFloat) c).getValue());
		case Constants.CONSTANT_Long:
			return String.valueOf(((ConstantLong) c).getValue());
		case Constants.CONSTANT_Double:
			return String.valueOf(((ConstantDouble) c).getValue());
		case Constants.CONSTANT_Class:
			i = ((ConstantClass) c).getNameIndex();
			c = getConstant(i, Constants.CONSTANT_Utf8);
			return compactClassName(((ConstantUtf8) c).getValue(), false);
		case Constants.CONSTANT_Fieldref:
			return constantToString(((ConstantFieldref) c).getClassIndex(), Constants.CONSTANT_Class) + "."
					+ constantToString(((ConstantFieldref) c).getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);
		case Constants.CONSTANT_String:
			i = ((ConstantString) c).getStringIndex();
			c = getConstant(i, Constants.CONSTANT_Utf8);
			return "\"" + escape(((ConstantUtf8) c).getValue()) + "\"";
		case Constants.CONSTANT_Methodref:
			return constantToString(((ConstantMethodref) c).getClassIndex(), Constants.CONSTANT_Class) + "."
					+ constantToString(((ConstantMethodref) c).getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);
		case Constants.CONSTANT_InterfaceMethodref:
			return constantToString(((ConstantInterfaceMethodref) c).getClassIndex(), Constants.CONSTANT_Class) + "."
					+ constantToString(((ConstantInterfaceMethodref) c).getNameAndTypeIndex(),
							Constants.CONSTANT_NameAndType);
		case Constants.CONSTANT_NameAndType:
			return constantToString(((ConstantNameAndType) c).getNameIndex(), Constants.CONSTANT_Utf8) + ":"
					+ constantToString(((ConstantNameAndType) c).getSignatureIndex(), Constants.CONSTANT_Utf8);
		case Constants.CONSTANT_MethodHandle:
			final ConstantMethodHandle cmh = (ConstantMethodHandle) c;
			return getMethodHandleName(cmh.getReferenceKind()) + " "
					+ constantToString(cmh.getReferenceIndex(), getConstant(cmh.getReferenceIndex()).getTag());
		case Constants.CONSTANT_MethodType:
			final ConstantMethodType cmt = (ConstantMethodType) c;
			return constantToString(cmt.getDescriptorIndex(), Constants.CONSTANT_Utf8);
		case Constants.CONSTANT_InvokeDynamic:
			final ConstantInvokeDynamic cid = (ConstantInvokeDynamic) c;
			return cid.getBootstrapMethodAttrIndex() + ":"
					+ constantToString(cid.getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);
		default: // Never reached
			throw new RuntimeException("Unknown constant type " + tag);
		}
	}

	/**
	 * TODO
	 */
	private static final String[] METHODHANDLE_NAMES = { "", "getField", "getStatic", "putField", "putStatic",
			"invokeVirtual", "invokeStatic", "invokeSpecial", "newInvokeSpecial", "invokeInterface" };

	/**
	 * TODO
	 * 
	 * @param index
	 * @return
	 */
	public static String getMethodHandleName(final int index) {
		return METHODHANDLE_NAMES[index];
	}

	/**
	 * TODO
	 * 
	 * @param str
	 * @return
	 */
	private static String escape(String str) {
		int len = str.length();
		StringBuilder buf = new StringBuilder(len + 5);
		char[] ch = str.toCharArray();
		for (int i = 0; i < len; i++) {
			switch (ch[i]) {
			case '\n':
				buf.append("\\n");
				break;
			case '\r':
				buf.append("\\r");
				break;
			case '\t':
				buf.append("\\t");
				break;
			case '\b':
				buf.append("\\b");
				break;
			case '"':
				buf.append("\\\"");
				break;
			default:
				buf.append(ch[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * TODO
	 * 
	 * @param str
	 * @param prefix
	 * @param chopit
	 * @return
	 */
	private static String compactClassName(String str, String prefix, boolean chopit) {
		final int len = prefix.length();
		str = str.replace('/', '.'); // Is `/' on all systems, even DOS
		if (chopit) {
			if (str.startsWith(prefix) && (str.substring(len).indexOf('.') == -1)) {
				str = str.substring(len);
			}
		}
		return str;
	}

	/**
	 * TODO
	 * 
	 * @param str
	 * @param chopit
	 * @return
	 */
	private static String compactClassName(String str, boolean chopit) {
		return compactClassName(str, "java.lang.", chopit);
	}

	/**
	 * TODO
	 * 
	 * @author Jaco Geldenhuys (geld@sun.ac.za)
	 */
	@SuppressWarnings("serial")
	public static class ClassFormatException extends RuntimeException {

		/**
		 * TODO
		 */
		public ClassFormatException() {
			super();
		}

		/**
		 * TODO
		 * 
		 * @param s
		 */
		public ClassFormatException(final String s) {
			super(s);
		}

		/**
		 * TODO
		 * 
		 * @param message
		 * @param cause
		 */
		public ClassFormatException(final String message, final Throwable cause) {
			super(message, cause);
		}

	}

}
