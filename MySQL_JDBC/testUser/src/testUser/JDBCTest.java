package testUser;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;



public class JDBCTest {
	
	
	
	@Test
	/*测试元数据的使用方法。
	 * 通过反射得到一个学生的类对象
	 * */
	public void testResultSetMetaData()
	{
		Connection connection = null;
		PreparedStatement preparedstatement = null;
		ResultSet resultSet = null;

		try {
			String sql="select flowid flowId,type_en type,idcard idCard,examcard examCard,studentname studentName,"+
				       "location ,grade from examstudent where flowid=?";
			connection = jdbcTools.getConnection();
			preparedstatement= connection.prepareStatement(sql);
			preparedstatement.setInt(1, 1);//设置占位符的值，占位符从1开始
			resultSet = preparedstatement.executeQuery();
			
			Map<String,Object> values = new HashMap<String,Object>();
			//1.得到ResultSetMetaData对象
			ResultSetMetaData rsmd = resultSet.getMetaData();
			while(resultSet.next())
			{
				//2.打印每一列的列名。	
				for(int i=0;i<rsmd.getColumnCount();i++)//获取有几列
				{
					String columnLabel=rsmd.getColumnLabel(i+1);//获取标签，也就是别名的名称
					Object columnValue = resultSet.getObject(columnLabel);//获取列的值
					values.put(columnLabel,columnValue);//把标签后值分别放入map集合中
				}
			}
			//System.out.println(values);
			Class clazz = Student.class;
			Object object = clazz.newInstance();//建立类对象
			for(Map.Entry<String,Object> entry:values.entrySet())
			{//遍历map把值和赋值给相应的属性。
				String fieldName = entry.getKey();
				Object fieldValue=entry.getValue();
				//System.out.println(fieldName+":"+fieldValue);
				ReflectionUtils.setFieldValue(object, fieldName, fieldValue);//反射工具包里面的内容，现在存疑，还不是很懂。
			}
			System.out.println(object);
			}

		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			jdbcTools.release(preparedstatement, connection,resultSet);
		}

	}
	//测试我们的通用查询方法能不能获取两个类对象。我们用了两个类做测试。
	@Test
	public  void testGet()
	{
		//可以为列取一个别名
		String sql="select flowid flowId,type_en type,idcard idCard,examcard examCard,studentname studentName,"+
	       "location ,grade from examstudent where flowid=?";
		Student student = get(Student.class,sql,1);
		System.out.println(student);
		
		sql="select id ,name,email,birth "+
		"from customers where id =?";
		Customer customer = get(Customer.class,sql,1);
		System.out.println(customer);
	}
	
	
