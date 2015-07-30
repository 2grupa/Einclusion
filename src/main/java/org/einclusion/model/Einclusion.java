package org.einclusion.model;

import org.apache.log4j.Logger;
import org.einclusion.GUI.AppFrame;

public class Einclusion {

	static final String FILE_TEST = "data/feedback.csv";
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);

	public static void main(String[] args) {
		AppFrame gui = new AppFrame();
		gui.setVisible(true);

	}
}
