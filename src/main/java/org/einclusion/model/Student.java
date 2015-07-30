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
@Table(name="Student")
@DynamicUpdate(value = true)
public class Student implements Serializable {
	private static final Logger LOG = Logger.getLogger(Student.class);
	private static final long serialVersionUID = 1001L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	Long id; // system generated
	String phone; // student phone
	String name; // student name surname
	String topic; // study topic
	Float swl; // student wiling to learn average
	Float ds; // digital skills average
	Float elm; // e-learning materials average
	Float ele; // e-learning environment average
	Float iws; // instructor willing share knowledge
	Float klbl; // knowledge before learning
	Float klal;// knowledge after learning
	Float sal; // student ability learn
	Float pu; // predicted usage
	Date submitDate; // date
	Integer ou; // observed usage
	Float puou; // combination of predicted usage and observed
	Float m2; // m2 risk

	void setName(String name) {
		this.name = name;
	}
	
	public static List<Student> getStudent() {
		List<Student> tmp = new LinkedList<Student>();
		try {
			TypedQuery<Student> query = entityManager.createQuery(
					"FROM Student", Student.class);

			List<Student> students = query.getResultList();
			return students;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
		}
		return tmp;
	}
	
	public static List<Student> getStudents(String topic) {
		List<Student> tmp = new LinkedList<Student>();
		try {
			TypedQuery<Student> query = entityManager.createQuery(
					"FROM Student WHERE Name IS NOT 'test' AND Topic IS '"+topic+"'" , Student.class);

			List<Student> students = query.getResultList();
			return students;

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
		}
		return tmp;
	}

	/**
	 * @param students
	 *            — Assessment to be stored Method relies on properly
	 *            initialized ModelManager.initModelDataManager(...) N.B. method
	 *            adds new entry if id is not set or merges with existing, if id
	 *            is set
	 */
	static Long setStudent(Student students) {
		EntityTransaction transaction = entityManager.getTransaction();
		Long id = -1l;
		try {
			transaction.begin();
			if (students.id != null && students.id > 0) {
				entityManager.merge(students);
			} else
				entityManager.persist(students);
			transaction.commit();
			// entityManager.flush();
			id = students.id;
			LOG.debug("Student ID:" + id);
			return id;
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			return id;
		}
	}

	static Student getStudent(Long id) {
		return entityManager.find(Student.class, id);
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
