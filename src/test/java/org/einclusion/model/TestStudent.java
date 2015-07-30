package org.einclusion.model;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.*;

@FixMethodOrder
public class TestStudent {
	private static final Logger LOG = Logger.getLogger(TestStudent.class);

	@BeforeClass
	public static void setUp() {
		LOG.info("TestModelManager started...");
		ModelManager.initModelManager("test");
	}

	@Test
	public void test() {
	/*	try {
			Date date = new Date();
			Student student = new Student();

			// Test creation of new entry
			student.name = "Name0";
			student.submitDate = date;
			final Long id = Student.setStudent(student);
			Student tmp = Student.getStudent(id);
			assertEquals("ID is WRONG", id, tmp.id);
			assertEquals("Name is WRONG", student.name, tmp.name);
			assertEquals("SubmitDate is WRONG", student.submitDate,
					tmp.submitDate);
			LOG.info("Creation of new entry OK");

			// Test update
			student = Student.getStudent(id);
			date = new Date();
			student.name = "Name";
			student.submitDate = date;
			student.id = id;
			Student.setStudent(student);
			tmp = Student.getStudent(id);
			assertEquals("ID after update is WRONG", student.id, tmp.id);
			assertEquals("Name after update is WRONG", student.name,
					tmp.name);
			assertEquals("SubmitDate after update is WRONG",
					student.submitDate, tmp.submitDate);
			LOG.info("Student:\n" + student);
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
			final List<Student> student = Student.getStudent();
			LOG.info("Selected elements:" + student.size());
			int i = 0;
			for (Student a : student) {
				a.name = "Name" + Integer.toString(i);
				Student.setStudent(a);
				Student tmp = Student.getStudent(a.id);
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
	}*/
}
}