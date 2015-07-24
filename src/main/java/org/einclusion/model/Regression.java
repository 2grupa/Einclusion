package org.einclusion.model;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

/**
 * This class is used to store Regression model attributes
 */
public class Regression {
	private static final Logger LOG = Logger.getLogger(Regression.class);
	Map<String, Double> coefficients;

	Regression() {
		super();
	}

	Regression(LinearRegression model, Instances data) {
		double[] coeff = model.coefficients();
		String cname = "";
		double cvalue;
		coefficients = new HashMap<String, Double>();
		for (int i = 0; i < coeff.length; i++) {
			if (coeff[i] != 0) {
				cvalue = coeff[i];
				if (i < model.coefficients().length - 1)
					cname = data.attribute(i).name();
				else
					cname = "";
				coefficients.put(cname, cvalue);
			}
		}
		LOG.debug("Regression coefficients: " + coefficients);
	}

	public String toFormula() {
		String formula = "";
		String plus = "";
		String key = "";
		Double value = 0d;
		for (Map.Entry<String, Double> entry : coefficients.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (key.equals("")) {
				plus = "";
				formula = formula + plus + value;
			} else {
				plus = " + ";
				formula = formula + plus + key + " * " + value;
			}
		}
		return formula;
	}

	@Override
	public String toString() {
		return "regression koefficient values:" + coefficients.toString();
	}
}