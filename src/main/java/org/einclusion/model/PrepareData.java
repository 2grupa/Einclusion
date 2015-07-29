package org.einclusion.model;

import java.io.*;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import static org.einclusion.model.ModelManager.*;

/**
 * This class is used to prepare Student data
 */

public class PrepareData {

	private static final Logger LOG = Logger.getLogger(PrepareData.class);

	static void csv2db(String file) {
		try {
			Query q;
			// READ FILE
			transaction.begin();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] value = line.split(",");
				Float SWL = (Float.parseFloat(value[3]) + Float
						.parseFloat(value[4])) / 2;
				Float DS = (Float.parseFloat(value[5]) + Float
						.parseFloat(value[6])) / 2;
				Float ELM = (Float.parseFloat(value[7]) + Float
						.parseFloat(value[8])) / 2;
				Float ELE = (Float.parseFloat(value[9]) + Float
						.parseFloat(value[10])) / 2;
				Float SAL = (float) 0;
				if (5 - Float.parseFloat(value[13]) == 0) {
					SAL = (float) 0;
				} else {
					SAL = (Float.parseFloat(value[12]) - Float
							.parseFloat(value[13]))
							* 4
							/ (5 - Float.parseFloat(value[13]));
				}
				SAL++;
				q = entityManager
						.createNativeQuery("INSERT into Student (Phone,Name,Topic,IWS,KLAL,KLBL,PU,SubmitDate,SWL,DS,ELM,ELE,SAL,PUOU,M2)"
								+ "SELECT '"
								+ value[0]
								+ "','"
								+ value[1]
								+ "','"
								+ value[2]
								+ "','"
								+ value[11]
								+ "','"
								+ value[12]
								+ "','"
								+ value[13]
								+ "','"
								+ value[14]
								+ "','"
								+ value[15]
								+ "','"
								+ SWL
								+ "','"
								+ DS
								+ "','"
								+ ELM
								+ "','"
								+ ELE
								+ "','"
								+ SAL
								+ "', 0,0 WHERE NOT EXISTS (SELECT TOPIC,PHONE FROM STUDENT WHERE PHONE='"
								+ value[0]
								+ "' "
								+ "AND TOPIC="
								+ " '"
								+ value[2] + "')");
				q.executeUpdate();
			}
			br.close();
			transaction.commit();

		} catch (Exception e) {
			System.out.println(e.getMessage() + " " + e.getCause());
		}
	}
}
