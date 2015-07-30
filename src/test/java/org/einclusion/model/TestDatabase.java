package org.einclusion.model;

import static org.einclusion.model.ModelManager.entityManager;
import static org.einclusion.model.ModelManager.transaction;
import static org.junit.Assert.*;

import javax.persistence.Query;

import org.junit.Test;

public class TestDatabase {
	static final String PERSISTENCE_SET = "test";

	@Test
	public void testDatabase() {
		try {
			ModelManager.initModelManager(PERSISTENCE_SET);
			transaction.begin();
			Query q = entityManager
					.createNativeQuery("Insert into Student (SWL, DS, KLBL, KLAL, ELE, ELM, IWS, PU, OU, TOPIC)"
							+ " values(5.0, 4.0, 3.0, 2.0, 1.0, 4.0, 3.0, 2.0, 1.0, 'Cleaning');");
			q.executeUpdate();

			q = entityManager
					.createNativeQuery("Delete from Student where TOPIC='Cleaning';");
			q.executeUpdate();

			transaction.commit();
		} catch (Exception e) {
			fail(e.getMessage() + " " + e.getCause());
		} finally {
			ModelManager.closeModelManager();
		}
	}

}