//通用的查询方法。通过查询，我们把查询到的属性赋值个相应的类对象。。	
	public <T> T get(Class<T> clazz,String sql,Object ...args)
	{
		T entity = null;
		
		Connection connection = null;
		PreparedStatement preparedstatement = null;
		ResultSet resultSet = null;

		try {
			//1.得到resultSet对象
			connection = jdbcTools.getConnection();
			preparedstatement= connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedstatement.setObject(i+1,args[i]);
			}
			
			resultSet = preparedstatement.executeQuery();
			//2.得到resultMetaData对象
			ResultSetMetaData rsmd = resultSet.getMetaData();
			//3.创建一个map<String,Object>对象;键：sql查询的类的别名，值：列的值
			Map<String,Object> values= new HashMap<String,Object>();
			//4.处理结果集，利用resultMetaData填充map对象
			if(resultSet.next())
			{
				for(int i=0;i<rsmd.getColumnCount();i++){
					String columnLabel = rsmd.getColumnLabel(i+1);
					Object columnValue=resultSet.getObject(i+1);
					values.put(columnLabel, columnValue);
				}
			}
			//5.利用反射创建clazz对应的对象
			if(values.size()>0){
				entity = clazz.newInstance();
				//6.遍历map，利用反射为class对象的对应的属性赋值。
				for(Map.Entry<String, Object> entry:values.entrySet()){
					String fieldName = entry.getKey();
					Object value=entry.getValue();
					ReflectionUtils.setFieldValue(entity, fieldName, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jdbcTools.release(preparedstatement, connection,resultSet);
		}

		return entity;
	}
	
	
	
	
	
//原理演示，我们为什么要写一个可以通用的方法，因为获取不同对象有其通用的地方。我们写getcustomer也是极其的类似，所以我们想能不能统一成一个
	public Student getStudent(String sql,Object ...args)
	{
		Student stu = null;//区别：Customer customer = null;

		Connection connection = null;
		PreparedStatement preparedstatement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcTools.getConnection();
			preparedstatement= connection.prepareStatement(sql);
			for(int i=0;i<args.length;i++)
			{
				preparedstatement.setObject(i+1,args[i]);
			}
			
			resultSet = preparedstatement.executeQuery();
			//把查询到的信息，依次赋值给学生的属性。赋值是一次拿到一行记录，可以按照数字拿属性，也可以输入列的名称拿属性
			if (resultSet.next()) {
				stu = new Student();
				stu.setFlowId(resultSet.getInt(1));
				stu.setType(resultSet.getInt(2));
				stu.setIdCard(resultSet.getString(6));
				stu.setExamCard(resultSet.getString(3));
				stu.setStudentName(resultSet.getString(4));
				stu.setLocation(resultSet.getString(5));
				stu.setGrade(resultSet.getInt(7));
				/*区别：
				 * customer = new Customer();
				 * customer.setId(resultSet.getInt(1));
				 * customer.setName(resultSet.getString(1));
				 * customer.setEmial(resultSet.getString(3));
				 * customer.setBirth(resultSet.getDate(4));
				 * */
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jdbcTools.release(preparedstatement, connection,resultSet);
		}

		return stu;
	}
	
	
	
	
	
	
	/*
	 * SQL注入 。因为你的sql语句是拼凑起来的。所以我完全可以拼凑语句来找你的逻辑漏洞
	 * 但是对于preparedstatement 这种方式是无效的。因为就是拿你的输入去库里面去匹配。
	 * */
	@Test
	public void testSQLInjection()
	{
		String deptno="9 or dname='";//string deptno="1"这里什么类型都可以，取决于下面sql语句是否有‘’号,以最终的显示的SQL语句为准
		String dname="or'1'='1";
		String sql = "select * from dept where deptno="+ deptno +" AND dname='"+dname+"'";
		System.out.println(sql);
		//select * from dept where deptno=1 OR  dname=' AND dname=' OR  '1'='1' 
		Connection connection =null;
		Statement statement = null;
		ResultSet resultSet =null;
		try
		{
			connection = jdbcTools.getConnection();//建立连接
			statement = connection.createStatement();//获取statement对象
			resultSet = statement.executeQuery(sql);//执行查询语句，获取resultset对象
			if(resultSet.next())//如果能有下一行，说明搜索到了结果
				System.out.println("success");
			else
				System.out.println("fail");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			jdbcTools.release(statement, connection,resultSet);//这个方法是重载的，可以关闭两个，也可以关闭三个。
		}
	}
	
	
	//用preparedstatement来简化sql语句的书写。
	//写这种预编译的sql语句，就是带占位符的语句
	//好处是防止sql注入
	//1.建立连接 2.写预编译sql语句3.建立preparestatement对象4.传值到sql中5.执行sql语句
   @Test
   public void testPreparedStatement()
   {
	   Connection connection=null;
	   PreparedStatement preparedStatement = null;//代替原来的statement对象
	   try
	   {
		   connection =jdbcTools.getConnection();
		   String sql="insert into examstudent"+ " values(?,?,?,?,?,?,?)";
		   preparedStatement = connection.prepareStatement(sql);
		   preparedStatement.setInt(1, 5);//这个占位符的填充是从1开始的。
		   preparedStatement.setInt(2, 4);
		   preparedStatement.setString(3, "qq");
		   preparedStatement.setString(4, "qq");
		   preparedStatement.setString(5, "qq");
		   preparedStatement.setString(6, "qq");
		   preparedStatement.setInt(7, 1);
		   //preparedStatement.setDate(9, new Date(new java.util.Date().getTime()));
		   //数据库时间的插入方式。
		   preparedStatement.executeUpdate();
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   finally
	   {
		   jdbcTools.release(preparedStatement, connection);
	   }
   }
   

  
   //测试按照身份证或者准考证信息去数据库里面查，是否能查到此人，并把结果打印出来
	public void testGetStudent()
	{
		//1.得到查询类型1代表按学号查询，2代表按身份证查询
		int searchType = getSearchTypeFromConsole();
		//2.具体查询学生信息，查询数据库，把查询的结果赋值给对象的各个属性
		Student student = searchStudent(searchType);
		
		//3.打印学生信息
		printStudent(student);
		
	}
	
	/**
	 * 打印学生信息：学生存在，打印具体信息，如果不存在显示查无此人
	 * @param student
	 */
	private void printStudent(Student student) {
		if (student != null) {
			System.out.println(student);
		} else {
			System.out.println("查无此人!");
		}
		
	}

	/**
	 * 具体查询学生信息的方法，返回一个student对象，不存在返回null
	 * @param searchType ：1 或 2
	 * @return
	 */
	private Student searchStudent(int searchType) {
		String sql = "SELECT flowid, type_en, idcard, examcard,"
				+ "studentname, location, grade " + "FROM examstudent "
				+ "WHERE ";
		Scanner scanner = new Scanner(System.in);
		//1.根据输入的searchType，提示用户输入信息
		//1.1如果是1，提示输入身份证号，如果是2，提示输入准考证号
		//2.根据searchType 确定SQL
		if (searchType == 1) {
			System.out.print("请输入准考证号:");
		
			String examCard = scanner.next();
			
			sql = sql + "examcard = '" + examCard + "'";
		} else {
			System.out.print("请输入身份证号:");
			String examCard = scanner.next();
			sql = sql + "idcard = '" + examCard + "'";//根据输入的值去拼这个sql语句
		}
		//3.执行查询
		Student student = getStudent(sql);
		//4.若存在查询结果，把查询结构封装成一个student对象
		scanner.close();
		return student;
	}

	/**
	 * 根据传入的SQL返回Student对象
	 * @param sql
	 * @return
	 */
	private Student getStudent(String sql) {
		 
		Student stu = null;

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = jdbcTools.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);
			//把查询到的信息，依次赋值给学生的属性。赋值是一次拿到一行记录，可以按照数字拿属性，也可以输入列的名称拿属性
			if (resultSet.next()) {
				stu = new Student(resultSet.getInt(1), resultSet.getInt(2),
						resultSet.getString(3), resultSet.getString(4),
						resultSet.getString(5), resultSet.getString(6),
						resultSet.getInt(7));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jdbcTools.release(statement, connection,resultSet);
		}

		return stu;
	}

	/**
	 * 从控制台读入一个整数，确定查询的类型
	 * @return 1.用身份证查询，2.用准考证查询 其他无效，提示请用户重新输入
	 */
	private int getSearchTypeFromConsole() {
	    System.out.print("请输入查询类型");
	    Scanner scanner = new Scanner(System.in);
	    int type = scanner.nextInt();
	    if(type!=1 && type!=2){
	    	System.out.println("输入有误重新输入");
	    	scanner.close();
	    	throw new RuntimeException();
	    }
	    //scanner.close();这里不能有。因为
		return type;
	}

	/**
	 * 测试加入学生是否成功
	 */
	@Test
	public void testAddNewStudent()
	{
		Student student=getStudentFromConsole();//从控制台把属性赋值给学生的各个属性
		addNewStudent2(student);//新加一个学生到数据库中
		
	}
	/**
	 * 从控制台接收学生对象
	 * @return
	 */
	private Student getStudentFromConsole() {
		Scanner scanner = new Scanner(System.in);

	    Student student = new Student();

		System.out.print("FlowId:");
		student.setFlowId(scanner.nextInt());

		System.out.print("Type: ");
		student.setType(scanner.nextInt());

		System.out.print("IdCard:");
		student.setIdCard(scanner.next());

		System.out.print("ExamCard:");
		student.setExamCard(scanner.next());

		System.out.print("StudentName:");
		student.setStudentName(scanner.next());

		System.out.print("Location:");
		student.setLocation(scanner.next());

		System.out.print("Grade:");
		student.setGrade(scanner.nextInt());
		scanner.close();
		return student;
	}
	
	/**
	 * 插入一条学生的记录
	 * @param student
	 */
	public void addNewStudent(Student student)
	{
//		准备一条插入SQL语句
		String sql = "INSERT INTO examstudent VALUES(" + student.getFlowId()
		+ "," + student.getType() + ",'" + student.getIdCard() + "','"
		+ student.getExamCard() + "','" + student.getStudentName()
		+ "','" + student.getLocation() + "'," + student.getGrade()
		+ ")";
//		执行插入操作：内部包含了建立连接，执行statement，关闭等操作
		jdbcTools.update(sql);
	}
	public void addNewStudent2(Student student)
	   {
		//这里用的是preparedstatement对象代替statement。用的是预编译的sql语句
		   String sql ="insert into examstudent(flowid,type_en,idcard,examcard,studentname,location,grade) "
		   		+ "values(?,?,?,?,?,?,?)";
		   //这个方法是重载的的。可以输入预编译的sql和相关的属性。
		   jdbcTools.update(sql,student.getFlowId(),student.getType(),student.getIdCard(),
				   student.getExamCard(), student.getStudentName(),student.getLocation(),student.getGrade());
	   }

 
}
