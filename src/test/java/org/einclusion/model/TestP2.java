package org.einclusion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestP2 {
  static final String PERSISTENCE_SET = "test";

  @Test
  public void testGetRegression() {
    try {
      ModelManager.initModelManager(PERSISTENCE_SET);
      P2.getPrognosis();
    } catch (Exception e) {
      fail(e.getMessage() + " " + e.getCause());
    } finally {
      ModelManager.closeModelManager();
    }
  }

}
