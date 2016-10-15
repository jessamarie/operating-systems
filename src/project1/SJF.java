package project1;

import project1.Process.ProcessState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implements the First Come First Serve Algorithm
 * 
 * Overview of SJF:
 * - processes arriving at  elapsed time are added to the readyQueue
 * - the readyQueue is sorted in order of increasing cpuBurstTime
 * - while there are items in the blocking queue that are ready to return:
 *   -- remove the process from the blockedQueue and add it to the 
 *      end of the readyQueue
 *   -- resort the readyQueue
 * - load the first process in the readyQueue 
 * - complete the process's CPU burst
 * - unload the process, and
 *   -- added to the blocking queue, or
 *   -- terminated
 * 
 */

public class SJF extends Algorithm {

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


	/**
	 * Constructor
	 */

	public SJF(ArrayList<Process> processes) {
		super(processes);
	}

	/**
	 * {@inheritDoc}
	 */

	public void run(Statistics sjf) {

		printInterestingEvent(elapsedTime, "Simulator started for SJF", readyQueue);


		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			transferArrivingProcesses();		

			checkIfReadyToReturn();


			if (!readyQueue.isEmpty()) {

				Process p = loadProcess();

				performBurst(p); 

				transferArrivingProcesses();

				checkIfReadyToReturn();

				/* If this process still has bursts left, then move it to
				 *  the blocking queue, otherwise terminate */

				if ( p.getNumCurrentBurst() > 0 ) {

					moveToBlocked(p);

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
		sjf.setType("SJF");
		sjf.setAvgWaitTime(totalWaitTime/totalNumCPUBursts);
		sjf.setAvgTurnAroundTime(totalTurnAroundTime/totalNumCPUBursts);
		sjf.setAvgBurstTime(totalCPUBurstTime/totalNumCPUBursts);
		sjf.setTotalNumContextSwitches(numContextSwitches);

		System.out.println("time " + elapsedTime + "ms: Simulator ended for SJF\n");

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
	 * {@inheritDoc}
	 */

	public void returnToReady(Process p) {

		p.setProcessState(ProcessState.READY);

		readyQueue.addLast(p);

		sort(readyQueue);

	}


	/**
	 *{@inheritDoc}
	 */

	protected void sort(LinkedList<Process> rq) {

		Collections.sort(rq, new ProcessSortByCPUBurstTime());

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
	 * {@inheritDoc}
	 */

	public void performBurst(Process p) {

		printInterestingEvent(elapsedTime, "Process " + p.toString() + " started using the CPU", readyQueue);

		p.setStartTime(elapsedTime); 		/* Set start time for this burst */		

		p.setArrivalTime(p.getReturnTime());


		/* Perform the "burst" */

		elapsedTime += p.getCpuBurstTime();

		totalCPUBurstTime += p.getCpuBurstTime();

		p.setBurstFinishTime(elapsedTime);

		p.setNumBursts(p.getNumCurrentBurst() - 1);

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
