package testUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

public class DAO {

	// insert，update，delete操作都可以包含其中
	public void  update(String sql,Object...args){
		Connection connection = null;
		PreparedStatement preparedStatement =null;
		try{
			connection = jdbcTools.getConnection();
			preparedStatement = connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedStatement.setObject(i+1,args[i] );
			}
			preparedStatement.executeUpdate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection);
			
		}
	}
	//查询一条记录，返回对应的对象
	public <T> T get(Class<T> clazz,String sql,Object ...  args){
		T entity =null;
		Connection connection=null;
		PreparedStatement preparedstatement=null;
		ResultSet resultset =null;
		try{
			//1.获取connection对象
			connection = jdbcTools.getConnection();
			//2.获取prepare的statement对象
			preparedstatement=connection.prepareStatement(sql);
			//3.填充占位符
			for(int i=0;i<args.length;i++)
			{
				preparedstatement.setObject(i+1, args[i]);
			}
			//4.进行查询，得到resultSet
			resultset=preparedstatement.executeQuery();
			//5.若resultset中有记录，
			if(resultset.next()){
				//准备一个map<String,Object>:键:存放列的别名，值：存放列的值
				Map<String,Object>values=new HashMap<String,Object>();
				//6.得到resultSetMetaData对象
				ResultSetMetaData rsmd = resultset.getMetaData();
				//7.处理resultset把指针向下移动一个单位
				//8.由resultsetmetadata对象得到结果中有多少列
				int columnCount = rsmd.getColumnCount();
				//9.由resultsetmetadata得到别名和具体列的值
				for(int i=0;i<columnCount;i++){
					String columnLabel =rsmd.getColumnLabel(i+1);
					Object columnValue=resultset.getObject(i+1);
					//10.填充map对象
					values.put(columnLabel,columnValue);
				}
				//11.用反射创建class对应的对象。
				entity = clazz.newInstance();
				//12.遍历map对象，用反射填充对象的属性值，属性名为map中的key，
				for(Map.Entry<String,Object> entry:values.entrySet())
				{
					String propertyName = entry.getKey();
					Object value = entry.getValue();
					//ReflectionUtils.setFieldValue(entity, propertyName, value);
					//用这种反射不是最正统的方式，用这个才是，因为属性是要调set方法设置的。
					BeanUtils.setProperty(entity, propertyName, value);
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally{
			jdbcTools.release(preparedstatement, connection,resultset);
		}

		return entity;
	}
	//查询多条记录，返回对应的对象的集合
	public <T> List<T> getForList(Class<T>clazz,String sql,Object...args){
		
		List<T> list = new ArrayList<T>();
		Connection connection=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try {
			connection=jdbcTools.getConnection();
			preparedStatement=connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedStatement.setObject(i+1, args[i]);
			}
			resultSet=preparedStatement.executeQuery();
			//获取一组对象，不同点就是
			
			//5.准备一个List<Map<String,Object>>键存放别名，值：存放列的值。其中map对象对应着一条记录
			List<Map<String, Object>> values = new ArrayList<Map<String,Object>>();
			
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			Map<String,Object> map = null;
			//7.处理resultset使用while
			while (resultSet.next())
			{
				map =new HashMap<String,Object>();
				for(int i=0;i<resultSetMetaData.getColumnCount();i++)
				{
					String columnLabel = resultSetMetaData.getColumnLabel(i+1);
					Object value = resultSet.getObject(i+1);
					map.put(columnLabel, value);
				}
				//11.把填充好的Map对象放入5准备的List中
				values.add(map);
			}
			//12.判断List是否为空集合，若不为空，则遍历List 得到一个个Map对象，在把一个Map对象转为一个class
			T bean = null;
			if(values.size()>0)
			{
				for(Map<String,Object> m:values)//遍历集合
				{
					bean = clazz.newInstance();
					for(Map.Entry<String, Object> entry:m.entrySet())
					{
						String propertyName = entry.getKey();
						Object value = entry.getValue();
						//参数对应的Object对象。
						BeanUtils.setProperty(bean, propertyName, value);
						
					}
					//13.把Object对象放入到List中。
					list.add(bean);
				}
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection,resultSet);
		}
		
		return list;
	}
	//返回某条记录的某一个字段的值或一个统一的值（一共多少条记录等）
	public <E> E getForValue(String sql,Object...argss){
		return null;
	}
}
