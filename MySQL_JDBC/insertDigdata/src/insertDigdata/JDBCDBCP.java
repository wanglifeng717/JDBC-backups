package insertDigdata;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;
//数据库连接池和创建方法。
public class JDBCDBCP {

	/**
	 * 使用DBCP数据库连接池
	 * 1.加入jar包 2个。common pool
	 * 2.创建数据库连接池
	 * 3.为数据源实例指定必须的属性
	 * 4.从数据源中获取数据库连接
	 * @throws SQLException 
	 * @throws InterruptedException 
	 */
	@Test
	public void testDBCP() throws SQLException, InterruptedException {
		BasicDataSource dataSource=null;
		//创建DBCP数据源实例
		 dataSource = new BasicDataSource();
		 final BasicDataSource dataSource2=dataSource;
		//为数据源实例指定必须的属性
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql:///mydata");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		//数据源一些可选的属性
		//指定初始化连接池中初始化的连接数
		dataSource.setInitialSize(5);
		//指定最大的连接数，同一时刻可以同时向数据库申请的连接数
		dataSource.setMaxTotal(5);
		//指定小连接数，在数据库连接池中保存的最小的空闲连接的数量
		dataSource.setMinIdle(2);
		//等待数据库连接池分配连接的最长时间，单位毫秒，超出就抛出异常
		dataSource.setMaxWaitMillis(5*1000);		
		//从数据源中获取数据库连接
		Connection connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		 connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		 connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		connection = dataSource.getConnection();
		System.out.println(connection.getClass());
		
		Connection connection2 = dataSource.getConnection();
		System.out.println("第五个连接开启"+connection2.getClass());
		/*我们之前的连接都没关，我们到第六个的时候，如果3秒还是连接不上就会抛出异常
		 * 我们在3秒内把那个连接释放掉就可以，测试的时候我们用一个线程获取连接，用一个线程释放连接*/
		
		new Thread(){
			public void run() {
				Connection conn;
				
				try {
					//这是新版本的特性，必须是
					conn = dataSource2.getConnection();
					System.out.println("第六个获得连接"+conn.getClass());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			};
		}.start();
		
		Thread.sleep(4000);
		System.out.println("第五个连接关闭");
		connection2.close();
		
	}
	//用配置文件的方式去创建数据库连接，这种方式更加的通用
	@Test
	public void testDBCPWithDataSourceFactory() throws Exception{
		Properties properties = new Properties();
		InputStream inputStream= JDBCDBCP.class.getClassLoader().getResourceAsStream("dbcp.properties");
		properties.load(inputStream);
		DataSource dataSource = 
				BasicDataSourceFactory.createDataSource(properties);
		
		System.out.println(dataSource.getConnection());
		BasicDataSource basicDataSource =(BasicDataSource)dataSource;
		System.out.println(basicDataSource.getInitialSize());
		System.out.println(basicDataSource.getMaxTotal());
		System.out.println(basicDataSource.getMinIdle());
		System.out.println(basicDataSource.getMaxWaitMillis());
		
		//basicDataSource.close();
	}

}
