package org.einclusion.model;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestModelException {
	private static final Logger LOG = Logger.getLogger(TestModelException.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOG.info("TestModelException started...");
	}

	@Test
	public void testModelException() {
		try {
			assertEquals("Error message WRONG", null, new ModelException().getMessage());
			assertEquals("Error message WRONG", "Test error", new ModelException("Test error").getMessage());
			LOG.info("Test OK");
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}

	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		LOG.info("TestModelException finished.");
	}
}
