package za.ac.sun.cs.deepsea.diver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.jdi.Method;

public class Trigger {

	private final String name;
	
	private Integer parameterCount = null;

	private String[] parameterName = null;

	private Object[] parameterType = null;

	private final Set<String> symbolicNames = new HashSet<>();

	public Trigger(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getParameterCount() {
		return parameterCount;
	}

	public void setParameterCount(int parameterCount) {
		assert this.parameterCount == null;
		this.parameterCount = parameterCount;
		parameterName = new String[parameterCount];
		parameterType = new Object[parameterCount];
	}

	public boolean isParameterSymbolic(int index) {
		assert (index >= 0) && (index < parameterCount);
		return (parameterName[index] != null);
	}

	public String getParameterName(int index) {
		assert (index >= 0) && (index < parameterCount);
		return parameterName[index];
	}

	public void setParameterName(int index, String name) {
		assert (index >= 0) && (index < parameterCount);
		assert parameterName[index] == null;
		assert !symbolicNames.contains(name);
		assert name.length() > 0;
		parameterName[index] = name;
		symbolicNames.add(name);
	}

	public Object getParameterType(int index) {
		assert (index >= 0) && (index < parameterCount);
		return parameterType[index];
	}
	
	public void setParameterType(int index, Object type) {
		assert (index >= 0) && (index < parameterCount);
		assert parameterType[index] == null;
		assert type != null;
		parameterType[index] = type;
	}

	public boolean match(String className, Method method) {
		String name = className + "." + method.name();
		if (!this.name.equals(name)) {
			return false;
		}
		List<String> types = method.argumentTypeNames();
		if (parameterCount != types.size()) {
			return false;
		}
		for (int i = 0; i < parameterCount; i++) {
			String type = types.get(i);
			if (parameterType[i] == null) { continue; }
			if ((parameterType[i] == Boolean.class) && type.equals("boolean")) { continue; }
			if ((parameterType[i] == Integer.class) && type.equals("int")) { continue; }
			return false;
		}
		return true;
	}

}