package project1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SJF extends Algorithm{
	
	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

	public SJF(List<Process> processes) {
		super(processes);
	}

	@Override
	public void run() {
		
	}
	
	public void run(ArrayList<Process> processes, Statistics sjf) {
		
		printInterestingEvent(0, "Start of simulation for SJF", readyQueue);

		int n = processes.size();   /* starting number of processes to process*/
		
		sjf.setType("SJF");
		
		
		
		
	}
	
	public void printInterestingEvent(int t, String details, Queue<Process> q){
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
