package org.einclusion.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.einclusion.frontend.Coefficient;
import org.einclusion.frontend.RegressionModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRegressionModel {
	private static final Logger LOG = Logger.getLogger(TestRegressionModel.class);
	private static RegressionModel tmp;
	
	@BeforeClass
	public static void setUp() throws Exception {
		LOG.info("TestRegressionModel started...");
	}
	@AfterClass
	public static void tearDown() throws Exception {
		LOG.info("TestRegressionModel completed.");
	}
	
	@Test
	public void testConstructor1() {
		try {
			String key = "Regression1";
			String value = "{\"coefficients\":{\"\":1,\"x\":2,\"y\":3}}";
			tmp = new RegressionModel(key, value);
			assertEquals("Key is WRONG", key, tmp.key);
			assertEquals("Value is WRONG", value, tmp.value);
			assertEquals("Coefficient Count is WRONG", 3, tmp.coefficients.size());
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@Test
	public void testConstructor2() {
		try {
			String key = "Regression2";
			String shortValue = "{=1, x=2, y=3}";
			String value = "{\"coefficients\":{\"\":1,\"x\":2,\"y\":3}}";
			tmp = new RegressionModel(key, shortValue, 0);
			assertEquals("Key is WRONG", key, tmp.key);
			assertEquals("Value is WRONG", value, tmp.value);
			assertEquals("Coefficient Count is WRONG", 3, tmp.coefficients.size());
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@Test
	public void testGetCoefficients() {
		try {
			String key = "Regression3";
			String value = "{\"coefficients\":{\"\":1,\"alfa\":1,\"beta\":1,\"landa\":1}}";
			tmp = new RegressionModel(key, value);
			
			assertEquals("Coefficient Count is WRONG", 4, tmp.coefficients.size());
			
			double sum = 0;
			ArrayList<Coefficient> coefList = tmp.coefficients;
			for (Coefficient c: coefList)
				sum += Double.parseDouble(c.value);
			assertEquals("Coefficient sum is WRONG", 4, sum, 0);
			
			tmp.value = "{\"coefficients\":{\"\":0.5,\"alfa\":0.75,\"beta\":0.25,\"landa\":1}}";
			tmp.getCoefficients();
			sum = 0;
			coefList = tmp.coefficients;
			
			for (Coefficient c: coefList)
				sum += Double.parseDouble(c.value);
			assertEquals("Coefficient sum is WRONG", 3, sum, 0.5);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@Test
	public void testHasCoefficient() {
		try {
			String key = "Regression4";
			String value = "{\"coefficients\":{\"\":1,\"a\":2,\"b\":3,\"c\":4,\"d\":5,\"e\":6}}";
			tmp = new RegressionModel(key, value);
			
			assertTrue("Coefficient NOT found", tmp.hasCoefficientValue("a"));
			assertTrue("Coefficient NOT found", tmp.hasCoefficientValue("c"));
			assertTrue("Coefficient NOT found", tmp.hasCoefficientValue("e"));
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@Test
	public void testGetM2regressionDegree() {
		try {
			String key = "Regression5";
			String value = "{\"coefficients\":{\"\":-5,\"SWL\":2,\"SAL\":3,\"ELM\":5,\"IWS\":6,\"ELE\":4}}";
			tmp = new RegressionModel(key, value);
			float result = RegressionModel.getM2regressionDegree(tmp, 2f, 5f, 0.5f, 0.25f, 2f);
			float verifyResult = 2*2 + 3*5 + 5*0.5f + 6*0.25f + 4*2 -5;
			assertEquals(verifyResult, result, 0);
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
}
