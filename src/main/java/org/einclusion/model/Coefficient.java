package org.einclusion.model;

public class Coefficient {
	public String name;
	public String value;

	public Coefficient(String name) {
		this.name = name;
		value = "0";
	}

	public Coefficient(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
