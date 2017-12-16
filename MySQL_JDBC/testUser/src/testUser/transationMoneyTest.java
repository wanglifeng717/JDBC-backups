package testUser;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;



public class transationMoneyTest {

	
	/**测试事务的隔离级别
	 * 在jdbc程序中可以通过connection的setTransationIsolation
	 * 来设置事务的隔离级别
	 * @author mdm
	 *
	 */
	@Test 
	public void testTransactionIsolationUpdate(){
		Connection connection =null;
		
		try {
			connection = jdbcTools.getConnection();
			connection.setAutoCommit(false);
			String sql ="update bank set balance=balance-500 where id=1";
			update(connection, sql);
			System.out.println("1");
			connection .commit();//设置断点，然后让其他程序去读这个值，选择junit debug as 让程序停在那里
			//然后运行testTransactionIsolationRead去读数据，看你的隔离级别不同生效不同。
			System.out.println("2");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			jdbcTools.release(null, connection);
		}
	}
	//由于不同的隔离权限去读数据库，看是否生效
	@Test
	public void testTransactionIsolationRead(){
		String sql ="select balance from bank where id=1";
	    Integer balance = getForValue(sql);
	    System.out.println(balance);
	}
	//返回某条记录的某一个字段的值或一个统一的值（一共多少条记录等）
	public <E> E getForValue(String sql,Object...args){
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try {
			connection=jdbcTools.getConnection();
			//这个级别很重要，如果是前者，你还没提交，其他程序就拿走了你更新的数据，你可能还回滚呢。
			//System.out.println(connection.getTransactionIsolation());//获取当前事务隔离级别
			//也可以直接在数据库中设置全局的事务隔离级别，不用再这里用代码去写。
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);//读未提交的数据
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			preparedStatement=connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedStatement.setObject(i+1, args[i]);
			}
			resultSet=preparedStatement.executeQuery();
			if(resultSet.next())
			{
				return (E)resultSet.getObject(1);/*获取一条记录第一列的值，如果知道具体类型和列名也可以用getString("dname")*/
			}/*这种写法就导致，在写sql语句的时候，想要搜索的类必须是第一个。*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection,resultSet);
		}
		return null;

	}
	
	/*tom给jerry汇款500元
	 * 关于事务
	 * 1.如果多个操作，每个操作使用的是自己的单独的连接，则事务无法保证
	 * DAOyouhua  dao= new DAOyouhua();
		String sql="update bank set balance=balance-500 where id=1";
		dao.update(sql);
		//我们过程中发生了一个异常，我们如何回滚到最原始的状态
		int i = 10/0;
		System.out.println(i);
		
		sql="update bank set balance=balance+500 where id=2";
		dao.update(sql);
		
	 * 具体步骤：
	 * 1.开始事务，取消默认的默认提交行为connection.setAutoCommit(false);
	 * 2.如果操作成功提交事务connection.commit();
	 * 3.如果出现异常回滚事务connection.rollback();
	 * 
	 * 
	 * */
	@Test
	public void testTransaction() {
		Connection connection=null;
		
		try {
			connection = jdbcTools.getConnection();
			//开始事务，取消默认提交
			connection.setAutoCommit(false);
			
			String sql="update bank set balance=balance-500 where id=1";
			update(connection, sql);
			
			int i = 10/0;
//			System.out.println(i);
			
			sql="update bank set balance=balance+500 where id=2";
			update(connection, sql);
			//提交事务
			connection.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			//回滚事务
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		finally{
			jdbcTools .release(null, connection);
		}
			
//		DAOyouhua  dao= new DAOyouhua();
//		String sql="update bank set balance=balance-500 where id=1";
//		dao.update(sql);
//		//我们过程中发生了一个异常，我们如何回滚到最原始的状态
//		int i = 10/0;
//		System.out.println(i);
//		
//		sql="update bank set balance=balance+500 where id=2";
//		dao.update(sql);
	}	
	//写个优化版本的update
	public void  update(Connection connection,String sql,Object...args)
	{
		PreparedStatement preparedStatement =null;
		try{
			preparedStatement = connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedStatement.setObject(i+1,args[i] );/*遍历可变参数，然后把占位符赋值，占位符标号从1开始*/
			}
			preparedStatement.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, null);
			
		}
	}

}
