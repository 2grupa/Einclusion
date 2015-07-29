package org.einclusion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestM2 {
	static final String PERSISTENCE_SET = "test";

	@Test
	public void testGetRegression() {
		try {
			ModelManager.initModelManager(PERSISTENCE_SET);
			M2.getRegression("Video", "M2-video");
			M2.getRegression("Robotika", "M2-robotika");
			M2.getRegression("Mobilās tehnoloģijas", "M2-mobilas");
			;
		} catch (Exception e) {
			fail(e.getMessage() + " " + e.getCause());
		} finally {
			ModelManager.closeModelManager();
		}
	}
}