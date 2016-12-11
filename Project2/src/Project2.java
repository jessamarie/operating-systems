/**  Project1.java
 *  @author Jessica Barre
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Project2 {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("ERROR: Invalid arguments\nUSAGE: .a/out <input-file> <stats-output-file>\n");
		}

		String inputfile = args[0];
		//String outputfile = args[1];

		ArrayList<Process> processes = new ArrayList<Process>();

		/** read data from the input file **/

		try {
			readData(inputfile, processes);
		} catch (IOException e) {
			throw new IllegalArgumentException("ERROR: Invalid input file format\n"
					+ inputfile + " " + e);
		}

		Collections.sort(processes, new ProcessSortByArrivalTime());
		

		/* Simulate each algorithm **/	


		/* Next-Fit */

		NextFit nextFit = new NextFit(processes);
		
		nextFit.run();

		resetProcesses(processes);



		/* Shortest Job First */

		//SJF sjf = new SJF(processes);

		//Statistics sjfStats = new Statistics();

		//sjf.run(sjfStats);

		//resetProcesses(processes);


		/* Round Robin */

		//RR rr = new RR(processes);

		//Statistics rrStats = new Statistics();

		//rr.run(rrStats);

		//resetProcesses(processes);


		/** write data to the output file **/
		/*
		try {
			writeData(outputfile, fcfsStats, sjfStats, rrStats);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error can't find file: "
					+ inputfile + " " + e);
		}

		 */

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
	 * @param: fps An ArrayList to store all of the processes for fcfs
	 * @param: sps An ArrayList to store all of the processes for sjf
	 * @param: rrps An ArrayList to store all of the processes for rrps
	 * @effects: Reads processes in from a file
	 * @throws:  IOException                                                                       
	 */

	public static void readData(String filename, ArrayList<Process> processes)  throws IOException {

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


				Process p = new Process(pid, frames, arrTimes, runTimes, numRuns);

				processes.add(p);

			} else {

				continue;

			}



		} /** End while **/

		reader.close();
	}

	/** 
	 * writeData creates an output file
	 * 
	 * @param filename the name of the output file
	 * @param: fcfs the set of first in first out statistics                                                                                               
	 * @param: sjf the set of shortest job first statistics
	 * @param: rr the set round robin statistics    
	 * @throws IOException 
	 * @effects: writes processes into an output file
	 * @throws:                                                                           
	 */

	private static void writeData(String filename, Statistics fcfs, Statistics sjf, Statistics rr) throws IOException {

		try {

			File file = new File(filename);
			file.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

			writer.write(fcfs.toString());
			writer.newLine();
			writer.write(sjf.toString());
			writer.newLine();
			writer.write(rr.toString());

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			System.err.println("File Not Found");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("something messed up");
			System.exit(1);
		}

	}

}
