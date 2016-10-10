package project1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RR extends Algorithm{

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

	private int elapsedTime = 0;
	int m = 1;                        /* default num of processes available in the CPU */
	int t_cs = 8;					  /* default time to context switch */

	private int time;
	private int numContextSwitches;
	private int numPreemptions;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;

	public RR(List<Process> processes) {
		super(processes);
	}

	@Override
	public void run() {
		
	}
	
	public void run(ArrayList<Process> processes, Statistics rr) {
		printInterestingEvent(0, "Start of simulation for RR", readyQueue);

		int n = processes.size();   /* starting number of processes to process*/
		
		rr.setType("RR");
	}


	public void printInterestingEvent(int t, String details, Queue<Process> q) {
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
