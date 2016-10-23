/*
 * This test class currently doesn't work
 * I am trying to use Java Reflection to get around
 * the package issue
 */


package project1.test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessTest {
	
	private static Class<?> c;
	private static Constructor<?> cons;
	private static Object process;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		c = Class.forName("Process.java");
		cons = c.getConstructor(String.class, Integer.class, Integer.class, Integer.class, Integer.class);
		Object process = cons.newInstance("A", 0, 168, 5, 287);
		
	}
	
	public Method getMethod(String methodName) {
		
		Method method = null;
		
		try {
			  method = c.getClass().getMethod(methodName);
			  } 
        catch (SecurityException e) { }	  
		
		catch (NoSuchMethodException e) { }
		
		return method;
	}
	
	

	@Test
	public void testGetProcessID() {

		Method method = getMethod("getProcessID");
		Object pid = null;

		try {
			pid = method.invoke(c);
		} 
		catch (IllegalArgumentException e) { }
		catch (IllegalAccessException e) { }
		catch (InvocationTargetException e) { }

		assertEquals("A", pid.toString());
	}
	
	@Test
	public void testToString() {
		assertEquals("A|0|168|5|287", process.toString());
	}

}
