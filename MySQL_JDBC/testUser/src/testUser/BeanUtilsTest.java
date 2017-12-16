package testUser;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

public class BeanUtilsTest {

	@Test
	public void testGetProperty() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		Object object = new Student();

	    BeanUtils.setProperty(object, "idCard", "211");
		//System.out.println(object);
	    Object s= BeanUtils.getProperty(object, "idCard");
		System.out.println(s);
}
	@Test
	public void testSetProperty() throws IllegalAccessException, InvocationTargetException {
		
			Object object = new Student();

		    BeanUtils.setProperty(object, "idCard", "211");
			System.out.println(object);
	}
}
