package io.varhttp;

public class PathVariableInfo {
	private String name;
	private Class<?> type;
	private int sortOffset;

	public PathVariableInfo(String name, Class<?> type, int sortOffset){
		this.name = name;
		this.type = type;
		this.sortOffset = sortOffset;
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
}
