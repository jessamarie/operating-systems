package project1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import project1.Process.ProcessState;

public class RR extends Algorithm{

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

	private int elapsedTime = 0;
	int m = 1;                        /* default num of processes available in the CPU */
	int t_cs = 8;					  /* default time to context switch */

	private int numPreemptions;
	private int t_slice = 84;
	private int numContextSwitches;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;

	public RR(ArrayList<Process> processes) {
		super(processes);
	}

	public void run(ArrayList<Process> processes, Statistics rr) {

		printInterestingEvent(0, "Start of simulation for RR", readyQueue);

		int n = processes.size();   /* starting number of processes to process*/

		rr.setType("RR");

		/* Add all processes with arrival time zero to readyQueue */

		for(Process process: processes){

			if(process.getInitalArrivalTime() == elapsedTime){

				process.setProcessState(ProcessState.READY);

				readyQueue.addLast(process);

			}
		}
		
		/* Continue loop until all processes have been finished */

		while (!isFinished(processes)) {		

			for(Process process: processes){

				if(process.getInitalArrivalTime() < elapsedTime 
						&& process.getProcessState() == ProcessState.NEW){

					process.setProcessState(ProcessState.READY);

					readyQueue.addLast(process);
				}
			}

			/* If a process is in the blocked queue, it still has bursts
			 * to calculate, otherwise we can move on to the next process
			 */

			if (!blockedQueue.isEmpty()) {

				printInterestingEvent(elapsedTime, "Process started performing IO", readyQueue);

				Process p = blockedQueue.pop();
				
			
				
				elapsedTime = p.getBurstFinishTime();
				
				p.setProcessState(ProcessState.READY);
				
				readyQueue.addFirst(p);

				printInterestingEvent(elapsedTime, "Process finished performing IO", readyQueue);

			} else {

				printInterestingEvent(elapsedTime, "Process started using the CPU", readyQueue);

				Process p = readyQueue.remove();

				p.setProcessState(ProcessState.RUNNING);
				
				/* Set start time for process only if this is the first
				 * time it has been in the ready queue*/
				if (p.getNumBursts() == p.getNumCurrentBurst()) {
					p.setStartTime(elapsedTime);
				}
/*
				if (p.getWorkTimeLeft() < t_slice ) {
					
				} else if () {
					
				} else {
					
				}
	*/			
				
				totalCPUBurstTime += p.getCpuBurstTime();

				p.setNumBursts(p.getNumCurrentBurst() - 1);

				elapsedTime = p.getBurstFinishTime();
							

				/* if this process still has bursts left add it to the blocking queue
				 * and change it's state, otherwise this process is finished */

				if ( p.getNumCurrentBurst() > 0 ) {

					p.setProcessState(ProcessState.BLOCKED);

					blockedQueue.add(p);

					printInterestingEvent(elapsedTime, "Process finished using the CPU", readyQueue);


				} else {

					p.setWaitTime(p.getStartTime() - p.getInitalArrivalTime());

					p.setTurnAroundTime(elapsedTime - p.getInitalArrivalTime());

					totalWaitTime += p.getWaitTime();

					totalTurnAroundTime += p.getTurnAroundTime();

					p.setProcessState(ProcessState.FINISHED);

					printInterestingEvent(elapsedTime, "Process terminated", readyQueue);
					
					numContextSwitches++; /* Increment num context switches */
					
					elapsedTime += t_cs; /* Add context switch time to elapsedTime */

				}

			} /* End Outer Else */
			
			

		} /* End While */


		rr.setAvgWaitTime(totalWaitTime/n);
		rr.setAvgTurnAroundTime(totalTurnAroundTime/n);
		rr.setAvgBurstTime(totalCPUBurstTime/n);
		rr.setTotalNumContextSwitches(numContextSwitches);
		
		System.out.println("OUTPUT time " + elapsedTime + "ms: Simulator ended for RR");

		System.out.println();
		System.out.println(rr.toString());
		
	}
	

	public boolean isFinished(ArrayList<Process> processes) {

		for (Process process : processes) {

			if (process.getProcessState() != ProcessState.FINISHED) {
			
				return false;
			
			}
		}

		return true;
	}

	@Override
	public void run() {

	}

}
