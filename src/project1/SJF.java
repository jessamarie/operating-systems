package project1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import project1.Process.ProcessState;

public class SJF extends Algorithm{
	
	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state waiting during IO */

	private int elapsedTime = 0;
	int m = 1;                        /* default num of processes available in the CPU */
	int t_cs = 8;					  /* default time to context switch */

	private int numContextSwitches;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;

	public SJF(List<Process> processes) {
		super(processes);
	}

	public void run(ArrayList<Process> processes, Statistics sjf) {
		
		printInterestingEvent(0, "Start of simulation for SJF", readyQueue);

		int n = processes.size();   /* starting number of processes to process*/
		
		sjf.setType("SJF");	
		

		for(Process process: processes){

			if(process.getInitalArrivalTime() == elapsedTime){

				process.setProcessState(ProcessState.READY);

				readyQueue.addLast(process);

			}
		}
		
		while (!isFinished(processes)) {		
			
			for(Process process: processes){

				if(process.getInitalArrivalTime() < elapsedTime 
						&& process.getProcessState() == ProcessState.NEW){

					process.setProcessState(ProcessState.READY);

					readyQueue.addLast(process);
				}
			}
			
			Collections.sort(readyQueue, new ProcessSortByCPUBurstTime()); /* sort the ready queue by CPU burst time */
			
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
				
				totalCPUBurstTime += p.getCpuBurstTime();

				p.decrementNumBursts();

				elapsedTime = p.getFinishTime();
							

				/* if this process still has bursts left add it to the blocking queue
				 * and change it's state, otherwise this process is finished */

				if ( p.getCurrentBurst() > 0 ) {

					p.setProcessState(ProcessState.BLOCKED);

					blockedQueue.add(p);

					printInterestingEvent(elapsedTime, "Process finished using the CPU", readyQueue);


				} else {

					System.out.println(p.toString());
					
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
		
		sjf.setAvgWaitTime(totalWaitTime/n);
		sjf.setAvgTurnAroundTime(totalTurnAroundTime/n);
		sjf.setAvgBurstTime(totalCPUBurstTime/n);
		sjf.setTotalNumContextSwitches(numContextSwitches);
		
		System.out.println("OUTPUT time " + elapsedTime + "ms: Simulator ended for SJF");

		System.out.println();
		System.out.println(sjf.toString());
		
	}
	
	private void printInterestingEvent(int t, String details, Queue<Process> q){
//		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
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
