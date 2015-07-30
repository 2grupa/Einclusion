package org.einclusion.model;

import org.apache.log4j.Logger;
import org.einclusion.GUI.AppFrame;

public class Einclusion {

	static final String FILE_TEST = "data/feedback.csv";
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);

	public static void main(String[] args) {

		try {
			// Init DB session
			ModelManager.initModelManager(PERSISTENCE_SET);

			PrepareData.csv2db(FILE_TEST);

			// Calculate M2 data
			M2.getRegression("Video", "M2-video");
			M2.getRegression("Robotika", "M2-robotika");
			M2.getRegression("Mobilās tehnoloģijas", "M2-mobilas");

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
