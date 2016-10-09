package project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Project1 {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println("ERROR: Invalid arguments");
		}

		String inputfile = args[0];
		String outputfile = args[1];

		Set<Process> processes = new HashSet<Process>();

		try {
			readData(inputfile, processes);
		} catch (IOException e) {
			throw new IllegalArgumentException("Error can't find file: "
					+ inputfile + " " + e);
		}
		
		Simulator sim = new Simulator();
		Statistics fcfs = new Statistics();
		fcfs.setType("FCFS");
		Statistics sjf = new Statistics();
		fcfs.setType("SJF");
		Statistics rr = new Statistics();
		fcfs.setType("RR");
		
		
		
		System.out.println("Finished reading");

	}

	/** 
	 * @param: filename The path to the text file that contains the processes                                                                                                
	 * @param: process A set to store all of the processes
	 * @effects: Reads processes in from a file
	 * @throws: IOException if file cannot be read                                                                               
	 */

	public static void readData(String filename, Set<Process> processes)  throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		
		String line = null;

		while ((line = reader.readLine()) != null) {

			String[] specs; /** The properties of each process **/
			
			
			/** If the current line begins with a letter split it by the '|' 
			 * delimiter. otherwise, ignore and move on to the next line **/
			
			if (Character.isLetter((line.charAt(0)))) {
				specs = line.split("\\|");            	 
			} else {
				continue;
			}
			
			/** Each process line should have five properties **/
			if (specs.length < 5 || specs.length > 5){
				System.err.println("ERROR: Invalid input file format");
			}

			String trimmedSpec = specs[0].trim();
			specs[0] = trimmedSpec;

			for(int j = 1; j < specs.length; j++){

				trimmedSpec = specs[j].trim();

				/** Make sure the last four properties are numbers **/
				try  
				{  
					Integer.parseInt(trimmedSpec);  
				}  
				catch(NumberFormatException e)  
				{  
					System.err.println("ERROR: Invalid input file format");
				}  

				specs[j] = trimmedSpec;

			}
			
			/** Finally, create a process and add it to the set **/
			String pid = specs[0];
			int initalArrivalTime = Integer.parseInt(specs[1]);
			int cpuBurstTime = Integer.parseInt(specs[2]); 
			int numBursts =  Integer.parseInt(specs[3]);
			int ioTime = Integer.parseInt(specs[4]);
			
			Process p = new Process(pid, initalArrivalTime, cpuBurstTime, numBursts, ioTime);
			
			processes.add(p);
			
		} /** End while **/
		
		reader.close();
	}

}
