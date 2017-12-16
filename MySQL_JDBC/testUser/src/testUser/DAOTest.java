package testUser;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.List;

import org.junit.Test;

public class DAOTest {

	DAOyouhua dao = new DAOyouhua();
	@Test
	public void testUpdate() {
		String sql="insert into customers values(?,?,?,?)";
		dao.update(sql, 3,"xiaoming","dd",new Date(new java.util.Date().getTime()));
	}

	@Test
	public void testGet() {
		String sql="select flowid flowId,type_en type,idcard idCard,examcard examCard,studentname studentName,"+
			       "location ,grade from examstudent where type_en=?";
		Student student = dao.get(Student.class, sql, 4);
		System.out.println(student);
	}

	@Test
	public void testGetForList() {
		String sql="select flowid flowId,type_en type,idcard idCard,examcard examCard,studentname studentName,"+
			       "location ,grade from examstudent where type_en=? ";		
		List<Student> students = dao.getForList(Student.class, sql, 4);
		System.out.println(students);
	}

	@Test
	public void testGetForValue() {
		String sql="select examcard from examstudent where flowid=?";	
		String examCard = dao.getForValue(sql,2);
		System.out.println(examCard);
	}

}
