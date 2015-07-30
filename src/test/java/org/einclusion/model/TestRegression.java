package org.einclusion.model;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRegression {
	private static final Logger LOG = Logger.getLogger(TestRegression.class);

	@BeforeClass
	public static void setUp() {
		LOG.info("TestRegression started...");
	}

	@Test
	public void test() {
		try {
			Regression reg = new Regression();
			double[] coeff = {5,1,2,3};
			String[] names = {"","a","b","c"};
			reg.coefficients = new HashMap<String, Double>();
			
			for (int i = 0; i < coeff.length; i++)
				reg.coefficients.put(names[i], coeff[i]);
			
			assertEquals("Regression toString is WRONG", 
					"regression koefficient values:{=5.0, a=1.0, b=2.0, c=3.0}",
					reg.toString());
			assertEquals("Regression toFormula is WRONG",
					"5.0 + a * 1.0 + b * 2.0 + c * 3.0",
					reg.toFormula());			
						
			LOG.info("Test OK");
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@AfterClass
	public static void tearDown() {
		LOG.info("TestRegression finished.");
	}
}
