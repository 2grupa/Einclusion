package org.einclusion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestModelManager {

	private static final Logger LOG = Logger.getLogger(TestModelManager.class);
	private static final String PERSISTENCE_SET = "test";
	private static String key = "test-key";
	private static String value;
	private static int intValue;
	private static float floatValue;

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
			
			ModelManager.setNumberValue(key, intValue);
			final int tmp2 = ModelManager.getIntValue(key);
			assertEquals(intValue, tmp2);
			
			ModelManager.setNumberValue(key, floatValue);
			final float tmp3 = ModelManager.getFloatValue(key);
			assertEquals(floatValue, tmp3, 0);
			

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
		
		
		try {
			Student assess = new Student();
			assess.name = "name" + value;
			ModelManager.setObjectValue("test-assessment", assess);
			Student tmp = new Student();
			tmp = (Student) ModelManager.getObjectValue("test-assessment",
					tmp.getClass());
			assertEquals(assess.name, tmp.name);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
		
		try{
			final Map<String, Object> props;
			final EntityManagerFactory factory;
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_SET);
			props = factory.getProperties();
			
			String url = props.get("hibernate.connection.url").toString();
			String tmp = ModelManager.getURL();
			
			assertEquals(url, tmp);
			
			String user = props.get("hibernate.connection.user").toString();
			user = ( user != null ) ? user : "";
			tmp = ModelManager.getUser();
			
			assertEquals(user, tmp);
			
			String password = "";
			try {
				password = props.get("hibernate.connection.password").toString();
			} catch (Exception e) {
				password = "";
			}
			tmp = ModelManager.getPassword();
			
			assertEquals(password, tmp);
			
			
		} catch( Exception e ){
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
