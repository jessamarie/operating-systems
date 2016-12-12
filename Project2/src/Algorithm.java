
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Algorithms is an abstract class meant to hold the similar
 * code which is used in a memory placement algorithm
 * 
 * overview of a memory placement algorithm:
 * 
 * - Processes arriving at elapsed time are added to the readyQueue
 * - While there are still unfinished processes
 *   - While workingQueue is not empty
 *       - remove processes that are finished running. 
 *       - Mark either finished or return to readyQueue
 *     End While
 *   - While readyQueue is not empty 
 *     - add processes to memory that are arriving
 *       based for a contiguous or non-contiguous algorithm
 *     - set next interval, if any
 *     - mark as running
 *      - Add these processes to the working Queue
 *     End While
 *   - increment elapsed time by one
 *   - transfer new processes to the readyQueue
 * End While
 * */

public abstract class Algorithm {

	protected ArrayList<Process> processes;
	private int elapsedTime;

	protected LinkedList<Process> readyQueue;
	protected LinkedList<Process> workingQueue;

	public Algorithm(ArrayList<Process> processes) {
		this.processes = processes;
		this.elapsedTime = 0;

		readyQueue = new LinkedList<Process>(); 
		workingQueue = new LinkedList<Process>();

	}


	/* SETTERS and GETTERS */


	/**
	 * @return the elapsedTime
	 */

	public int getElapsedTime() {
		return elapsedTime;
	}


