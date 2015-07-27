package org.einclusion.model;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.experiment.InstanceQuery;
import static org.einclusion.model.ModelManager.*;

public class InstanceManager {
	private static final Logger LOG = Logger.getLogger(InstanceManager.class);

	static Instances retrieveModelInstances(String queryString) {
		InstanceQuery instanceQuery;
		try {
			if (persistenceSet == null)
				throw new ModelException(
						"ModelManager is not initialized. \n"
								+ "Run ModelManager.initModelManager(PERSISTENCE_SET) before");
			instanceQuery = new InstanceQuery();
			instanceQuery.setDatabaseURL(getURL());
			instanceQuery.setUsername(getUser());
			instanceQuery.setPassword(getPassword());
			instanceQuery.setQuery(queryString);
			Instances data = instanceQuery.retrieveInstances();
			return data;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			return null;
		}
	}
}
