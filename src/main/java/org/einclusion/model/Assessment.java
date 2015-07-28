package org.einclusion.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import org.apache.log4j.Logger;
import org.hibernate.annotations.DynamicUpdate;

import static org.einclusion.model.ModelManager.*;

@Entity
@DynamicUpdate(value = true)
public class Assessment implements Serializable {
	private static final Logger LOG = Logger.getLogger(Assessment.class);
	private static final long serialVersionUID = 1001L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	Long id; // system generated
	String name; // student name surname
	String topic; // study topic
	Float swl1; // student wiling learn 1
	Float swl2; // student wiling learn 2
	Float swl; // student wiling to learn average
	Float ds1; // digital skills 1
	Float ds2; // digital skills 2
	Float ds; // digital skills average
	Float elm1; // e-learning materials 1
	Float elm2; // e-learning materials 2
	Float elm; // e-learning materials average
	Float ele1; // e-learning environment 1
	Float ele2; // e-learning environment 2
	Float ele; // e-learning environment average
	Float iws; // instructor willing share knowledge
	Float klbl; // knowledge before learning
	Float klal;// knowledge after learning
	Float sal; // student ability learn
	Float pu; // predicted usage
	Date submitDate; // date
	Integer ou; // observed usage
	Float puou; // combination of predicted usage and observed
	String numurs;

	// nepiecieshamie mainigie M1 un M3 modeliem
	Float kfa; // knowledge flow acceleration

	Assessment() {
		super();
	}

	Assessment(Long id, String code, String name, String email,
			String topic, Float swl1, Float swl2, Float swl, Float ds1,
			Float ds2, Float ds, Float sal, Float elm1, Float elm2, Float elm,
			Float iws, Float ele1, Float ele2, Float ele, Float pu, Float kfa,
			Float klbl, Float klal, Float puou, Integer ou, Date submitDate,
			String numurs) {

		this.id = id;
		this.name = name;
		this.topic = topic;
		this.swl1 = swl1;
		this.swl2 = swl2;
		this.swl = swl;
		this.ds1 = ds1;
		this.ds2 = ds2;
		this.ds = ds;
		this.sal = sal;
		this.elm1 = elm1;
		this.elm2 = elm2;
		this.elm = elm;
		this.iws = iws;
		this.ele1 = ele1;
		this.ele2 = ele2;
		this.ele = ele;
		this.pu = pu;
		this.kfa = kfa;
		this.klbl = klbl;
		this.klal = klal;
		this.puou = puou;
		this.ou = ou;
		this.submitDate = submitDate;
		this.numurs = numurs;

	}

	void setName(String name) {
		this.name = name;
	}

	public static List<Assessment> getAssessments() {
		List<Assessment> tmp = new LinkedList<Assessment>();
		try {
			TypedQuery<Assessment> query = entityManager.createQuery(
					"FROM Assessment", Assessment.class);

			List<Assessment> assessments = query.getResultList();
			return assessments;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
		}
		return tmp;
	}

	/**
	 * @param assessment
	 *            — Assessment to be stored Method relies on properly
	 *            initialized ModelManager.initModelDataManager(...) N.B. method
	 *            adds new entry if id is not set or merges with existing, if id
	 *            is set
	 */
	static Long setAssessment(Assessment assessment) {
		EntityTransaction transaction = entityManager.getTransaction();
		Long id = -1l;
		try {
			transaction.begin();
			if (assessment.id != null && assessment.id > 0) {
				entityManager.merge(assessment);
			} else
				entityManager.persist(assessment);
			transaction.commit();
			// entityManager.flush();
			id = assessment.id;
			LOG.debug("Assessment ID:" + id);
			return id;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			return id;
		}
	}

	static Assessment getAssessment(Long id) {
		return entityManager.find(Assessment.class, id);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				LOG.error(ex.getMessage() + " " + ex.getCause());
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}
}