	/**
	 * @param elapsedTime the elapsedTime to set
	 */

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}


	/**
	 * @return the readyQueue
	 */

	public LinkedList<Process> getReadyQueue() {
		return readyQueue;
	}


	/**
	 * @param readyQueue the readyQueue to set
	 */

	public void setReadyQueue(LinkedList<Process> readyQueue) {
		this.readyQueue = readyQueue;
	}


	/**
	 * @return the workingQueue
	 */

	public LinkedList<Process> getWorkingQueue() {
		return workingQueue;
	}


	/**
	 * @param workingQueue the workingQueue to set
	 */

	public void setWorkingQueue(LinkedList<Process> workingQueue) {
		this.workingQueue = workingQueue;
	}


	/* Algorithm methods */


	/**
	 * {@inheritDoc}
	 */

	protected int run(Frames frames) {

		setElapsedTime(0);

		transferArrivingProcesses(processes, getReadyQueue());

		/* Loop terminates when all processes are finished */

		while (!isFinished(processes)) {

			while ( (!getWorkingQueue().isEmpty()) && getWorkingQueue().getFirst().getReturnTime() <= getElapsedTime()) {
				removeProcess(frames);
			}

			while ( (!getReadyQueue().isEmpty()) && getReadyQueue().getFirst().getNextArrivalTime() <= getElapsedTime()) {
				addProcess(frames);
			}

			setElapsedTime(getElapsedTime() + 1); /* Increment time by one */

			transferArrivingProcesses(processes, getReadyQueue());


		} /* End While */

		return getElapsedTime() - 1;

	}


	/** 
	 *  transferArrivingProcesses adds new processes to the readyQueue 
	 *  if their arrival times have reached the elapsed time 
	 *  
	 *  @effects adds processes to the readyQueue
	 */

	public void transferArrivingProcesses(ArrayList<Process> processes, LinkedList<Process> q) {

		for(Process p: processes){

			if(p.getProcessState() == Process.ProcessState.NEW &&
					p.getNextArrivalTime() <= elapsedTime) {

				changeProcessState(processes, p, Process.ProcessState.READY);

				addToQueue(q, p, new ProcessSortByArrivalTime());

			}
		}
	}


	/** 
	 * @param frames
	 */

	public void removeProcess(Frames frames) {

		Process p = loadProcess(getWorkingQueue(), new ProcessSortByReturnTime());

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
	 * addProcess adds a process to the workingQueue
	 * 
	 * @param frames
	 * 
	 */

	protected void addProcess(Frames frames) {

		/* Load the first process */

		Process p = loadProcess(getReadyQueue(), new ProcessSortByArrivalTime());

		printInterestingEvent(p.getNextArrivalTime(), "Process "+ p.getProcessID() + " arrived (requires " + p.getFrames() + " frames)");

		/* Check to see if there are enough frames left to
		 * place the process, o.w skip this interval */

		if(frames.getEmptyFrames() >= p.getFrames()) {
			placeProcessIntoMemory(frames, p);

		} else {
			skipProcessInterval(p);
		}

		System.out.println(frames.toString());

	}


	/**
	 * placePocess places a process into memory
	 *  
	 * must be overridden in a subtype. 
	 * 
	 * @param frames
	 * @param p
	 */

	protected abstract void placeProcessIntoMemory(Frames frames, Process p);


	/**
	 * @param p
	 */

	protected void skipProcessInterval(Process p) {

		printInterestingEvent(p.getNextArrivalTime(), "Cannot place process "+ p.getProcessID() + " -- skipped!");

		int n = p.setNextTimes();

		if (n >= 0) {
			addToQueue(getReadyQueue(), p, new ProcessSortByArrivalTime());
		} else {
			changeProcessState(processes, p, Process.ProcessState.FINISHED);
		}
	}

	/**
	 * loadProcess removes the first element from a queue and sorts
	 * 
	 * @param q the queue
	 * @effects removes the first element of q
	 * @returns the first element of q
	 */

	public Process loadProcess(LinkedList<Process> q, Comparator<Process> c) {
		Process p = q.removeFirst();
		sort(q, c);
		return p;
	}


	/**
	 * addToQueue adds an element to a queue and sorts
	 * 
	 * @param q the queue
	 * @param p the process to add
	 * @param c the sorting comparator
	 * @effects adds to to the end of q and sorts
	 */

	public void addToQueue(LinkedList<Process> q, Process p, Comparator<Process> c) {
		q.addLast(p);
		sort(q, c);
	}


	/**
	 * @param t the current elapsed time
	 * @param details the interesting details of the event
	 */

	protected void printInterestingEvent(int t, String details) {

		System.out.println("time " + t + "ms: " + details);

	}


	/**
	 * @param processes the ArrayList of processes to check whether all processes are 
	 *        in the FINISHED state
	 * @return true if all processes are FINISHED, o.w false
	 */

	protected boolean isFinished(ArrayList<Process> processes) {

		for (Process process : processes) {

			if (process.getProcessState() != Process.ProcessState.FINISHED) {

				return false;

			}
		}

		return true;
	}


	/**
	 * @param processes the ArrayList of processes to postpone
	 * @param postponementTime the time to defer arrivalTimes
	 *        
	 * @return true if all processes are FINISHED, o.w false
	 */

	protected void postponeAllProcesses(ArrayList<Process> processes, int postponementTime) {

		for (Process process : processes) {

			if (process.getProcessState() != Process.ProcessState.FINISHED) {

				process.setPostpone(postponementTime);

			}
		}
	}


	/**
	 * changeProcessState changes the ProcessState of a process
	 * 
	 * @param processes the ArrayList of processes to search
	 * @param p1 the process to search for
	 * @param state the state to change p1 to
	 *        
	 * @effects changes the state of p1 to state
	 */

	protected void changeProcessState(ArrayList<Process> processes, Process p1, Process.ProcessState state) {

		for (Process p2: processes) {
			if(p1.getProcessID() == p2.getProcessID()) {
				p2.setProcessState(state);

			}
		}

	}


	/**
	 * @param p
	 */

	protected void updateProcessStatus(Process p) {

		int n = p.setNextTimes();

		if (n < 0) {
			changeProcessState(processes, p, Process.ProcessState.FINISHED);
		} else {

			changeProcessState(processes, p, Process.ProcessState.READY);
			addToQueue(getReadyQueue(), p, new ProcessSortByArrivalTime());
		}
	}


	/**
	 * @param frames
	 * @param p
	 * @param start
	 */

	protected void useFrames(Frames frames, Process p, int start) {
		frames.use(p.getProcessID(), start, p.getFrames());
	}


	/**
	 * sort sorts the queue if on the simulation's algorithm
	 * depends on it.
	 * 
	 * @param q the queue
	 * @param c the comparator
	 * @effects sorts q based on c
	 */

	protected void sort(LinkedList<Process> q, Comparator<Process> c) {

		Collections.sort(q, c);

	}


}