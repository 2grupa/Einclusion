package org.einclusion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestM1 {
  static final String PERSISTENCE_SET = "test";

  @Test
  public void testGetRegression() {
    try {
      ModelManager.initModelManager(PERSISTENCE_SET);
      M1.getCentroids();
      M1.getClusters();
      } catch (Exception e) {
      fail(e.getMessage() + " " + e.getCause());
    }
    finally {
      ModelManager.closeModelManager();
    }
  }

}
