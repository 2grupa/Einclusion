package org.einclusion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestM3 {
  static final String PERSISTENCE_SET = "test";

  @Test
  public void testGetRegression() {
    try {
      ModelManager.initModelManager(PERSISTENCE_SET);
      M3.getRegression();
    } catch (Exception e) {
      fail(e.getMessage() + " " + e.getCause());
    } finally {
      ModelManager.closeModelManager();
    }
  }

}
