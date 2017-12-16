package cunchuguocheng;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Test;

public class testDBUtils {

	QueryRunner queryRunner = new QueryRunner();
	/**
	 * 测试QueryRunner类的Update方法
	 * 内部实现和我们自己写的DAO实现都是差不多的。
	 */
	@Test
    public void testQueryRunnerUpdate() {
		//1.创建testQueryRunner的实现类
		//QueryRunner queryRunner = new QueryRunner();
		//2.使用其updata方法,增删改都只要改SQL就行了。
		String sql ="delete from customers where id in(?,?)";
		Connection connection=null;
		try {
			connection=jdbcTools.getConnection();
			//System.out.println(connection);
			queryRunner.update(connection, sql, 3,4);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * 测试查询的方法
	 */
	@Test
	public void testQuery() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			String sql = "select name,email from customers where id=9 ";
			Object object=queryRunner.query(connection, sql, new myResultSetHandler());
			System.out.println(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * 上面的方法是通用的，具体对结果集的处理是我们自己定义的hander里面自己处理的。
	 * @author mdm
	 *
	 */
	class myResultSetHandler implements ResultSetHandler{
		public Object handle(ResultSet resultSet) throws SQLException
		{
			List<Customer> customers=new ArrayList<Customer>() ;
				
			while(resultSet.next())
			{
				String name = resultSet.getString(1);
				String email=resultSet.getString(2);
				Customer customer = new Customer(3,name,email,null);
				customers.add(customer);
			}
			//System.out.println("handle...");
			return customers;
		}
	}
	/**
	 * 重點學習一下handler 這些handler 非常好用，不用我們自己瀉處理代碼了
	 * 把第一条记录转为创建beanHanlder对象传入class参数对应的对象。
	 * @author mdm
	 *
	 */
	@Test
	public void testBeanHandler() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			String sql = "select name,email from customers where id=? ";
			@SuppressWarnings("unchecked")
			Customer customer= (Customer) queryRunner.query(connection, sql, new BeanHandler(Customer.class),9);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * BeanListHanlder 把记过转化为一个list对象，list不为null 但可能是空集合 size()方法返回0
	 * 若sql语句能够查询到记录，List中存放的每条记录赋值给属性的对象。
	 * 给对象赋值的方式:是set和get方法所定义的那个名字
	 * 是调用set方法赋值的。所以如果数据库字段搜出来和属性对不上，你必须给数据库字段取个别名。
	 * 这样才能找到相应的set方法给相应的属性赋值。不然不行
	 * 数据库里面的字段如果和类里面的字段是一样的可以，忽略大小写，自动赋值
	 * 
	 * @author mdm
	 *
	 */
	@Test
	public void testBeanListHandler() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			
			String sql = "select id  ID_EN,name,email from customers where name=? ";
			
			List<Customer> customer= (List<Customer>) queryRunner.query(connection, sql, new BeanListHandler(Customer.class),"name_9");
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * MapHandler :返回sql对应的第一条记录对应的Map对象
	 * 键：sql查询的列名（不是列的别名），值：列的值 
	 * @author mdm
	 *
	 */
	@Test
	public void testMapHandler() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			String sql = "select name,email from customers where id=? ";
			@SuppressWarnings("unchecked")
			Map<String, Object> customer= (Map<String, Object>) queryRunner.query(connection, sql, new MapHandler(),9);
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}
	/**
	 * ListMapHandler :返回Map集合
	 * 一条记录对应的Map对象
	 * 键：sql查询的列名（不是列的别名），值：列的值 
	 * @author mdm
	 *
	 */
	@Test
	public void testListMapHandler() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			String sql = "select name,email from customers where name=? ";
			
			List<Map<String, Object>> customer= (List<Map<String, Object>>) queryRunner.query(connection, sql, new MapListHandler(),"name_9");
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}
	
	/**
	 * ScalarHanlder:把结果集转为一个数值返回，可以是任意数据类型。
	 * 可以搜很多行很多类，但是就返回第一行第一列
	 */
	@Test
	public void testScalarHandler() {
		Connection connection =null;
		try {
			connection=jdbcTools.getConnection();
			String sql = "select name ,email from customers where name=? ";
			
			String customer= (String) queryRunner.query(connection, sql, new ScalarHandler(),"name_9");
			System.out.println(customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(null, connection);
		}
	}

}
