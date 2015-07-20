package org.einclusion.model;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.*;

@FixMethodOrder
public class TestAssessment {
  private static final Logger LOG = Logger.getLogger(TestAssessment.class);
  @BeforeClass
  public static void setUp() {
    LOG.info("TestModelManager started...");
    ModelManager.initModelManager("test");
  }

  @Test
  public void test() {
    try {
      Date date = new Date();
      Assessment assessment = new Assessment();

      // Test creation of new entry
      assessment.name = "Name0";
      assessment.surname = "Surname0";
      assessment.submitDate = date;
      final Long id = Assessment.setAssessment(assessment);
      Assessment tmp = Assessment.getAssessment(id);
      assertEquals("ID is WRONG", id, tmp.id);
      assertEquals("Name is WRONG", assessment.name, tmp.name);
      assertEquals("Surname is WRONG", assessment.surname, tmp.surname);
      assertEquals("SubmitDate is WRONG", assessment.submitDate, tmp.submitDate);
      LOG.info("Creation of new entry OK");

      // Test update
      assessment = Assessment.getAssessment(id);
      date = new Date();
      assessment.name = "Name";
      assessment.surname = "Surname";
      assessment.submitDate = date;
      assessment.id = id;
      Assessment.setAssessment(assessment);
      tmp = Assessment.getAssessment(id);
      assertEquals("ID after update is WRONG", assessment.id, tmp.id);
      assertEquals("Name after update is WRONG", assessment.name, tmp.name);
      assertEquals("Surname after update is WRONG", assessment.surname, tmp.surname);
      assertEquals("SubmitDate after update is WRONG", assessment.submitDate, tmp.submitDate);
      LOG.info("Assessment:\n" + assessment);
      LOG.info("Update of the entry OK");

      // Check getting list of entries
      getList();

    } catch (Exception e) {
      LOG.error(e.getMessage() + " " + e.getCause());
      fail();
    }

  }

  private void getList() {
    try {
      final List<Assessment> assessments = Assessment.getAssessments();
      LOG.info("Selected elements:" + assessments.size());
      int i = 0;
      for (Assessment a : assessments) {
        a.name="Name"+ Integer.toString(i);
        a.modifyDate = new Date();
        Assessment.setAssessment(a);
        Assessment tmp = Assessment.getAssessment(a.id);
        assertEquals(tmp.name, "Name" + Integer.toString(i));
        i++;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage() + " " + e.getCause());
      fail();
    }
  }

  @AfterClass
  public static void tearDown() {
    ModelManager.closeModelManager();
    LOG.info("Test finished successfully");
  }
}
