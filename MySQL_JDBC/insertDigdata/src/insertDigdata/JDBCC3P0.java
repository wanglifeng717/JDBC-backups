package insertDigdata;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;



public class JDBCC3P0 {

	
	@Test
	public void testc3p0() throws PropertyVetoException, SQLException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver            
		cpds.setJdbcUrl( "jdbc:mysql:///mydata" );
		cpds.setUser("root");                                  
		cpds.setPassword("root");   
		System.out.println(cpds.getConnection());
	}
	//读配置文件的方式
	/*首先创建c3p0-config.xml文件，参考AppendixB内容
	 * 创建ComboPooledDataSource实例
	 * 从DataSource实例中获取连接
	 * */
	@Test
	public void testc3p0_2() throws SQLException{
		DataSource dataSource = new ComboPooledDataSource("helloc3p0");
		System.out.println(dataSource.getConnection());
		ComboPooledDataSource comboPooledDataSource=(ComboPooledDataSource) dataSource;
		System.out.println(comboPooledDataSource.getMaxStatements());
	}

}
