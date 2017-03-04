package project1;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class Simulator {

	/** 
	 * Empty Contructor                                                                              
	 */

	public Simulator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method computes the first come first serve algorithm
	 * 
	 * @param: processes the current processes we are working with                                                                                
	 * @param: fcfs to keep track of the statistics for output
	 * @effects: 
	 */

	public void doFCFS(Set<Process> processes, Statistics fcfs){

		// This is how we set the Statisics:
		fcfs.setAvgBurstTime(122.22);
		fcfs.setAvgWaitTime(10.23);
		fcfs.setAvgTurnAroundTime(122.22);
		fcfs.setTotalNumContextSwitches(10);
		fcfs.setTotalNumPreemptions(5);
		
		// OUTPUT time <t>m: Simulator ended for FCFS
	}

	/**
	 * This method computes the shortest job first algorithm
	 * 
	 * @param: p the current processes we are working with                                                                                
	 * @param: sjf to keep track of the statistics for output
	 * @effects: 
	 */

	public void doSJF(Set<Process> processes, Statistics sjf){


		// OUTPUT time <t>m: Simulator ended for SJF
	}


	/**
	 * This method computes the round robin algorithm
	 * 
	 * @param: processes the current processes we are working with                                                                                
	 * @param: rr to keep track of the statistics for output
	 * @effects: 
	 */

	public void doRR(Set<Process> processes, Statistics rr){
		
		
		
		// OUTPUT time <t>m: Simulator ended for RR
	}

	public void printInterestingEvent(int t, String details, Queue<Process> q){
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
