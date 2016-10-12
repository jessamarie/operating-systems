package project1;

import project1.Process.ProcessState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implements the First Come First Serve Algorithm
 * 
 * Overview of RR:
 * - processes arriving at elapsed time are added to the readyQueue
 * - while there are items in the blocking queue that are ready to return:
 *   -- remove the process from the blockedQueue and add it to the 
 *      end of the readyQueue
 * - load the first process in the readyQueue 
 * - complete the process's CPU burst
 * - unload the process, and
 *   -- added to the blocking queue, or
 *   -- terminated
 * 
 */

public class RR extends Algorithm {

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state blocked during IO */

	private int elapsedTime = 0;
	private int m = 1;                        /* default number of processes available in the CPU */
	private int t_cs = 8;					  /* default time to context switch */
	private int loadTime = t_cs/2;
	private int unloadTime = t_cs/2;
	private int n = processes.size();   /* starting number of processes to process*/

	private int numContextSwitches;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;
	
	private int numPreemptions;
	private int t_slice = 84;

	public RR(ArrayList<Process> processes) {
		super(processes);
	}

	public void run(Statistics rr) {

		printInterestingEvent(0, "Start of simulation for RR", readyQueue);

		/* Notes:
		 * Wait time = arrivalTime - load
		 * 
		 * avgCPUBurstTime = Sum of all bursts
		 * 
		 * TurnaroundTime =  BurstFinishTime - ArrivalTime 
		 *   
		 * Load process before running CPU Burst and unload after
		 *  
		 * I/O is the the amount of time from the end of the CPU burst (i.e., before the context switch) to the
		 * end of the I/O operation
		 * 
		 * */
		
		

		/* Add all processes with arrival time zero to readyQueue */

		for(Process p: processes){

			if(p.getInitalArrivalTime() == elapsedTime){

				p.setProcessState(ProcessState.READY);

				readyQueue.addLast(p);

			}
		}
		
		
		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			
			/* Add new processes to readyQueue if their arrival times
			 *  have reached the elapsed time  */

			for(Process p: processes){

				if(p.getProcessState() == ProcessState.NEW &&
						p.getInitalArrivalTime() <= elapsedTime) {

					p.setProcessState(ProcessState.READY);

					readyQueue.addLast(p);
					
				}
			}


			/* If a process is in the blocked queue, it still has bursts
			 * to calculate, otherwise move on to the next process
			 */

			while ( (!blockedQueue.isEmpty()) && blockedQueue.getFirst().getReturnTime() <= elapsedTime){

				returnToReady();

			}


			if (!readyQueue.isEmpty()) {

				Process p = loadProcess();

				performBurst(p); 

				//debug(p); /* Debugging */

				/* If this process still has bursts left after this last burst, then move it to
				 *  the blocking queue, otherwise this terminate and summarize the results */

				if ( p.getNumCurrentBurst() > 0 ) {

					moveToBlocked(p);

				} else {

					terminateProcess(p);
				}

				elapsedTime += unloadTime;
				
				//System.out.println("Process " + p.getProcessID() + " unloaded... " + elapsedTime + "ms"); /* Debugging */

			} else {

				elapsedTime++; /* Because nothing is happening if the readyQueue is empty */

			}

		} /* End While */
		
		elapsedTime -= unloadTime; /* Subtract last unload time */

		/* Set Statistics */
		rr.setType("RR");
		rr.setAvgWaitTime(totalWaitTime/n);
		rr.setAvgTurnAroundTime(totalTurnAroundTime/n);
		rr.setAvgBurstTime(totalCPUBurstTime/n);
		rr.setTotalNumContextSwitches(numContextSwitches);

		System.out.println("OUTPUT time " + elapsedTime + "ms: Simulator ended for RR");

