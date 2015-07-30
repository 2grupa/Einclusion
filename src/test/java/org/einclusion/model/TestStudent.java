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
		LOG.info("TestStudent started...");
		ModelManager.initModelManager("test");
	}

	@Test
	public void testStudent() {
		try {
			Date date = new Date();
			Long id = 1L;
			String name = "Test" + date;
			
			Student student = Student.getStudent(id);
			String originalName = student.name;
			student.setName(name);
			Student.setStudent(student);
			
			student = Student.getStudent(id);
			LOG.info("Student:\n" + student);
			assertEquals("Name is WRONG", name, student.name);
			LOG.info("Set/Get Student entry OK");
			
			LOG.info("Reversing changes...");
			student.setName(originalName);
			Student.setStudent(student);

		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}
	
	@Test
	public void getList() {
		try {
			final List<Student> student = Student.getStudent();
			LOG.info("Selected elements:" + student.size());
			int i = 0;
			for (Student a : student) {
				if (a.name.equals("test"))
					i++;
			}
			assertEquals("Listed object count is WRONG", 134, i);
			LOG.info("Test OK");
		} catch (Exception e) {
			LOG.error(e.getMessage() + " " + e.getCause());
			fail();
		}
	}	
	
	@AfterClass
	public static void tearDown() {
		ModelManager.closeModelManager();
		LOG.info("TestStudent finished.");

	}
}