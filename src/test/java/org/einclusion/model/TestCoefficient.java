package org.einclusion.model;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.einclusion.frontend.Coefficient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCoefficient {

	private static final Logger LOG = Logger.getLogger(TestCoefficient.class);
	private static Coefficient tmp;
	
	@BeforeClass
	public static void setUp() {
		LOG.info("TestCoefficient started...");
	}
	@AfterClass
	public static void tearDown() throws Exception {
		LOG.info("TestCoefficient completed.");
	}

	@Test
	public void testCoefficientConstructors() {
		try {
			String name = "Tester";
			String value = "1.234";
			
			tmp = new Coefficient(name);
			assertEquals("Name is WRONG", name, tmp.name);
			assertEquals("Value is WRONG", "0", tmp.value);
			
			name = "OtherTester";
			tmp = new Coefficient(name, value);
			assertEquals("Name is WRONG", name, tmp.name);
			assertEquals("Value is WRONG", value, tmp.value);
			assertNotEquals("Value is not WRONG", "0", tmp.value);
			
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}

}
