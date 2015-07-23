package org.einclusion.model;

import static org.einclusion.model.ModelManager.entityManager;
import static org.einclusion.model.ModelManager.transaction;
import static org.junit.Assert.*;

import javax.persistence.Query;

import org.junit.Test;

public class TestDatabase {
	static final String PERSISTENCE_SET = "test";

	@Test
	public void testGetRegression() {
		try {
			ModelManager.initModelManager(PERSISTENCE_SET);
			// copy data from temporary table to Assessment table
			transaction.begin();
			Query q = entityManager
					.createNativeQuery("insert into assessment (SWL1, SWL2, DS1, DS2, KLBL, KLAL, ELE1, ELE2, ELM1, ELM2, IWS, PU, OU)"
							+ " values(5.0, 4.0, 3.0, 2.0, 1.0, 4.0, 3.0, 2.0, 1.0, 1.0, 5.0, 3.0, 2);");
			q.executeUpdate();
			transaction.commit();
		} catch (Exception e) {
			fail(e.getMessage() + " " + e.getCause());
		} finally {
			ModelManager.closeModelManager();
		}
	}

}
