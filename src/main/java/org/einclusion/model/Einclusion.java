package org.einclusion.model;

import org.apache.log4j.Logger;


public class Einclusion {
	static final String FILE = "data/test-data.arff";
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);

	public static void main(String[] args) {
		
		AppFrame gui = new AppFrame();
		gui.setVisible(true);
		
		try {
			// Init DB session
			ModelManager.initModelManager(PERSISTENCE_SET);

			/*
			 * // Clean data if (args[0].indexOf("clean") > 0) { // Delete old
			 * assessment data PrepareData.cleanAssessment(); }
			 */

			// Load data
			if (args[0].indexOf("load") > 0) {
				// Load test data from file to database
				PrepareData.arff2db(FILE);
				// Calculate derived values
				PrepareData.calculateValues();

				// Learn data
			}
			if (args[0].indexOf("learn") > 0) {
				// Calculate M1 data
				// M1.getClusters();
				// M1.getCentroids();

				// Calculate M2 data
				M2.getRegression();

				// Calculate M3 data
				// M3.getRegression();

			}

		} catch (Throwable t) {
			LOG.error(t.getMessage() + " " + t.getCause());
		}

		finally {
			// Close DB session
			ModelManager.closeModelManager();
		}
	}
}
