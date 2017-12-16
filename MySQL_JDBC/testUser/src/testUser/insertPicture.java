package testUser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.Test;

import com.mysql.jdbc.Blob;

public class insertPicture {

	/**
	 * 把已经插入的图片读出来
	 * 读取blob数据
	 * 1.使用getblob方法读取到Blob对象
	 * 2.调用blob的getBinaryStream（）方法得到输入流，在使用IO操作。
	 */
	@Test
	public void testRead(){
		
		Connection connection = null;
		PreparedStatement preparedStatement =null;
		ResultSet resultSet = null;
		try{
			connection = jdbcTools.getConnection();
			String sql="select id,name,email,pic from customers where id=2";
			preparedStatement = connection.prepareStatement(sql);
			resultSet=preparedStatement.executeQuery();
			if(resultSet.next())
			{
				int id = resultSet.getInt(1);
				String name = resultSet.getString(2);
				String email = resultSet.getString(3);
				
				System.out.println(id+":"+name+":"+email);
				Blob pic=(Blob) resultSet.getBlob(4);
				InputStream  inputStream = pic.getBinaryStream();
				OutputStream outputStream = new FileOutputStream("haha.jpg");
				byte[] buf = new byte[1024];
				int len=0;
				while((len=inputStream.read(buf))!=-1)
				{
					outputStream.write(buf, 0, len);
				}
				outputStream.close();
				inputStream.close();
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection,resultSet);
			
		}
	}
	/**
	 * 插入图片，blob
	 * 插入BLOB类型的数据必须使用preparedStatement.
	 * 因为数据无法使用字符串拼写
	 */
	@Test
	public void testInsertPicture() {
		
		Connection connection = null;
		PreparedStatement preparedStatement =null;
		try{
			connection = jdbcTools.getConnection();
			String sql ="insert into customers(name,email,birth,pic) values(?,?,?,?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, "wanglifeng");
			preparedStatement.setString(2, "11@qq.com");
			preparedStatement.setDate(3, new Date(new java.util.Date().getTime()));
			InputStream inputStream=new FileInputStream("8RQLFKF0B61J.jpg");
			preparedStatement.setBlob(4, inputStream);
			
			preparedStatement.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection);
			
		}
	}

}
