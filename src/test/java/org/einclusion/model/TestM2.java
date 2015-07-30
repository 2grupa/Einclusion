package org.einclusion.model;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestM2 {
	static final String PERSISTENCE_SET = "test";
	private static final Logger LOG = Logger.getLogger(TestM2.class);
	
	@BeforeClass
	public static void setUp() {
		LOG.info("TestM2 started...");
		ModelManager.initModelManager(PERSISTENCE_SET);
	}
	
	@Test
	public void testGetRegression() {
		try {
			M2.getRegression("Video", "M2-video");
		} catch (Exception e) {
			fail(e.getMessage() + " " + e.getCause());
		}
	}
	
	@AfterClass
	public static void tearDown() {
		ModelManager.closeModelManager();
		LOG.info("TestM2 finished.");

	}
}