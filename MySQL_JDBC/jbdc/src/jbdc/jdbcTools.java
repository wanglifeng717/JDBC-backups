package jbdc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class jdbcTools {
//这个类是数据库的工具方法，里面包含了很多数据库操作的方式
//	1.获取连接
//	2.关闭资源
	
	public static Connection getConnection() throws Exception
	{
		//1.准备连接数据库的4个字符串
				String driverClass =null;
				String jdbcUrl=null;
				String user=null;
				String password=null;
		//读取类路径下的jdbc.propertise文件
				InputStream in =
						jdbcTools.class.getClassLoader().getResourceAsStream("jdbc.properties");
				Properties properties = new Properties();
				properties.load(in);
				driverClass= properties.getProperty("driver");
				jdbcUrl = properties.getProperty("jdbcUrl");
				user = properties.getProperty("user");
				password = properties.getProperty("password");
//				加载数据库驱动程序（注册驱动,对应的Driver实现类中有注册驱动的静态代码块,所以不同写了）
//				这种注册的方式优点是可以往里面注册多个驱动，
				Class.forName(driverClass);
//				DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
//				通过DriverManagerde的getConnection()方法获取数据库连接 
				Connection connection =
						DriverManager.getConnection(jdbcUrl,user,password);
				return connection;
	}
//	关闭数据库连接的方法
	public static void release(Statement statement,Connection conn){
		if(statement!=null){
			try{
				statement.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(conn !=null){
			try{
				conn.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
		public static void release(Statement statement,Connection conn,ResultSet rs)
		{
			if(statement!=null){
				try{
					statement.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			if(conn !=null){
				try{
					conn.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			if(rs !=null){
				try{
					rs.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
}
