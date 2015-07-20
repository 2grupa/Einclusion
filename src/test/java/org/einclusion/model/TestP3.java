package org.einclusion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestP3 {
  static final String PERSISTENCE_SET = "test";

  @Test
  public void testGetRegression() {
    try {
      ModelManager.initModelManager(PERSISTENCE_SET);
      P3.getPrognosis();
    } catch (Exception e) {
      fail(e.getMessage() + " " + e.getCause());
    } finally {
      ModelManager.closeModelManager();
    }
  }

}
