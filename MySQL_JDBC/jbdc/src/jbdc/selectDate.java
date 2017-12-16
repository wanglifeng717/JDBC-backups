package jbdc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class selectDate {

	public static void main(String[] args) {
		Connection conn = null;
		Statement statement=null;
		ResultSet rs=null;
		try{
//			1.获取数据库连接
			conn= jdbcTools.getConnection();
			String sql =null; 
//			2.准备SQL语句
			sql="select * from dept ";
//			3.获取statement对象
			statement = conn.createStatement();
//			4.执行查询，得到ResultSet
			rs=statement.executeQuery(sql);
//			5.处理ResultSet
			while(rs.next())
			{
				int id = rs.getInt(1);
				String name = rs.getString("dname");
				String loc = rs.getString(3);
				System.out.println(id+":"+name+":"+loc);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			jdbcTools.release(statement,conn,rs);
		}

	}

}
