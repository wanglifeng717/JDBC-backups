package testUser;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;

/*DAO的优化版本，是直接可以拿来用的*/
public class DAOyouhua {
/**/
	// insert，update，delete操作都可以包含其中
	public void  update(String sql,Object...args){
		Connection connection = null;
		PreparedStatement preparedStatement =null;
		try{
			connection = jdbcTools.getConnection();
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
			jdbcTools.release(preparedStatement, connection);
			
		}
	}
	//查询一条记录，返回对应的对象
	public <T> T get(Class<T> clazz,String sql,Object ...  args){
		List<T> result = getForList(clazz,sql,args);
		if(result.size()>0){
			return result.get(0);
		}
		return null;
	}
	//查询多条记录，返回对应的对象的集合
	/**重点优化的对象。
	 * 步骤：1.获取一个记录，把别名和值存在map集合中，然后把map放入list集合中
	 * 2.依次取出集合中的map然后赋值给对象，并把这些对象存在集合中。
	 * @param clazz
	 * @param sql
	 * @param args
	 * @return
	 */
	public <T> List<T> getForList(Class<T>clazz,String sql,Object...args){
		
		List<T> list = new ArrayList<T>();/*定义存最终被赋值的对象的集合*/
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
			/*通过resultset获取到了别名和值，放入map中，然后把每个map放入list集合中*/
			List<Map<String, Object>> values = handleResultSetToMapList(resultSet);
			/*把集合list中的map依次取出，然后赋值给相应的bean对象。最后把对象存入另外的list集合返回*/
			list=tansforMapListToBeanList(clazz, values);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(preparedStatement, connection,resultSet);
		}
		
		return list;
	}
	
	
	//返回某条记录的某一个字段的值或一个统一的值（一共多少条记录等）
		/**
		 * 读一个数据或者字段
		 * @param sql
		 * @param args
		 * @return
		 */
		public <E> E getForValue(String sql,Object...args){
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
		
		
		
	/**
	 * 把一个装满了map的list集合中的map取出来，把值赋给bean对象属性
	 * @param clazz
	 * @param values
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private <T> List<T> tansforMapListToBeanList(Class<T> clazz, List<Map<String, Object>> values) throws InstantiationException, IllegalAccessException, InvocationTargetException 
	{
		List<T> result = new ArrayList<T>();/*用来装bean对象*/
		T bean = null;
		if(values.size()>0)
		{
			for(Map<String,Object> m:values)//遍历list集合
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
				result.add(bean);
			}
		}
		return result;
	}
	/**
	 * 处理结果集，得到一个list 里面装了都是已经赋值的Map集合
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private List<Map<String, Object>> handleResultSetToMapList(ResultSet resultSet) throws SQLException {
		List<Map<String, Object>> values = new ArrayList<Map<String,Object>>();
		
		
		List<String> columnLabels=getColumnLabels(resultSet);
		Map<String,Object> map = null;
		//7.处理resultset使用while
		while (resultSet.next())
		{
			map =new HashMap<String,Object>();
		    for(String columnLabel:columnLabels)
			{
		    	Object value = resultSet.getObject(columnLabel);/*通过别名获取相应列的值*/
		    	map.put(columnLabel, value);
			}	
			//11.把填充好的Map对象放入准备的List中
			values.add(map);
		}
		return values;
	}
	/**
	 * 获取结果集columnLabel对应的List
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<String> getColumnLabels(ResultSet rs) throws SQLException
	{
		List<String> labels = new ArrayList<String>();
		ResultSetMetaData resultSetMetaData = rs.getMetaData();
		for(int i=0;i<resultSetMetaData.getColumnCount();i++)
		{
			labels.add(resultSetMetaData.getColumnLabel(i+1));/*把所有的列的别名全部放到一个集合中*/
		}
		return labels;
	}
	
}
