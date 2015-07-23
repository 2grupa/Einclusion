package org.einclusion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestModelManager {

	private static final Logger LOG = Logger.getLogger(TestModelManager.class);
	private static final String PERSISTENCE_SET = "test";
	private static String key = "test-key";
	private static String value;

	@BeforeClass
	public static void setUp() {
		LOG.info("TestModelManager started...");
		ModelManager.initModelManager(PERSISTENCE_SET);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		value = dateFormat.format(date);
	}

	@Test
	public void test() {
		try {
			ModelManager.setStringValue(key, value);
			// for testing
			// LOG.debug("Test key value ' " + value + "'  stored");
			final String tmp = ModelManager.getStringValue(key);
			assertEquals(value, tmp);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}

		try {
			Assessment assess = new Assessment();
			assess.name = "name" + value;
			ModelManager.setObjectValue("test-assessment", assess);
			Assessment tmp = new Assessment();
			tmp = (Assessment) ModelManager.getObjectValue("test-assessment",
					tmp.getClass());
			assertEquals(assess.name, tmp.name);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}

	@AfterClass
	public static void tearDown() {
		ModelManager.closeModelManager();
		LOG.info("TestModelManager finished successfully");
	}
}
