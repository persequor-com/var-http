package io.varhttp.controllers;

public enum MyEnum {
	Kilroy("Kilroy was here"), Cow("Cows say muh");

	private final String s;
	MyEnum(String s) {
		this.s = s;
	}

	public String stringValue() {
		return s;
	}
}
