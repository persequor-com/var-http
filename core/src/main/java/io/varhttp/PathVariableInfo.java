package io.varhttp;

public class PathVariableInfo {
	private int argno;
	private String name;
	private Class<?> type;
	private int sortOffset;

	public PathVariableInfo(String name, Class<?> type, int sortOffset, int argno){
		this.name = name;
		this.type = type;
		this.sortOffset = sortOffset;
		this.argno = argno;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public int getSortOffset() {
		return sortOffset;
	}

	public int getArgno() {
		return argno;
	}
}
