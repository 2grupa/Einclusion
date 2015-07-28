package org.einclusion.model;

import org.apache.log4j.Logger;

public class Einclusion {
	static final String FILE = "data/test-data.arff";
	static final String FILE_TEST = "data/feedback.csv";
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);

	public static void main(String[] args) {

		try {
			// Init DB session
			System.out.println("Hello");
			ModelManager.initModelManager(PERSISTENCE_SET);
			System.out.println("Hello");
			/*
			 * // Clean data if (args[0].indexOf("clean") > 0) { // Delete old
			 * assessment data PrepareData.cleanAssessment(); }
			 */

			// Load data
			// if (args[0].indexOf("load") > 0) {
			// Load test data from file to database
			// PrepareData.arff2db(FILE);
			// Calculate derived values
			// PrepareData.calculateValues();
			System.out.println("Hello");
			PrepareData.csv2db(FILE_TEST);

			// Learn data
			// }
			// if (args[0].indexOf("learn") > 0) {
			// Calculate M1 data
			// M1.getClusters();
			// M1.getCentroids();

			// Calculate M2 data
			M2.getRegression("Video", "M2-video");
			M2.getRegression("Robotika", "M2-robotika");
			M2.getRegression("Mobilās tehnoloģijas", "M2-mobilas");

			// Calculate M3 data
			// M3.getRegression();

			// }
			AppFrame gui = new AppFrame();
			gui.setVisible(true);

		} catch (Throwable t) {
			LOG.error(t.getMessage() + " " + t.getCause());
		}

		finally {
			// Close DB session
			ModelManager.closeModelManager();
		}
	}
}
