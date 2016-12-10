
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This class implements the Next-Fit Placement Algorithm
 * 
 * Overview of Next-Fit:
 * 
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

public class NextFit extends Algorithm {

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> workingQueue = new LinkedList<Process>(); /* process state blocked during IO */

	private int elapsedTime = 0;
	//	private int m = 1;                        /* default number of processes available in the CPU */
	private int t_cs = 8;					  /* default time to context switch */
	private int loadTime = t_cs/2;
	private int unloadTime = t_cs/2;
	//	private int n = processes.size();   /* starting number of new processes*/
	
	
	private int lastFitLocation = 0; /* The location of the last fit */



	/**
	 * Constructor
	 */

	public NextFit(ArrayList<Process> processes) {
		super(processes);
	}


	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public void run() {
		
		Frames frames = new Frames();

		printInterestingEvent(elapsedTime, "Simulator started (Contiguous -- Next-Fit)");		


		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			transferArrivingProcesses();
			
			//checkIfReadyToReturn();
			
			while(!readyQueue.isEmpty()) {
				
				
				// remove process
				removeFinishedProcesses();
				
				
				// add process
				
				Process p = loadProcess();
				
				printInterestingEvent(p.getNextArrivalTime(), "Process "+ p.getProcessID() + " arrived (requires " + p.getFrames() + " frames)");
				
				if(frames.getEmptyFrames() >= p.getFrames()) {
					
					int start = frames.scanForNextFit(lastFitLocation, p.getFrames());
					
					if (start < 0) {
						frames.defrag();
						start = frames.scanForNextFit(lastFitLocation, p.getFrames());
					}
					
					lastFitLocation = frames.changeFrames(p.getProcessID(), start, p.getFrames());
					
					workingQueue.add(p);
					
					printInterestingEvent(p.getNextArrivalTime(), "Placed process "+ p.getProcessID() + ":");
										
					
				} else {
					printInterestingEvent(p.getNextArrivalTime(), "Cannot place process "+ p.getProcessID() + " -- skipped!");
				}
				

				
				System.out.println(frames.toString());

				
			}


			if (!readyQueue.isEmpty()) {
	
				
				/*

				if ( p.getNumCurrentBurst() > 0 ) {

					if (!blockedQueue.isEmpty()) {
						
						if (blockedQueue.getFirst().getReturnTime() == p.getBurstFinishTime()){

							moveToBlocked(p);

							checkIfReadyToReturn();

						} else {

							checkIfReadyToReturn();

							moveToBlocked(p);
						} 
						
					} else {
						
						checkIfReadyToReturn();
						moveToBlocked(p);
					}


				} else {
					
					checkIfReadyToReturn();
					
					terminateProcess(p);

					//debug(p); 
					
				} */

			//	elapsedTime += unloadTime;

			} else {

				elapsedTime++; /* Because nothing is happening if the readyQueue is empty */

			}

		} /* End While */
		
		printInterestingEvent(elapsedTime, "Simulator ended (Contiguous -- Next-Fit)");
	}


	/**
	 * {@inheritDoc}
	 */

	public void transferArrivingProcesses() {

		for(Process p: processes){

			if(p.getProcessState() == Process.ProcessState.NEW &&
					p.getNextArrivalTime() <= elapsedTime) {

				p.setProcessState(Process.ProcessState.READY);

				readyQueue.addLast(p);
				
			}
		}
	}


	/** 
	 * {@inheritDoc}
	 */

	public boolean removeFinishedProcesses() {

		boolean ready = false;

		while ( (!workingQueue.isEmpty()) && workingQueue.getFirst().getReturnTime() <= elapsedTime){

			Process p = workingQueue.removeFirst();

			returnToReady(p);

			printInterestingEvent(p.getReturnTime(), "Process " + p.toString() + " removed: ");

			ready = true;

		}

		return ready;
	}


	/**
	 * {@inheritDoc}
	 */

	public void returnToReady(Process p) {

		p.setProcessState(Process.ProcessState.READY);

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

		Process p = readyQueue.removeFirst();	

		p.setProcessState(Process.ProcessState.RUNNING);

		elapsedTime += loadTime;

		return p;
	}



	/**
	 * {@inheritDoc}
	 */

	public void performBurst(Process p) {

	//	printInterestingEvent(elapsedTime, "Process " + p.toString() + " started using the CPU", readyQueue);

	//	p.setStartTime(elapsedTime); 		/* Set start time for this burst */	

	//	p.setArrivalTime(p.getReturnTime());


		/* Perform the "burst" */

		//elapsedTime += p.getCpuBurstTime();

	//	totalCPUBurstTime += p.getCpuBurstTime();

//	p.setBurstFinishTime(elapsedTime);

	//	p.setNumBursts(p.getNumCurrentBurst() - 1);

	//	p.setWaitTime(p.getWaitTime() + (p.getStartTime() - p.getArrivalTime()) - loadTime); /* Don't count  this processes load time */

	//	p.setTurnAroundTime(p.getTurnAroundTime() + (p.getBurstFinishTime() - p.getArrivalTime()));


	}


	/**
	 * {@inheritDoc}
	 */

	public void terminateProcess(Process p) {

		p.setProcessState(Process.ProcessState.FINISHED);

	//	printInterestingEvent(elapsedTime, "Process " + p.toString() + " terminated", readyQueue);

	}


	/**
	 * {@inheritDoc}         
	 */

	@Override
	public void moveToBlocked(Process p) {

	//	printInterestingEvent(elapsedTime, "Process " + p.toString() + " completed a CPU burst; " + p.getNumCurrentBurst() + " to go", readyQueue);

		p.setProcessState(Process.ProcessState.BLOCKED);

	//	p.setReturnTime(p.getBurstFinishTime() + p.getIoTime());

		blockedQueue.add(p);

		Collections.sort(blockedQueue, new ProcessSortByReturnTime());

	//	printInterestingEvent(elapsedTime, "Process " + p.toString() + " blocked on I/O until time " + p.getReturnTime() + "ms", readyQueue);

	}


	/**
	 * @param p the process to debug
	 */

	@SuppressWarnings("unused")
	private void debug(Process p) {
		System.out.print("Process " + p.getProcessID());
	}

}
