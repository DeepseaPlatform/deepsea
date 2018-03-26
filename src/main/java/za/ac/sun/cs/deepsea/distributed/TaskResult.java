package za.ac.sun.cs.deepsea.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import za.ac.sun.cs.green.expr.Constant;

public class TaskResult implements Serializable {

	private static final long serialVersionUID = -1792007788307029197L;

	private final String path;
	
	private final Map<String, Constant> values;

	private String string;

	public static final TaskResult EMPTY = new TaskResult();

	private TaskResult() {
		path = Strings.EMPTY;
		values = Collections.emptyMap();
	}

	public String getPath() {
		return path;
	}

	public Map<String, Constant> getValues() {
		return Collections.unmodifiableMap(values);
	}

	public String intoString() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}
	
	public static TaskResult fromString(String s) throws IOException, ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode( s );
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return (TaskResult) o;
	}

	@Override
	public String toString() {
		if (string == null) {
			StringBuilder s = new StringBuilder();
			s.append(path);
			for (Map.Entry<String, Constant> e : values.entrySet()) {
				s.append(e.getKey()).append(' ').append(e.getValue().toString());
			}
			string = s.toString();
		}
		return string;
	}

}
