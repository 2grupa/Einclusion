package org.einclusion.model;

import weka.core.*;
import weka.core.converters.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import static org.einclusion.model.ModelManager.*;

/**
 * This class is used to prepare Assessment data
 */

public class PrepareData {

  private static final Logger LOG = Logger.getLogger(PrepareData.class);

  /**
   * Clean data from Assessment table in database  
   */
  static void cleanAssessment() {
    transaction.begin();
    Query q = entityManager.createNativeQuery("delete from Assessment");
    q.executeUpdate();
    transaction.commit();
    LOG.info("Assesment table cleared successfully");
  }

  /**
   * Load data from arff file to the database
   */
  static void arff2db(String file) {
    try {
      LOG.info("Data loading from arff file to the database sarted");
      transaction.begin();
      Query q = entityManager
          .createNativeQuery("drop table if exists TEST_DATA");
      q.executeUpdate();
      transaction.commit();

      Instances data = new Instances(new BufferedReader(new FileReader(file)));
      data.setClassIndex(data.numAttributes() - 1);

      DatabaseSaver saver = new DatabaseSaver();
      saver.setDestination(getURL(), getUser(), getPassword());
      saver.setRelationForTableName(true);
      saver.setInstances(data);
      saver.writeBatch();

      // copy data from temporary table to Assessment table
      transaction.begin();
      q = entityManager
          .createNativeQuery("insert into assessment (SWL1, SWL2, DS1, DS2, KLBL, KLAL, ELE1, ELE2, ELM1, ELM2, IWS, PU, OU)"
              + "SELECT * FROM TEST_DATA");
      q.executeUpdate();
      transaction.commit();

      transaction.begin();
      q = entityManager
          .createNativeQuery("update assessment set modifydate = '"
              + new Timestamp(new Date().getTime())
              + "' where modifydate is null");
      q.executeUpdate();
      transaction.commit();
      LOG.info("Data from arff file copied to temporary table successfully");

    } catch (Exception e) {
      LOG.error(e.getMessage() + "  " + e.getCause());
    }
  }

  /**
   * calculate values for Assessment data
   */
  static void calculateValues() {
    List<Assessment> assessments = Assessment.getAssessments();
    for (Assessment a : assessments) {
      try {
        a.puou = (a.pu + a.ou) / 2;
        a.swl = (a.swl1 + a.swl2) / 2;
        a.ele = (a.ele1 + a.ele2) / 2;
        a.elm = (a.elm1 + a.elm2) / 2;
        a.ds = (a.ds1 + a.ds2) / 2;
        a.kfa = (a.iws + a.ele + a.elm) * a.klbl;
        a.sal = (a.klal - a.klbl) * 4 / ((5 - a.klbl) + 1);
        Assessment.setAssessment(a);
        Assessment tmp = Assessment.getAssessment(a.id);
        LOG.debug("Assesment: " + tmp.toString());
      } catch (Exception e) {
        LOG.error("Exception on Assessment saving " + e.getMessage() + " "
            + e.getCause());

      }
    }
  }
  
  static void csv2db(String file) {
		try {
			Query q;
			// READ FILE
			transaction.begin();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] value = line.split(",");
				Float SWL = (Float.parseFloat(value[3]) + Float.parseFloat(value[4])) / 2;
				Float DS = (Float.parseFloat(value[5]) + Float.parseFloat(value[6])) / 2;
				Float ELM = (Float.parseFloat(value[7]) + Float.parseFloat(value[8])) / 2;
				Float ELE = (Float.parseFloat(value[9]) + Float.parseFloat(value[10])) / 2;
				Float SAL = (float) 0;
				if (5 - Float.parseFloat(value[13]) == 0) {
					SAL = (float) 0;
				} else {
					SAL = (Float.parseFloat(value[12]) - Float.parseFloat(value[13])) * 4
							/ (5 - Float.parseFloat(value[13]));
				}
				SAL++;
				q = entityManager.createNativeQuery(
						"INSERT into Studenti (Numurs,Name,Tema,IWS,KLAL,KLBL,PU,IZPILDITS,SWL,DS,ELM,ELE,SAL,PUOU,M2)"
								+ "Values ('" + value[0] + "','" + value[1] + "','" + value[2] + "','" + value[11]
								+ "','" + value[12] + "','" + value[13] + "','" + value[14] + "','" + value[15] + "','"
								+ SWL + "','" + DS + "','" + ELM + "','" + ELE + "','" + SAL + "', 0,0)");
				q.executeUpdate();
			}
			br.close();
			transaction.commit();

		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
		}
	}
}
