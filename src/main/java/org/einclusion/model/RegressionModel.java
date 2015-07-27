package org.einclusion.model;

import java.util.ArrayList;

public class RegressionModel {
	public String key;
	public String value;
	public ArrayList<Coefficient> coefficients = new ArrayList<>();

	public RegressionModel(String key, String value) {
		this.key = key;
		this.value = value;
		getCoefficients();
	}

	private void getCoefficients() {
		String sr[] = this.value.replaceAll("[{}]", "").split(",");

		String coef = sr[0].split(":")[2];
		coefficients.add(new Coefficient("const", coef));

		for (int i = 1; i < sr.length; i++) {
			String tmp[] = sr[i].split(":");
			String key = tmp[0].replaceAll("\"", "");
			String value = tmp[1];
			coefficients.add(new Coefficient(key, value));
		}
	}

	public boolean hasCoefficientValue(String coeffName) {
		String value = "";
		for (Coefficient c : coefficients) {
			if (c.name.equals(coeffName))
				value = c.value;
		}
		return !value.equals("");
	}
}
