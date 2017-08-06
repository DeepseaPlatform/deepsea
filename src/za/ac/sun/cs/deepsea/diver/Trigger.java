package za.ac.sun.cs.deepsea.diver;

import java.util.HashSet;
import java.util.Set;

public class Trigger {

	private final String name;

	private int parameterCount = 0;

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
		assert parameterCount == 0;
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
	
}
