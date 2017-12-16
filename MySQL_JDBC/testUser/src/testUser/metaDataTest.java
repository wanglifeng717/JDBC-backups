package testUser;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.Test;
/*元数据的重要就在于，他可以获取到列的个数，列的名称，列的别名，这样非常方便我们去赋值给属性。*/
public class metaDataTest {

	/**
	 * DatabaseMetaData是描述数据库的元数据对象，
	 * 可以由connection得到
	 * 了解即可
	 */
	@Test
	public void testDatabaseMetaData() {
		Connection connection=null;
		DatabaseMetaData data =null;
		ResultSet resultSet=null;
		try {
			connection=jdbcTools.getConnection();
			data = connection.getMetaData();
			//可以得到数据库版本
			int version = data.getDatabaseMajorVersion();
			System.out.println(version);
			//数据库的用户名,密码得不到
			String user = data.getUserName();
			System.out.println(user);
			
			//得到有哪些数据库
			resultSet = data.getCatalogs();
			while(resultSet.next())
			{
				System.out.println(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * ResultSetMetaData:描述结果集的元数据
	 * 可以得到结果集中的基本信息：结果集中有哪些列，列名，列的别名。
	 */
	@Test 
	public void testResultSetMetaData(){
		Connection connection =null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try {
			connection = jdbcTools.getConnection();
			String sql="select id,name,email,birth from customers";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			//1.得到resultSetMeteData 对象
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			//2.得到列的个数
			int columncount = resultSetMetaData.getColumnCount();
			System.out.println(columncount);
			//3.得到列名
			for(int i=0;i<columncount;i++)
			{
				String columnName = resultSetMetaData.getColumnName(i+1); 
				//4.得到列的别名
				String columnLabel = resultSetMetaData.getColumnLabel(i+1);
				System.out.println(columnName+":"+columnLabel);
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			jdbcTools.release(preparedStatement, connection);
		}
	}
	

}
