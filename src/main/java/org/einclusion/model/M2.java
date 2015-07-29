package org.einclusion.model;

import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.classifiers.functions.LinearRegression;
import weka.filters.unsupervised.attribute.Remove;
import static org.einclusion.model.InstanceManager.*;

public class M2 {
	// static final String REGRESSION_KEY = "M2-regression";
	static String REGRESSION_KEY = "";
	static final Logger LOG = Logger.getLogger(M2.class);
	static final String QUERY_STRING = "SELECT SWL,  SAL, ELM, IWS, ELE, PUOU from Studenti where "
			+ "SWL>0 and SAL>0 and ELM>0 and IWS>0 and ELE>0 and PUOU>0";

	static void getRegression(String topic, String regression_key) {
		REGRESSION_KEY = regression_key;
		// load data
		Instances data = retrieveModelInstances(QUERY_STRING + " and TEMA is '"
				+ topic + "'");

		System.out.println("MODEL DATA:\n>>" + data);

		data.setClassIndex(data.numAttributes() - 1);
		// build model
		LinearRegression model = new LinearRegression();
		try {
			model.buildClassifier(data);
			LOG.debug(model);

			String[] options = new String[2];
			options[0] = "-R"; // "range"
			options[1] = "1"; // first attribute
			Remove remove = new Remove(); // new instance of filter
			remove.setOptions(options); // set options
			remove.setInputFormat(data);

			// Save regression coefficients
			Regression regression = new Regression(model, data);
			ModelManager.setObjectValue(REGRESSION_KEY, regression);

		} catch (Exception e) {
			// LOG.error(e.getMessage() + " " + e.getCause());
		}
	}

}