/*
 * This test class currently doesn't work
 * I am trying to use Java Reflection to get around
 * the package issue
 */

package project1.test;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

//import Statistics;

public class StatisticsTest {
	
//	private static Statistics a;
//	private static Statistics b;
//	private static Statistics c;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		a = new Statistics("FCFS", 122.22, 10.23, 14.30, 10, 5);
//	    b = new Statistics("SJF", 12.5, 100.12, 5.33, 12, 13);
//		c = new Statistics("RR", 120.20, 11.5, 6.22, 8, 6);
	}
	
	@Test
	public void testOneSet() {
		String expected = 
		"Algorithm FCFS\n" +
		"-- average CPU burst time: 122.22 ms\n" +
		"-- average wait time: 10.23 ms\n" +
		"-- average turnaround time: 14.30 ms\n" +
		"-- total number of context switches: 10\n" +
		"-- total number of preemptions: 5\n";		
//		assertEquals(expected, a.toString());
	}
	
	public void testAllThree(){
//		String actual = a.toString() + b.toString() + c.toString();
		String expected = 
				"Algorithm FCFS\n" +
						"-- average CPU burst time: 122.22 ms\n" +
						"-- average wait time: 10.23 ms\n" +
						"-- average turnaround time: 14.30 ms\n" +
						"-- total number of context switches: 10\n" +
						"-- total number of preemptions: 5\n" +
				"Algorithm SJF\n" +
						"-- average CPU burst time: 12.50 ms\n" +
						"-- average wait time: 100.12 ms\n" +
						"-- average turnaround time: 5.33 ms\n" +
						"-- total number of context switches: 12\n" +
						"-- total number of preemptions: 13\n"+
				"Algorithm RR\n" +
						"-- average CPU burst time: 120.20 ms\n" +
						"-- average wait time: 11.50 ms\n" +
						"-- average turnaround time: 6.22 ms\n" +
						"-- total number of context switches: 8\n" +
						"-- total number of preemptions: 6\n";
//		assertEquals(expected, actual);
	}

}
