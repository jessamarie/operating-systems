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
	//	private int m = 1;                        /* default number of processes available in the CPU */
	private int t_cs = 8;					  /* default time to context switch */
	private int loadTime = t_cs/2;
	private int unloadTime = t_cs/2;
	//	private int n = processes.size();   /* starting number of processes to process*/

	private int numContextSwitches;
	private double totalCPUBurstTime;
	private double totalWaitTime;
	private double totalTurnAroundTime;

	private int numPreemptions;
	private int t_slice = 84;


	/**
	 * Constructor
	 */

	public RR(ArrayList<Process> processes) {
		super(processes);
	}


	/**
	 * {@inheritDoc}
	 */

	public void run(Statistics rr) {

		printInterestingEvent(elapsedTime, "Simulator started for RR", readyQueue);		


		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			transferArrivingProcesses();

			checkIfReadyToReturn();


			if (!readyQueue.isEmpty()) {

				Process p = loadProcess();

				if(p.getWorkTimeLeft() == 0 ) {
					p.setWorkTimeLeft(p.getCpuBurstTime());
				}

				printInterestingEvent(elapsedTime, "Process " + p.toString() + " started using the CPU", readyQueue);

				performBurst(p);

				transferArrivingProcesses();

				checkIfReadyToReturn();

				/* if the RQ is empty, keep performing the same burst until something
				 * enters, or there is no work time left on the burst **/

				while ( checkIfReadyToReturn() == false && 
						readyQueue.isEmpty() && p.getWorkTimeLeft() != 0)  {

					printInterestingEvent(elapsedTime, "Time slice expired; no preemption because ready queue is empty", readyQueue);

					performBurst(p);

				}


				/* If this process still has bursts left after this last burst, then move it to
				 *  the blocking queue, otherwise this terminate and summarize the results */

				if ( p.getWorkTimeLeft() == 0 && p.getNumCurrentBurst() > 0 ) {

					moveToBlocked(p);

				} else if(p.getNumCurrentBurst() > 0) {

					checkIfReadyToReturn();

					returnToReady(p);

					printInterestingEvent(elapsedTime, "Time slice expired; process " + p.toString() + " preempted with " + p.getWorkTimeLeft() + "ms to go", readyQueue);

				} else {

					terminateProcess(p);

					//debug(p); /* Debugging */
				}

				elapsedTime += unloadTime;

			} else {

				elapsedTime++; /* Because nothing is happening if the readyQueue is empty */

			}

		} /* End While */

		/* Set Statistics */
		rr.setType("RR");
		rr.setAvgWaitTime(totalWaitTime/totalNumCPUBursts);
		rr.setAvgTurnAroundTime(totalTurnAroundTime/totalNumCPUBursts);
		rr.setAvgBurstTime(totalCPUBurstTime/totalNumCPUBursts);
		rr.setTotalNumContextSwitches(numContextSwitches);
		rr.setTotalNumPreemptions(numPreemptions);

		System.out.println("time " + elapsedTime + "ms: Simulator ended for RR");

	}


	/**
	 * {@inheritDoc}
	 */

	public void transferArrivingProcesses() {

		for(Process p: processes){

			if(p.getProcessState() == ProcessState.NEW &&
					p.getInitalArrivalTime() <= elapsedTime) {

				p.setProcessState(ProcessState.READY);

				readyQueue.addLast(p);

				printInterestingEvent(p.getInitalArrivalTime(), "Process "+ p.toString() + " arrived", readyQueue);

			}
		}
	}


	/** 
	 * {@inheritDoc}
	 */

	public boolean checkIfReadyToReturn() {

		boolean ready = false;

		while ( (!blockedQueue.isEmpty()) && blockedQueue.getFirst().getReturnTime() <= elapsedTime){

			Process p = blockedQueue.removeFirst();

			returnToReady(p);

			printInterestingEvent(p.getReturnTime(), "Process " + p.toString() + " completed I/O", readyQueue);

			ready = true;

		}

		return ready;
	}




	/**
	 * returnToReady returns a process to the readyQueue if it's return time
	 * is less than the elapsed time
	 * 
	 * @effects removes a process from blockedQueue and adds to readyQueue
	 *          changes state to READY
	 */

	public void returnToReady(Process p) {

		p.setProcessState(ProcessState.READY);

		readyQueue.addLast(p);

		sort(readyQueue);

	}

	/**
	 * {@inheritDoc}
	 */

	protected void sort(LinkedList<Process> rq) {

	}


	/**
	 * {@inheritDoc}
	 */

	public Process loadProcess() {

		numContextSwitches++; /* Only context switch when a process is just about to start */

		Process p = readyQueue.removeFirst();	

		p.setProcessState(ProcessState.RUNNING);

		elapsedTime += loadTime;

		return p;
	}


	/**
	 *{@inheritDoc}
	 */

	public void performBurst(Process p) {

		if( p.getWorkTimeLeft() - t_slice > 0) {

			performBurst(p, t_slice);

			p.setWorkTimeLeft(p.getWorkTimeLeft() - t_slice);

			numPreemptions++; 


		} else { /* This burst is finally finished, just add what's left to the burst */

			performBurst(p, p.getWorkTimeLeft());

			p.setWorkTimeLeft(0);

			totalCPUBurstTime += p.getCpuBurstTime();

			p.setNumBursts(p.getNumCurrentBurst() - 1);

		}
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
	 * {@inheritDoc}
	 */

	public void terminateProcess(Process p) {

		totalWaitTime += p.getWaitTime();

		totalTurnAroundTime += p.getTurnAroundTime();

		p.setProcessState(ProcessState.FINISHED);

		printInterestingEvent(elapsedTime, "Process " + p.toString() + " terminated", readyQueue);

	}


	/**
	 * {@inheritDoc}
	 */

	public void moveToBlocked(Process p) {

		printInterestingEvent(elapsedTime, "Process " + p.toString() + " completed a CPU burst; " + p.getNumCurrentBurst() + " to go", readyQueue);

		p.setProcessState(ProcessState.BLOCKED);

		p.setReturnTime(p.getBurstFinishTime() + p.getIoTime());

		blockedQueue.add(p);

		Collections.sort(blockedQueue, new ProcessSortByReturnTime());

		printInterestingEvent(elapsedTime, "Process " + p.toString() + " blocked on I/O until time " + p.getReturnTime() + "ms", readyQueue);

	}


	/**
	 * @param p the process to debug
	 */

	@SuppressWarnings("unused")
	private void debug(Process p) {
		System.out.print("Process " + p.getProcessID());
		System.out.print(" - total num cs " + numContextSwitches);
		System.out.print("; wait t Added: " + p.getWaitTime() );
		System.out.print("; total wait t: " + totalWaitTime);
		System.out.print("; burst t added: " + p.getCpuBurstTime()); 
		System.out.print("; Total burst t " + totalCPUBurstTime + "\n");
	}

	@Override
	public void run() {

	}

}
