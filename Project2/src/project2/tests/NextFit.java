package project2.tests;

import java.util.ArrayList;
import java.util.LinkedList;

import Algorithm;
import Frames;
import Process;
import ProcessSortByArrivalTime;
import ProcessSortByReturnTime;
import Process.ProcessState;

/**
 * This class implements the Next-Fit Placement Algorithm
 * 
 * Overview of Next-Fit:
 * 
 * - processes arriving at elapsed time are added to the readyQueue
 */

public class NextFit extends Algorithm {

	private LinkedList<Process> readyQueue = new LinkedList<Process>();   /* process is ready to use the CPU */
	private LinkedList<Process> workingQueue = new LinkedList<Process>(); /* process state blocked during IO */

	private int mostRecentPosition = 0; /* The location of the last fit */



	/**
	 * Constructor
	 */

	public NextFit(ArrayList<Process> processes) {
		super(processes, 0);
	}


	/**
	 * {@inheritDoc}
	 */

	@Override
	public int run(Frames frames) {		

		transferArrivingProcesses(processes, readyQueue);
 		
		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			while ( (!workingQueue.isEmpty()) && workingQueue.getFirst().getReturnTime() <= getElapsedTime()) {
				removeProcess(frames);
			}

			while ( (!readyQueue.isEmpty()) && readyQueue.getFirst().getNextArrivalTime() <= getElapsedTime()) {
				addProcess(frames);
			}

			setElapsedTime(getElapsedTime() + 1); /* Increment time by one */

			transferArrivingProcesses(processes, readyQueue);


		} /* End While */

		return getElapsedTime() - 1;
	}


	/**
	 * {@inheritDoc}
	 */

	private void addProcess(Frames frames) {

		/* Load the first process */

		Process p = loadProcess(readyQueue, new ProcessSortByArrivalTime());
				
		printInterestingEvent(p.getNextArrivalTime(), "Process "+ p.getProcessID() + " arrived (requires " + p.getFrames() + " frames)");

		/* Check to see if there are enough frames left to
		 * place the process, o.w skip this interval */

		if(frames.getEmptyFrames() >= p.getFrames()) {
			placeProcess(frames, p);

		} else {
			skipProcessInterval(p);
		}

		System.out.println(frames.toString());

	}


	/**
	 * {@inheritDoc}
	 */
	
	
	/**
	 * @param frames
	 * @param p
	 * 
	 */
	private void placeProcess(Frames frames, Process p) {

		int start = scan(frames, p);

		if (start < 0) {
			
			/* There are enough frames, just not enough contingious
			 * memory. A defrag is needed */

			defrag(frames, p);

			start = scan(frames, p);
		} 

		useFrames(frames, p, start);
		
		addToQueue(workingQueue, p, new ProcessSortByReturnTime());
		
		printInterestingEvent(p.getNextArrivalTime(), "Placed process "+ p.getProcessID() + ":");
	}


	/**
	 * @param p
	 * 
	 */	
	private int scan(Frames frames, Process p) {
		
		int start = frames.scan(frames.getScanPosition(), p.getFrames());
		
		return start;
	}
	
	/**
	 * defrag defragments the memory
	 * 
	 * @param frames
	 * @param p
	 */
	private void defrag(Frames frames, Process p) {
		
		printInterestingEvent(p.getNextArrivalTime(), "Cannot place process "
				+ p.getProcessID() + " -- starting defragmentation");
		
		String defragged = frames.defrag();

		int postponementTime = frames.getBusyFrames();

		postponeAllProcesses(processes, postponementTime);
		
		setElapsedTime(getElapsedTime() + postponementTime);

		printInterestingEvent(p.getNextArrivalTime(), 
				"Defragmentation complete (moved "+ frames.getBusyFrames() + 
				" frames: " + defragged + ")");

		System.out.println(frames.toString());
	}


	/**
	 * @param frames
	 * @param p
	 * @param start
	 */
	private void useFrames(Frames frames, Process p, int start) {
		mostRecentPosition = frames.use(p.getProcessID(), start, p.getFrames());
	}


	/**
	 * @param p
	 */
	private void skipProcessInterval(Process p) {
		
		printInterestingEvent(p.getNextArrivalTime(), "Cannot place process "+ p.getProcessID() + " -- skipped!");

		int n = p.setNextTimes();

		if (n >= 0) {
			addToQueue(readyQueue, p, new ProcessSortByArrivalTime());
		} else {
			changeProcessState(processes, p, Process.ProcessState.FINISHED);
			}
	}


	/** 
	 * @param frames
	 */
	
	public void removeProcess(Frames frames) {
	
		Process p = loadProcess(workingQueue, new ProcessSortByReturnTime());
	
		removeProcessFromMemory(frames, p);
	
		updateProcessStatus(p);
	}


	/**
	 * @param frames
	 * @param p
	 */
	private void removeProcessFromMemory(Frames frames, Process p) {
		
		frames.clear(p.getProcessID());
	
		printInterestingEvent(p.getReturnTime(), "Process " + p.getProcessID() + " removed:");
	
		System.out.println(frames.toString());
	}


	/**
	 * @param p
	 */
	private void updateProcessStatus(Process p) {
	
		int n = p.setNextTimes();
	
		if (n < 0) {
			changeProcessState(processes, p, Process.ProcessState.FINISHED);
		} else {
	
			changeProcessState(processes, p, Process.ProcessState.READY);
			addToQueue(readyQueue, p, new ProcessSortByArrivalTime());
		}
	}

}
