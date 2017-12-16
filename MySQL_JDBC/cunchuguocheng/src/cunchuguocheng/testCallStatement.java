package cunchuguocheng;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import org.junit.Test;

//重點中的重點

public class testCallStatement {

	/**
	 * 如何使用 JDBC 调用存储在数据库中的函数或存储过程
	 */
	@Test
	public void testCallableStatement(){
		Connection connection = null;
		CallableStatement callableStatement=null;
	
		try {
			/*1.通過connection对象的prepareCall（）方法创建一个callableStatement对象的实例
			 * 在使用connection对象的preparedCall（）方法时，需要传入一个String类型的字符串，
			 * 该字符串用于指明如何调用存储过程*/
			//{?= call <procedure-name>[(<arg1>,<arg2>, ...)]}
			  // {call <procedure-name>[(<arg1>,<arg2>, ...)]}

			connection=jdbcTools.getConnection();
			System.out.println(connection);
			String sql="{?=call subString(?,?,?)}";
			//String sql ="{call proc1(?)}";
			//callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement =  connection.prepareCall(sql);
			/*2.通过callablestatement对象的reisterOutParamter()方法注册OUt参数*/
			callableStatement.registerOutParameter(1, Types.DATE);
			//註冊有時候能省
			callableStatement.setString(2, "abdcd");
			callableStatement.setInt(3, 1);
			callableStatement.setInt(4, 3);
			
			// 3. 通过 CallableStatement 对象的 setXxx() 方法设定 IN 或 IN OUT 参数. 若想将参数默认值设为
		    // null, 可以使用 setNull() 方法.
			//callableStatement.setInt(2, 80);
						
			// 4. 通过 CallableStatement 对象的 execute() 方法执行存储过程
			callableStatement.execute();
						
			// 5. 如果所调用的是带返回参数的存储过程, 
			//还需要通过 CallableStatement 对象的 getXxx() 方法获取其返回值.
			String sum = callableStatement.getString(1);
			//long empCount = callableStatement.getLong(3);
			System.out.println(sum);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			jdbcTools.release(null, connection);
		}
	}
}
