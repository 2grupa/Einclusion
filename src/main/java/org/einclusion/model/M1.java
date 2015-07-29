package org.einclusion.model;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.clusterers.EM;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import static org.einclusion.model.InstanceManager.*;

public class M1 {
	static final String CLUSTER_KEY = "M1-clusters";
	static final String CENTROID_KEY = "M1-centroids";
	private static final Logger LOG = Logger.getLogger(M1.class);
	static final String QUERY_STRING = "SELECT SWL,  DS,  SAL,  ELM,  IWS,  ELE, PU,  OU from assessment where "
			+ "SWL>0 and DS>0 and SAL>0 and ELM>0 and IWS>0 and ELE>0 and PU>0 and OU>0";

	static void getClusters() {
		int clusters;
		try {
			Instances data = retrieveModelInstances(QUERY_STRING);
			ClusterEvaluation eval = new ClusterEvaluation();
			Clusterer clusterer = new EM(); // new clusterer instance, default
											// options
			clusterer.buildClusterer(data); // build clusterer
			eval.setClusterer(clusterer); // the cluster to evaluate
			eval.evaluateClusterer(data); // data to evaluate the clusterer on

			clusters = clusterer.numberOfClusters();
			ModelManager.setNumberValue(CLUSTER_KEY, clusters);
			LOG.info("M1-clusters: " + clusters);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void getCentroids() {
		int clusters;
		try {
			Instances data = retrieveModelInstances(QUERY_STRING);

			// create the model
			SimpleKMeans kMeans = new SimpleKMeans();
			clusters = ModelManager.getIntValue(CLUSTER_KEY);
			kMeans.setNumClusters(clusters);
			kMeans.buildClusterer(data);

			// print out the cluster centroids
			Instances centroids = kMeans.getClusterCentroids();
			for (int i = 0; i < centroids.numInstances(); i++) {
				LOG.info("Centroid " + i + 1 + ": "
						+ centroids.instance(i).toString());
			}
			ModelManager.setObjectValue(CENTROID_KEY, centroids);
		} catch (Exception e) {
			LOG.error("getCentroids() error:" + e.getMessage() + " "
					+ e.getCause());
		}
	}
}
