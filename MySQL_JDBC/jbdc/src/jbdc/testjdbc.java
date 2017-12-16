package jbdc;


import java.io.InputStream;
import java.sql.*;
import java.util.*;

import org.junit.Test;

public class testjdbc {

	
	public static void main1(String[] args) throws Exception {

		/* Driver是数据库厂商给我们实现的 类，我们可以通过Driver的实现类对象获取数据库连接
		 * 1.加入MYSQL驱动
		 * 2.解压驱动，添加的是jar文件
		 * 3.在当前项目下新建lib,加入驱动jar文件
		 * 4.右键，build-path
		 * */
		//1.创建一个Driver实现类的对象
		Driver driver = new com.mysql.jdbc.Driver();
		//2.准备连接数据库的基本信息：url,user,passworld
		String url = "jdbc:mysql://localhost:3306/mydata";
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "root");
		//3.调用Driver接口的connect(url,info)获取数据库连接
		Connection connection =  driver.connect(url, info);
		System.out.println(connection);
	}
	
	/*编写一个通用的方法，在不修改源程序的情况下，可以获取任何数据库的连接
	 * 解决方案，吧数据库的驱动Driver实现类的全名、URL，user，password放入一个配置文件中
	 * 通过修改配置文件的方式实现和具体数据库的解耦*/

	public static void main(String[] args) throws Exception
	{
		new testjdbc().getConnection();
		System.out.println(222);
	}
	
	@Test
	public  void testGetConnection()throws Exception
	{
		System.out.println(111);
		System.out.println(getConnection());
	}
	
	public  Connection getConnection()throws Exception
	{
		String driverClass =null;
		String jdbcUrl=null;
		String user=null;
		String password=null;
		//读取路径下的jdbc.properties文件
		//这种方式配置文件必须放到bin文件夹下，不然会找不到
		InputStream in =
				getClass().getClassLoader().getResourceAsStream("jdbc.properties");
		//System.out.println(getClass().getClassLoader());
		Properties properties = new Properties();
		properties.load(in);
		driverClass= properties.getProperty("driver");
		jdbcUrl = properties.getProperty("jdbcUrl");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		Driver driver =
				(Driver)Class.forName(driverClass).newInstance();
		Properties info = new Properties();
		info.put("user", user);
		info.put("password", password);
		Connection connection = driver.connect(jdbcUrl,info);
		//System.out.println(connection);
		return connection;
		
	}
}

 