package project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Simulator {

	/** 
	 * Empty Constructor                                                                              
	 */

	public Simulator() {
		
	}
	
	public void work(ArrayList<Process> p, int n, int m, int t_cs){
		
	}

	/**
	 * This method computes the first come first serve algorithm
	 * 
	 * @param: processes the current processes we are working with                                                                                
	 * @param: fcfs to keep track of the statistics for output
	 * @effects:
	 */

	public void doFCFS(ArrayList<Process> processes, Statistics fcfs){

		ArrayList<Process> sortedProcesses = processes;
		Collections.sort(sortedProcesses, new ProcessSortByArrivalTime());
		
		LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
		LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

		int elapsedTime = 0;
		int n = sortedProcesses.size();   /* starting number of processes to process*/
		int m = 1;                        /* default num of processes available in the cpu */
		int t_cs = 8;					  /* default time to context switch */

		
		
		printInterestingEvent(elapsedTime, "Start of simulation for FCFS", readyQueue);
		
		while (n > 0) {
			
			//work(sortedProcesses, n, m, t_cs);
		
		
			
		
		} /* End While */

		
/*/

		int totalTime = 0;
		Set<Process> processesCopy = processes; //So we don't change the original

		for (Process p : processesCopy) {
			totalTime += ( (p.getNumBursts() * p.getCpuBurstTime()) + ( (p.getNumBursts()-1)*p.getIoTime() ) ) ; 
		}
		while (t < totalTime) {
			
			if (!processesCopy.isEmpty()) {
				
				for ( Process p : processesCopy) {
					if (p.getInitalArrivalTime() == t) {
						readyQueue.add(p);
						processesCopy.remove(p);
					}
				}
			}		
			
			t++;
		}*/

		
		// OUTPUT time <t>m: Simulator ended for FCFS
	}

	
	
	/**
	 * This method computes the shortest job first algorithm
	 * 
	 * @param: p the current processes we are working with                                                                                
	 * @param: sjf to keep track of the statistics for output
	 * @effects: 
	 */

	public void doSJF(ArrayList<Process> processes, Statistics sjf){


		// OUTPUT time <t>m: Simulator ended for SJF
	}


	/**
	 * This method computes the round robin algorithm
	 * 
	 * @param: processes the current processes we are working with                                                                                
	 * @param: rr to keep track of the statistics for output
	 * @effects: 
	 */

	public void doRR(ArrayList<Process> processes, Statistics rr){
		
		
		
		// OUTPUT time <t>m: Simulator ended for RR
	}

	public void printInterestingEvent(int t, String details, Queue<Process> q){
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
