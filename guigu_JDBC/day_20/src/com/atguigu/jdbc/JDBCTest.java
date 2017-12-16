package com.atguigu.jdbc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.Test;

public class JDBCTest {

	/**
	 * 读取 blob 数据: 
	 * 1. 使用 getBlob 方法读取到 Blob 对象
	 * 2. 调用 Blob 的 getBinaryStream() 方法得到输入流。再使用 IO 操作即可. 
	 */
	@Test
	public void readBlob(){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "SELECT id, name customerName, email, birth, picture " 
					+ "FROM customers WHERE id = 13";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()){
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String email = resultSet.getString(3);
				
				System.out.println(id + ", " + name  + ", " + email);
				Blob picture = resultSet.getBlob(5);
				
				InputStream in = picture.getBinaryStream();
				System.out.println(in.available()); 
				
				OutputStream out = new FileOutputStream("flower.jpg");
				
				byte [] buffer = new byte[1024];
				int len = 0;
				while((len = in.read(buffer)) != -1){
					out.write(buffer, 0, len);
				}
				
				in.close();
				out.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCTools.releaseDB(resultSet, preparedStatement, connection);
		}
	}
	
	/**
	 * 插入 BLOB 类型的数据必须使用 PreparedStatement：因为 BLOB 类型
	 * 的数据时无法使用字符串拼写的。
	 * 
	 * 调用 setBlob(int index, InputStream inputStream)
	 */
	@Test
	public void testInsertBlob(){
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "INSERT INTO customers(name, email, birth, picture)" 
					+ "VALUES(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			
			preparedStatement.setString(1, "ABCDE");
			preparedStatement.setString(2, "abcde@atguigu.com");
			preparedStatement.setDate(3, 
					new Date(new java.util.Date().getTime()));
			
			InputStream inputStream = new FileInputStream("Hydrangeas.jpg");
			preparedStatement.setBlob(4, inputStream);
			
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCTools.releaseDB(null, preparedStatement, connection);
		}
	}
	
	/**
	 * 取得数据库自动生成的主键
	 */
	@Test
	public void testGetKeyValue() {
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = JDBCTools.getConnection();
			String sql = "INSERT INTO customers(name, email, birth)" +
					"VALUES(?,?,?)";
//			preparedStatement = connection.prepareStatement(sql);

			//使用重载的 prepareStatement(sql, flag) 
			//来生成 PreparedStatement 对象
			preparedStatement = connection.prepareStatement(sql, 
					Statement.RETURN_GENERATED_KEYS);
			
			preparedStatement.setString(1, "ABCDE");
			preparedStatement.setString(2, "abcde@atguigu.com");
			preparedStatement.setDate(3, 
					new Date(new java.util.Date().getTime()));
			
			preparedStatement.executeUpdate();
			
			//通过 getGeneratedKeys() 获取包含了新生成的主键的 ResultSet 对象
			//在 ResultSet 中只有一列 GENERATED_KEY, 用于存放新生成的主键值.
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if(rs.next()){
				System.out.println(rs.getObject(1));
			}
			
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 0; i < rsmd.getColumnCount(); i++){
				System.out.println(rsmd.getColumnName(i + 1)); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCTools.releaseDB(null, preparedStatement, connection);
		}
		
	}

}
