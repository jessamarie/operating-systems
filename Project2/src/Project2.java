/**  
 *  @author Jessica Barre
 *  @name:  Project2.java (main class)
 *  Date Due: Dec 12, 2016
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Project2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("ERROR: Invalid arguments\nUSAGE: .a/out <input-file> <input-file>\n");
		}

		String file1 = args[0];
		String file2 = args[1];

		ArrayList<Process> processes = new ArrayList<Process>();
		ArrayList<String> references = new ArrayList<String>();

		/** read data from the input files **/

		try {
			readProcesses(file1, processes);
		} catch (IOException e) {
			throw new IllegalArgumentException("ERROR: Invalid input file format\n"
					+ file1 + " " + e);
		}

		try {
			readReferences(file2, references);
		} catch (IOException e) {
			throw new IllegalArgumentException("ERROR: Invalid input file format\n"
					+ file2 + " " + e);
		}

		Collections.sort(processes, new ProcessSortByArrivalTime());


		/* Simulate each algorithm **/	


		/* Next-Fit */

		Algorithm cont = new Contiguous(processes);

		System.out.println("time 0ms: " + "Simulator started (Contiguous -- Next-Fit)");

		int time = cont.run(new NextFitFrames());

		System.out.println("time " + time + "ms: " + "Simulator ended (Contiguous -- Next-Fit)");

		System.out.println();

		resetProcesses(processes);


		/* Best-Fit */

		System.out.println("time 0ms: " + "Simulator started (Contiguous -- Best-Fit)");

		time = cont.run(new BestFitFrames());

		System.out.println("time " + time + "ms: " + "Simulator ended (Contiguous -- Best-Fit)");

		System.out.println();

		resetProcesses(processes);


		/* Worst-Fit */
		System.out.println("time 0ms: " + "Simulator started (Contiguous -- Worst-Fit)");

		time = cont.run(new WorstFitFrames());

		System.out.println("time " + time + "ms: " + "Simulator ended (Contiguous -- Worst-Fit)");


		System.out.println();

		resetProcesses(processes);


		/* Non-Contiguous First-Fit */

		Algorithm noncont = new NonContiguous(processes);

		System.out.println("time 0ms: " + "Simulator started (Non-contiguous)");

		time = noncont.run(new NonContiguousFrames());

		System.out.println("time " + time + "ms: " + "Simulator ended (Non-contiguous)");

		resetProcesses(processes);

		System.out.println();
		
		
		/* Append memory placement algorithms to the end */
		
		VirtualMemory vm = new VirtualMemory(references);
		
		vm.doOPT();
		
		vm.init();
		
		System.out.println();
		
		vm.doLRU();
		
		vm.init();
		
		System.out.println();
		
		vm.doLFU();



	}


	/** 
	 * @param: processes the ArrayList of processes to reset
	 * @effects: Resets the process to it's original state
	 *                                                                   
	 */

	private static void resetProcesses(ArrayList<Process> processes) {
		for (Process p: processes ) {
			p.init();

		}

	}

	/** 
	 * @param: filename The path to the text file that contains the processes                                                                                                
	 * @param: processes An ArrayList to store all of the processes
	 * @effects: Reads processes in from a file
	 * @throws:  IOException                                                                       
	 */

	public static void readProcesses(String filename, ArrayList<Process> processes)  throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = null;

		while ((line = reader.readLine()) != null) {

			String[] specs; /** The properties of each process **/

			/** If the current line begins with a letter split it by the '|' 
			 * delimiter. otherwise, ignore and move on to the next line **/

			if (Character.isDigit(line.charAt(0))) {

				specs = line.split("\n");

				specs[0] = specs[0].trim();

				//int numProcesses = Integer.parseInt(specs[0]);

			} 
			else if (Character.isLetter((line.charAt(0)))) {

				specs = line.split(" ");  

				String trimmedSpec = specs[0].trim();

				specs[0] = trimmedSpec;



				for(int j = 1; j < specs.length; j++){

					trimmedSpec = specs[j].trim();

					specs[j] = trimmedSpec;

				}


				/* Finally, parse the data,
				 * create a process and add it to the list **/

				String pid = specs[0];

				int frames = Integer.parseInt(specs[1]);


				int numRuns = specs.length - 2; /* gets the number of this
				                                    process's #/# pairs */

				int arrTimes[] = new int[numRuns];
				int runTimes[] = new int[numRuns];


				int i = 2, j = 0;

				while (i < specs.length) {

					String[] times = specs[i].split("/");

					arrTimes[j] = Integer.parseInt(times[0]); 
					runTimes[j] =  Integer.parseInt(times[1]);


					i++; j++;

				}


				Process p = new Process(pid, frames, arrTimes, runTimes);

				processes.add(p);

			} else {

				continue;

			}



		} /** End while **/

		reader.close();
	}

	/** 
	 * @param: filename The path to the text file that contains the page references                                                                                                
	 * @param: references An ArrayList to store all of the page references
	 * @effects: Reads page references in from a file
	 * @throws:  IOException                                                                       
	 */

	public static void readReferences(String filename, ArrayList<String> references)  throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = null;

		while ((line = reader.readLine()) != null) {

			String[] specs; /** The properties of each process **/

			/** If the current line begins with a letter split it by the '|' 
			 * delimiter. otherwise, ignore and move on to the next line **/

			specs = line.split(" ");

			for (int i = 0; i < specs.length; i++) {
				references.add(specs[i]);
			}

	} /** End while **/

	reader.close();
}

}
