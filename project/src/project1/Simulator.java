package project1;

import java.util.HashSet;
import java.util.Queue;

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
	 * @param: p the current processes we are working with                                                                                
	 * @param: fcfs to keep track of the statistics for output
	 * @effects: 
	 */

	public void doFCFS(HashSet<Process> p, Statistics fcfs){


		// OUTPUT time <t>m: Simulator ended for FCFS
	}

	/**
	 * This method computes the shortest job first algorithm
	 * 
	 * @param: p the current processes we are working with                                                                                
	 * @param: sjf to keep track of the statistics for output
	 * @effects: 
	 */

	public void doSJF(HashSet<Process> p, Statistics sjf){
		
		
		// OUTPUT time <t>m: Simulator ended for SJF
	}


	/**
	 * This method computes the round robin algorithm
	 * 
	 * @param: p the current processes we are working with                                                                                
	 * @param: rr to keep track of the statistics for output
	 * @effects: 
	 */

	public void doRR(HashSet<Process> p, Statistics rr){
		
		
		
		// OUTPUT time <t>m: Simulator ended for RR
	}

	public void printInterestingEvent(int t, String details, Queue<Process> q){
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
