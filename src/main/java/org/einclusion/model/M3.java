package org.einclusion.model;

import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.classifiers.functions.LinearRegression;
import static org.einclusion.model.InstanceManager.*;

public class M3 {
	static final Logger LOG = Logger.getLogger(M3.class);
	static final String QUERY_STRING = "SELECT KFA, PUOU from assessment where "
			+ "KFA>0 and PUOU>0";

	static void getRegression() {
		LOG.info("Regression calculation started");
		try {
			// load data
			Instances data = retrieveModelInstances(QUERY_STRING);
			data.setClassIndex(data.numAttributes() - 1);

			// build model
			LinearRegression model = new LinearRegression();
			model.buildClassifier(data);
			LOG.debug("Model: " + model);

			// Save regression coefficients
			Regression regr = new Regression(model, data);
			ModelManager.setObjectValue("M3-regression", regr);
			LOG.info("M3-regression:" + regr);

			LOG.info("Regression calculation finished successfully");
		} catch (Exception e) {
			LOG.error(e.getMessage() + "  " + e.getCause());
		}
	}
}
