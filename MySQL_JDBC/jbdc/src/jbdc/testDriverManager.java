package jbdc;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import org.junit.Test;

public class testDriverManager {

//	DriverManager是驱动的管理类
//	1.可以通过重载的getconnection方法获取数据库连接，较为方便
//	2.可以同时管理多个驱动程序
//	
	public static void main(String[] args) {
		

	}
	@Test
//	数据库连接的最终版本
	public void testDriverManager1() throws Exception{
//		1.准备连接数据库的4个字符串
		String driverClass =null;
		String jdbcUrl=null;
		String user=null;
		String password=null;
		//读取类路径下的jdbc.propertise文件
		InputStream in =
				getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		properties.load(in);
		driverClass= properties.getProperty("driver");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
//		加载数据库驱动程序（注册驱动,对应的Driver实现类中有注册驱动的静态代码块,所以不同写了）
//		这种注册的方式优点是可以往里面注册多个驱动，
		Class.forName(driverClass);
//		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
//		通过DriverManagerde的getConnection()方法获取数据库连接 
		Connection connection =
				DriverManager.getConnection(jdbcUrl,user,password);
		
		System.out.println(connection);
		
	}
	

}