		// System.out.println(rr.toString());

	}

	
	/**
	 * returnToReady returns a process to the readyQueue if it's return time
	 * is less than the elapsed time
	 * 
	 * @effects removes a process from blockedQueue and adds to readyQueue
	 *          changes state to READY
	 */
	
	private void returnToReady() {
		
		printInterestingEvent(blockedQueue.getFirst().getReturnTime(), "Process finished performing IO", readyQueue);

		Process p = blockedQueue.removeFirst();

		p.setProcessState(ProcessState.READY);

		//System.out.println("Process " + p.getProcessID() + " is returning to the RQ"); /* Debugging */

		readyQueue.addLast(p);
	}

	
	/**
	 * loadProcess removes the first element of the readyQueue and
	 * changes it's state to RUNNING
	 * 
	 * @return p the process to be loaded
	 * @effects changes the state of P to running and adds the load time
	 *          to the elapsed time
	 */
	
	private Process loadProcess() {
		
		Process p = readyQueue.removeFirst();	

		p.setProcessState(ProcessState.RUNNING);

		elapsedTime += loadTime;
		
		//System.out.println("Process " + p.getProcessID() + " loading... " + elapsedTime + "ms"); /* Debugging */
		
		return p;
	}
	

	/**
	 * performBurst performs a cpu burst
	 * 
	 * @param p the process currently running
	 * @effects performs a cpu burst and changes waitTime, TurnAroundTime, 
	 *          numBursts, burstFinishTime, elapsedTime, numContextSwitches,
	 *          and totalCPUBurstTime
	 */
	
	private void performBurst(Process p) {
		
		printInterestingEvent(elapsedTime, "Process started using the CPU", readyQueue);

		p.setStartTime(elapsedTime); 		/* Set start time for this burst */		

		/* Set arrival time for process only if this is not 
		 * the first time it has been in the ready queue */

		if (p.getNumBursts() != p.getNumCurrentBurst()) {
			p.setArrivalTime(p.getReturnTime());
		}
		
		/* Perform the "burst" */

		elapsedTime += p.getCpuBurstTime();
		
		totalCPUBurstTime += p.getCpuBurstTime();
		
		printInterestingEvent(elapsedTime, "Process finished using the CPU", readyQueue);
		
		p.setBurstFinishTime(elapsedTime);

		p.setNumBursts(p.getNumCurrentBurst() - 1);

		p.setWaitTime(p.getWaitTime() + (p.getStartTime() - p.getArrivalTime()) - loadTime); /* Don't count  this processes load time */

		p.setTurnAroundTime(p.getTurnAroundTime() + (p.getBurstFinishTime() - p.getArrivalTime()));

		numContextSwitches++;
		
	}
	

	/**
	 * terminateProcess is called when a process has zero bursts left to process.
	 * The process is summarized and then it's state is changed to finished.
	 * 
	 * @param p the process to terminate
	 * @effects changes the state of p to finished, then adds it's wait time to totalWaitTime, 
	 *          and adds it's turnAroundTime to totalTurnAroundTime
	 */
	
	private void terminateProcess(Process p) {

		totalWaitTime += p.getWaitTime();

		totalTurnAroundTime += p.getTurnAroundTime();

		p.setProcessState(ProcessState.FINISHED);

		//System.out.print(" and terminated at " + p.getBurstFinishTime() + "\n"); /* Debugging */

		printInterestingEvent(elapsedTime, "Process terminated", readyQueue);

	}
	

	/**
	 * moveToBlocked moves a process to the blockedQueue
	 * 
	 * @param p the process to be moved to block
	 * @effects changes the state of p to BlOCKED, sets it's return time,  and adds it to the blockedQueue.
	 *          The blocked queue is also sorted by return time to ensure that processes return to the 
	 *          readyQueue in the correct order.
	 *          
	 */
	
	private void moveToBlocked(Process p) {

		p.setProcessState(ProcessState.BLOCKED);

		p.setReturnTime(p.getBurstFinishTime() + p.getIoTime());

		// System.out.print(" it will return to RQ at " + p.getReturnTime() + "\n"); /* Debugging */

		blockedQueue.add(p);

		printInterestingEvent(elapsedTime, "Process started performing IO", readyQueue);

		Collections.sort(blockedQueue, new ProcessSortByReturnTime());

	}
	

	/**
	 * @param p the process to debug
	 */
	
	private void debug(Process p) {
		System.out.print("Process " + p.getProcessID());
		System.out.print(" on burst " + (p.getNumCurrentBurst() + 1));
		System.out.print(" arrived in RQ at " + p.getArrivalTime());
		System.out.print(" started running at " + p.getStartTime());
		System.out.print(" and finished this burst at " + p.getBurstFinishTime());
		System.out.print(" with a wait time of " + (p.getStartTime() - p.getArrivalTime() - loadTime));
	}

	
	/**
	 * @param processes the ArrayList of processes to check whether all processes are 
	 *        finished
	 * @return true if all processes are FINISHED, o.w false
	 */
	
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
