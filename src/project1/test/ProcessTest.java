package project1.test;

import project1.Process;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ProcessTest {
	
	private static Process process;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		process = new Process("A", 0, 168, 5, 287);
	}

	@Test
	public void testGetProcessID() {
		assertEquals("A", process.getProcessID());
	}
	
	@Test
	public void testToString() {
		assertEquals("A|0|168|5|287", process.toString());
	}

}
