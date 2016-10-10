package project1;

import java.util.List;
import java.util.Queue;

import project1.Process.ProcessState;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implements the First Come First Serve Algorithm
 * 
 * */

public class FCFS extends Algorithm{

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

	private int elapsedTime = 0;
	int m = 1;                        /* default num of processes available in the CPU */
	int t_cs = 8;					  /* default time to context switch */

	private int time;
	private int numContextSwitches;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;

	public FCFS(List<Process> processes) {
		super(processes);
	}

	public void run(ArrayList<Process> processes, Statistics fcfs) {

		printInterestingEvent(0, "Start of simulation for FCFS", readyQueue);

		int n = processes.size();   /* starting number of processes to process*/

		/* Notes:
		 * Wait time = Time Since Entry to Ready Queue - ioBursts - Context Switch Time
		 * 
		 * AvgWaitTime = (wt_1 + wt_2 + wt_3 + ... + wt_n)/n 
		 * 
		 * 
		 * avgCPUBurstTime = Sum of all bursts
		 * -- Does not include context switch times
		 * 
		 * TurnaroundTime =  wait time + burst time 
		 *                OR  CPUBursttimes + IOtimes + ContextSwitchTimes - Arrival Time
		 *              
		 *  A context switch occurs each time a process leaves the
		 *  CPU and is replaced by another process.
		 * 
		 * I/O wait time is defined as the amount of time from the end of the CPU burst (i.e., before the context switch) to the
		 * end of the I/O operation
		 * 
		 * DEBUG Statistics with
		 * System.out.println(fcfs.toString());
		 * 
		 * use Linked list add and removeFirst(), removeLast()
		 * */
		fcfs.setType("FCFS");

		/* Add all processes with arrival time zero to readyQueue */

		for(Process process: processes){

			if(process.getInitalArrivalTime() == elapsedTime){

				process.setProcessState(ProcessState.READY);

				readyQueue.addLast(process);

			}
		}
		
		/* Continue loop until all processes have been finished */

		while (!isFinished(processes)) {
			
			elapsedTime += t_cs; /* Add context switch time to elapsedTime */
			

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
				
				p.addIOBurst(elapsedTime);
				
				elapsedTime = p.getFinishTime();
				
				p.setProcessState(ProcessState.READY);
				
				readyQueue.addFirst(p);

				printInterestingEvent(elapsedTime, "Process finished performing IO", readyQueue);

			} else {

				printInterestingEvent(elapsedTime, "Process started using the CPU", readyQueue);

				Process p = readyQueue.remove();

				p.setProcessState(ProcessState.RUNNING);

				
				/* Set start time for process only if this is the first
				 * time it has been in the ready queue*/
				if (p.getNumBursts() == p.getCurrentBurst()) {
					p.setStartTime(elapsedTime);
				}

				p.addCPUBurst(elapsedTime);

				p.decrementNumBursts();

				elapsedTime = p.getFinishTime();
							

				/* if this process still has bursts left add it to the blocking queue
				 * and change it's state, otherwise this process is finished */

				if ( p.getCurrentBurst() > 0 ) {

					p.setProcessState(ProcessState.BLOCKED);

					blockedQueue.add(p);

					printInterestingEvent(elapsedTime, "Process finished using the CPU", readyQueue);


				} else {

					p.setWaitTime(elapsedTime - p.getInitalArrivalTime());

					p.setTurnAroundTime(p.getWaitTime() + p.getCpuBurstTime());
					
					totalCPUBurstTime += p.getCpuBurstTime();

					totalWaitTime += p.getWaitTime();

					totalTurnAroundTime += p.getTurnAroundTime();

					p.setProcessState(ProcessState.FINISHED);

					printInterestingEvent(elapsedTime, "Process terminated", readyQueue);

				}

			} /* End Outer Else */
			
			numContextSwitches++;

		} /* End While */


		fcfs.setAvgWaitTime(totalWaitTime/n);
		fcfs.setAvgTurnAroundTime(totalTurnAroundTime/n);
		fcfs.setAvgBurstTime(totalCPUBurstTime/n);
		fcfs.setTotalNumContextSwitches(numContextSwitches);
		
		System.out.println("OUTPUT time " + elapsedTime + "ms: Simulator ended for FCFS");

		System.out.println();
		System.out.println(fcfs.toString());
		
	}
	

	public void printInterestingEvent(int t, String details, Queue<Process> q) {
		
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
		
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
