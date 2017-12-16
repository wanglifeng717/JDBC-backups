package DBUtils;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

public class CustomerDaoTest {

	CustomerDao customerDao = new CustomerDao();
	@Test
	public void testGet() {
		Connection connection=null;
		try {
			connection = jdbcTools.getConnection();
			String sql="select id ,name from customers where id=? ";
			Customer customer=customerDao.get(connection, sql, 9);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}

}
