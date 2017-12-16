package insertDigdata;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.junit.Test;

//import com.mysql.jdbc.PreparedStatement;

public class JDBCInsertDigData {

	/**
	 * 向数据库customer数据表插入10万条记录
	 * 测试如何插入，用时最短
	 * 1.使用Statement
	 */
	@Test
	public void testBatchWithStatement() {
		Connection connection=null;
		Statement statement=null;
		try {
			connection = jdbcTools.getConnection();
			//System.out.println(connection);
			jdbcTools.beginTx(connection);
			long begin = System.currentTimeMillis();
			statement = connection.createStatement();
			for(int i=0;i<50000;i++)
			{
				String sql="insert into customers (name,birth) values("
						+"'name_"+i+"','2009-1-1')";
				statement.executeUpdate(sql);
			}
			long end = System.currentTimeMillis();
			System.out.println("time:"+(end-begin));
			jdbcTools.commit(connection);
			//数据库里查看记录多少条： select count(id)from customer;
			//time:2238
		} catch (Exception e) {
			e.printStackTrace();
			jdbcTools.rollback(connection);
		}
		finally {
			
			jdbcTools.release(statement, connection);
		}
	}
	/*
	 * 使用preparedstatement看看效率是否提高了些*/
	@Test
	public void testBatchWithPreparedStatement() {
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		try {
			connection = jdbcTools.getConnection();
			String sql="insert into customers (name,birth) values(?,?)";
			preparedStatement = connection.prepareStatement(sql);
			
			jdbcTools.beginTx(connection);
			long begin = System.currentTimeMillis();
			for(int i=0;i<50000;i++)
			{  
				preparedStatement.setString(1, "name_"+i);
				preparedStatement.setString(2, "2009-1-1");
				preparedStatement.executeUpdate();
			}
			long end = System.currentTimeMillis();
			System.out.println("time:"+(end-begin));
			jdbcTools.commit(connection);
			//数据库里查看记录多少条： select count(id)from customers;
			//清空数据表： truncate table customers;
			//time:2236
		} catch (Exception e) {
			e.printStackTrace();
			jdbcTools.rollback(connection);
		}
		finally {
			
			jdbcTools.release(preparedStatement, connection);
		}	
	}
	
	/*
	 * 现在是一条一条的执行，我们现在等到积累一定的SQL之后一起执行
	 * addBatch(String)添加需要批量处理的SQL
	 * executeBatch()执行批量处理语句*/
	@Test
	public void testBatchWithPreparedStatement2() {
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		try {
			connection = jdbcTools.getConnection();
			String sql="insert into customers (name,birth) values(?,?)";
			preparedStatement = connection.prepareStatement(sql);
			
			jdbcTools.beginTx(connection);
			long begin = System.currentTimeMillis();
			for(int i=0;i<50000;i++)
			{  
				preparedStatement.setString(1, "name_"+i);
				preparedStatement.setString(2, "2009-1-1");
				//积累SQL
				preparedStatement.addBatch();
				//积累一定数量统一执行，并清空之前的积累
				if((i+1)%300==0)
				{
					preparedStatement.executeBatch();
					preparedStatement.clearBatch();
				}	
			}
			//如果不是批量值得整数倍，则还需要在额外的执行一次
			if(50000%300!=0){
				preparedStatement.executeBatch();
				preparedStatement.clearBatch();
			}
			
			long end = System.currentTimeMillis();
			System.out.println("time:"+(end-begin));
			jdbcTools.commit(connection);
			//数据库里查看记录多少条： select count(id)from customers;
			//清空数据表： truncate table customers;
			//time:2214
		} catch (Exception e) {
			e.printStackTrace();
			jdbcTools.rollback(connection);
		}
		finally {
			
			jdbcTools.release(preparedStatement, connection);
		}	
	}
	

}
