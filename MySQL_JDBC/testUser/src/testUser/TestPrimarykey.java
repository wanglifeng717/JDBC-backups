package testUser;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;

public class TestPrimarykey {

	/*取得数据库自动生成的主键，只适用于MYSQL这种自增的主键，orcal是不行的*/
	@Test
	public void testGetKeyValue() {
		Connection connection = null;
		PreparedStatement preparedStatement=null;
		try {
			connection = jdbcTools.getConnection();
			String sql="insert into customers(name,email) values(?,?)";
			//通过重载的方法来获取自动生成的ID
			preparedStatement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, "abcde");
			preparedStatement.setString(2, "123@qq.com");
			preparedStatement.executeUpdate();
			//获取包含了新生成主键的resultset对象，结果集中就一列，就是那个主键值
			ResultSet resultSet = preparedStatement.getGeneratedKeys();
			if(resultSet.next()){
				System.out.println(resultSet.getObject(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			jdbcTools.release(preparedStatement, connection);
		}
		 
	}

}
