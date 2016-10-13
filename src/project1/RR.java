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
 *      end of the readyQueue (sort by Return time)
 * - load the first process in the readyQueue 
 *  - Complete a t_slice worth of the burst, unload the process and
 *    -- if the burst's workTimeLeft is > 0 add to end of RQ
 *    -- else < 0
 *        -- if there are still bursts left add to the BQ
 *        -- else terminate
 * 
 */

public class RR extends Algorithm {

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> blockedQueue = new LinkedList<Process>(); /* process state blocked during IO */
	
	private int totalNumCPUBursts = 0;

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
			
		
			checkIfReadyToReturn();


			if (!readyQueue.isEmpty()) {

				Process p = loadProcess();
				
				if(p.getWorkTimeLeft() == 0 ) {
					p.setWorkTimeLeft(p.getCpuBurstTime());
				}
				

							
				performBurst(p);

				//debug(p); /* Debugging */
				
				/** if the RQ is empty, keep performing a burst until something
				 * enters **/
				while ( checkIfReadyToReturn() == false && 
						readyQueue.isEmpty() && p.getWorkTimeLeft() != 0)  {
					
					//System.out.println("\nprocess is continuing..,"); /* Debugging */
					//debug(p);                                         /* Debugging */
					
					performBurst(p);
					
				}
				
				numContextSwitches++;
				

				/* If this process still has bursts left after this last burst, then move it to
				 *  the blocking queue, otherwise this terminate and summarize the results */

				if ( p.getWorkTimeLeft() == 0 && p.getNumCurrentBurst() > 0 ) {

					moveToBlocked(p);
					
					//System.out.print(" it will return to RQ at " + p.getReturnTime() + "\n"); /* Debugging */
					
				} else if(p.getNumCurrentBurst() > 0) {
					
					checkIfReadyToReturn();
					
					returnToReady(p);
					
					//System.out.println("\nProcess " + p.getProcessID() + " is returning to the RQ"); /* Debugging */

				} else {

					terminateProcess(p);
				}

				elapsedTime += unloadTime;
				
				//System.out.println("Process " + p.getProcessID() + " unloaded... " + elapsedTime + "ms\n"); /* Debugging */

			} else {

				elapsedTime++; /* Because nothing is happening if the readyQueue is empty */

			}

		} /* End While */
		
		elapsedTime -= unloadTime; /* Subtract last unload time */

		/* Set Statistics */
		rr.setType("RR");
		rr.setAvgWaitTime(totalWaitTime/totalNumCPUBursts);
		rr.setAvgTurnAroundTime(totalTurnAroundTime/totalNumCPUBursts);
		rr.setAvgBurstTime(totalCPUBurstTime/totalNumCPUBursts);
		rr.setTotalNumContextSwitches(numContextSwitches);
		rr.setTotalNumPreemptions(numPreemptions);

		System.out.println("OUTPUT time " + elapsedTime + "ms: Simulator ended for RR");

		//System.out.println(rr.toString()); /*DEBUG*/

	}

	/**
	 * @param p
	 */
	private void performBurst(Process p) {
		if( p.getWorkTimeLeft() - t_slice > 0) {
			
			printInterestingEvent(elapsedTime, "Process started using the CPU", readyQueue);
			
			performBurst(p, t_slice);
			
			printInterestingEvent(elapsedTime, "Process finished using the CPU", readyQueue);
			
			p.setWorkTimeLeft(p.getWorkTimeLeft() - t_slice);
			
			printInterestingEvent(elapsedTime, "preemption occured", readyQueue);		
			
			numPreemptions++; 

			
			
		} else { /* This burst is finally finished, just add what's left to the burst */
			
			performBurst(p, p.getWorkTimeLeft());
			
			p.setWorkTimeLeft(0);
			
			totalCPUBurstTime += p.getCpuBurstTime();
			
			p.setNumBursts(p.getNumCurrentBurst() - 1);

		}
	}
	
	/**
	 * returnToReady returns a process to the readyQueue if it's return time
	 * is less than the elapsed time
	 * 
	 * @effects removes a process from blockedQueue and adds to readyQueue
	 *          changes state to READY
	 */
	
	protected void returnToReady(Process p) {
		

		p.setProcessState(ProcessState.READY);

		readyQueue.addLast(p);

	}

	
	/**
	 * checkIfReadyToReturn checks if the blockedQueue has any elements left to return
	 * 
	 * @effects returns any elements that are ready to the readyQueue
	 * @return true if there were elements to return, and o.w false
	 */
    
	protected boolean checkIfReadyToReturn() {
		
		boolean ready = false;
		
		while ( (!blockedQueue.isEmpty()) && blockedQueue.getFirst().getReturnTime() <= elapsedTime){

			printInterestingEvent(blockedQueue.getFirst().getReturnTime(), "Process finished performing IO", readyQueue);
			
			Process p = blockedQueue.removeFirst();
			
			returnToReady(p);
						
			ready = true;
			
		}
		
		return ready;
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
	
	private void performBurst(Process p, int addTime) {
		

		p.setStartTime(elapsedTime); 		/* Set start time for this burst */		

		p.setArrivalTime(p.getReturnTime());
			
		/* Perform the "burst" */
		
		elapsedTime += addTime;

		p.setBurstFinishTime(elapsedTime);

		p.setWaitTime(p.getWaitTime() + (p.getStartTime() - p.getArrivalTime()) - loadTime); /* Don't count  this processes load time */

		p.setTurnAroundTime(p.getTurnAroundTime() + (p.getBurstFinishTime() - p.getArrivalTime()));
		
		totalNumCPUBursts++;

		
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


		blockedQueue.add(p);

		printInterestingEvent(elapsedTime, "Process started performing IO", readyQueue);

		Collections.sort(blockedQueue, new ProcessSortByReturnTime());

	}
	

	/**
	 * @param p the process to debug
	 */
	
	private void debug(Process p) {
		System.out.print("Process " + p.getProcessID());
		System.out.print(" slice started running at " + p.getStartTime());
		System.out.print(" and finished at " + p.getBurstFinishTime());
		System.out.print(" with a wtl of " + p.getWorkTimeLeft() );
	}

	@Override
	public void run() {

	}

}
