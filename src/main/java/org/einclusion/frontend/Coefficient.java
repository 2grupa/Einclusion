package org.einclusion.frontend;
/**
 * @author student
 * Represents a Variable in an Equation.
 */
public class Coefficient {
	public String name;
	public String value;
	
	/**
	 * Creates a Coefficient with given Name and a default value of 0.
	 * @param name - name of the Coefficient, for example, "SWL", "x", "Constant", etc.
	 */
	public Coefficient(String name) {
		this.name = name;
		value = "0";
	}
	
	/**
	 * Creates a Coefficient with given Name and Value.
	 * @param name - name of the constant.
	 * @param value - value of the constant.
	 */
	public Coefficient(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
