package org.einclusion.model;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import static org.einclusion.model.ModelManager.*;

public class P3 {
  static final Logger LOG = Logger.getLogger(P3.class);
  static final String REGRESSION_KEY = "M3-regression";
  static final String QUERY_STRING = "from Assessment as a where "
      + "a.swl>0 and a.sal>0 and a.elm>0 and a.iws>0 and a.ele>0 and a.puou>0";

  static void getPrognosis() {
    try {
      LOG.info("P3 prognosis started");
      Regression regression = (Regression) ModelManager.getObjectValue(REGRESSION_KEY,
          Regression.class);
      String formula = regression.toFormula();
      LOG.info("P3 Regression formula: " + formula);

      TypedQuery <Assessment> query = entityManager.createQuery(QUERY_STRING,
          Assessment.class);
      List<Assessment> assessments = query.getResultList();

      for (Assessment a : assessments) {
        transaction.begin();
        Query updateQuery = entityManager
            .createQuery("Update Assessment a set a.prog3 =(" + formula
                + ") where a.id=" + a.id);
        updateQuery.executeUpdate();
        transaction.commit();
      }
      LOG.info("P3 prognosis finished successfully");
    } catch (Exception e) {
      LOG.error(e.getMessage() + " " + e.getCause());
    }
  }
}
