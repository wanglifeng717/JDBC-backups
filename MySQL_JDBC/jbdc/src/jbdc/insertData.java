package jbdc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class insertData {
  /*1.获取数据库连接
	2.准备插入的SQL语句	
	3.执行插入
		1)获取操作ＳＱＬ语句的ｓｔａｔｅｍｅｎｔ对象
			调用connection的createStatement（）方法获取
		２）调用ｓｔａｔｅｍｅｎｔ对象的ｅｘｅｃｕｔｅＵｐｄａｔｅ（ｓｑｌ）执行语句
			executeUpdate可以执行insert,update,delete 但是不能是select;
		３）关闭ｓｔａｔｅｍｅｎｔ对象
			都是连接资源必须要关闭。出异常也需要关闭，异常可以不处理，但是连接必须要关闭。 
	4.关闭连接:
			关闭资源的时候，正常我们是先关里面在关外面，有个层次的关系。
*/
	public static void main(String[] args)
	{
		Connection conn = null;
		Statement statement=null;
		try{
			conn= jdbcTools.getConnection();
			
			String sql =null; 
//			完成添加，删除，修改工作
			sql="insert into dept (deptno,dname,loc) VALUES(3,'cc','df')";
			//sql = "delete from dept where deptno= 1";
			//sql="update dept set dname='zhongguo' where deptno=2";
			
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
	
}
