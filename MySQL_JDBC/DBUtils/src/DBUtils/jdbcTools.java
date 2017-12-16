package DBUtils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;



public class jdbcTools {
	
	//一个项目一个数据库连接池就行了
	//数据库连接池拿到的connection对象关闭的时候，并不是真的关闭，而是把数据连接归还到数据库连接池中
	private static DataSource dataSource=null;
	static 
	{
		dataSource=new ComboPooledDataSource("helloc3p0");
	}
//这个类是数据库的工具方法，里面包含了很多数据库操作的方式
//	1.获取连接
//	2.关闭资源
//  3.处理事务的方法。
	
	public static Connection getConnection() throws Exception
	{
		
		return dataSource.getConnection();
//		//1.准备连接数据库的4个字符串
//				String driverClass =null;
//				String jdbcUrl=null;
//				String user=null;
//				String password=null;
//		//读取类路径下的jdbc.propertise文件
//				InputStream in =
//						jdbcTools.class.getClassLoader().getResourceAsStream("jdbc.properties");
//				Properties properties = new Properties();
//				properties.load(in);
//				driverClass= properties.getProperty("driver");
//				jdbcUrl = properties.getProperty("jdbcUrl");
//				user = properties.getProperty("user");
//				password = properties.getProperty("password");
////				加载数据库驱动程序（注册驱动,对应的Driver实现类中有注册驱动的静态代码块,所以不同写了）
////				这种注册的方式优点是可以往里面注册多个驱动，
//				Class.forName(driverClass);
////				DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
////				通过DriverManagerde的getConnection()方法获取数据库连接 
//				Connection connection =
//						DriverManager.getConnection(jdbcUrl,user,password);
//				return connection;
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
	//关闭数据库的方法。重载的方法。
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
//		更新的方法,执行SQL方法，主要是insert,update,delete.不含select
//		目前是存在问题的，因为我们每次操作的时候都要连接和断开操作
		public static void update(String sql)
		{
			Connection conn = null;
			Statement statement=null;
			try{
				conn= jdbcTools.getConnection();
				
				statement = conn.createStatement();
				
				statement.executeUpdate(sql);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				jdbcTools.release(statement,conn);
			}
		
		}
		/**
		 * 用preparedstatement方法去更新数据，减少拼接sql字符串的痛苦
		 * 1.执行sql语句，使用preparedstatement
		 * @param sql，后面的应该是可变参数的形式。构成方法的重载,
		 * 可以用数组的形式，但是传进来的时候你还要封装成数组，这样我想传几个就几个。
		 * 而且传进来后我可以当数组来用，所以比数组好
		 * @param objects
		 */
		public static void update(String sql,Object ... args)
		{
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			try
			{
				connection = jdbcTools.getConnection();
				preparedStatement = connection.prepareStatement(sql);
				for(int i=0;i<args.length;i++)//用操作数组的方式来给占位符赋值
				{
					preparedStatement.setObject(i+1,args[i]);
				}
				preparedStatement.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				jdbcTools.release(preparedStatement, connection);
			}
		}
		
		//处理数据库事务
		public static void commit(Connection connection){
			if(connection!=null)
				try {
					connection.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		//处理事务回滚
		public static void rollback(Connection connection)
		{
			if(connection!=null)
				try {
					connection.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		//开始事务，取消自动提交
		public static void beginTx(Connection connection)
		{
			if(connection!=null)
				try {
					connection.setAutoCommit(false);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
	
}
