package project1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RR extends Algorithm{

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

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
