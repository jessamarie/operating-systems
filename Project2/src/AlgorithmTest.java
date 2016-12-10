
import java.io.*;

import static org.junit.Assert.*;
import org.junit.Test;

public class AlgorithmTest {

	/**
	 * @param file1 
	 * @param file2
	 * @return true if file1 and file2 have the same content, false otherwise
	 * @throws IOException
	 */	

	/* compares two text files, line by line */
	private static boolean compare(String file1, String file2) throws IOException {
		BufferedReader is1 = new BufferedReader(new FileReader(file1)); // Decorator design pattern!
		BufferedReader is2 = new BufferedReader(new FileReader(file2));
		String line1, line2;
		boolean result = true;
		while ((line1=is1.readLine()) != null) {

			line2 = is2.readLine();
			if (line2 == null) {
				System.out.println(file1+" longer than "+file2);
				result = false;
				break;
			}
			if (!line1.equals(line2)) {
				System.out.println("Lines:\n"+line1+", and\n"+line2+"\ndiffer\n");
				result = false;
				break;
			}
		}
		if (result == true && is2.readLine() != null) {
			System.out.println(file1+" shorter than "+file2);
			result = false;
		}
		is1.close();
		is2.close();
		return result;		
	}

	private void runTest(String filename) throws IOException {
		
	      // System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		InputStream in = System.in; 
		PrintStream out = System.out;
		
		String inFilename = "src/project1/data/"+filename +".test"; // Input filename: [filename].test here  
		String expectedFilename ="src/project1/data/"+filename +".expected"; // Expected result filename: [filename].expected
		String outFilename = "src/project1/data/"+filename+".out"; // Output filename: [filename].out

		BufferedInputStream is = new BufferedInputStream(new FileInputStream(inFilename));
		System.setIn(is); // redirects standard input to a file, [filename].test 
		PrintStream os = new PrintStream(new FileOutputStream(outFilename));
		System.setOut(os); // redirects standard output to a file, [filename].out 
		
		
		String[] args = {inFilename, "output.txt"};
		Project2.main(args); // Call to YOUR main. May have to rename.
		System.setIn(in); // restores standard input
		System.setOut(out); // restores standard output
		assertTrue(compare(expectedFilename,outFilename));

		// TODO: More informative file comparison will be nice.

	}
	
	@Test
	public void testFile1() throws IOException {
		runTest("test1");
	}
	
	@Test
	public void testFile2() throws IOException {
		runTest("test2");
	}
	
	@Test
	public void testFile3() throws IOException {
		runTest("test3");
	}
	
	@Test
	public void testFile4() throws IOException {
		runTest("test4");
	}
	
	@Test
	public void testFile5() throws IOException {
		runTest("test5");
	}
}
