
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Algorithm {

	protected ArrayList<Process> processes;
	protected LinkedList<Process> readyQueue;   /* process is ready to use the CPU */
	protected LinkedList<Process> blockedQueue;  /* process state blocked during IO */

	protected int elapsedTime;
	protected int m;                        /* default number of processes available in the CPU */
	protected int t_cs;					  /* default time to context switch */
	protected int loadTime;
	protected int unloadTime;

	public Algorithm(ArrayList<Process> processes) {
		super();
		this.processes = processes;
	}

	/** 
	 * The run method runs the processes based on the desired algorithms. 
	 */

	public abstract void run();

	/** 
	 *  transferArrivingProcesses adds new processes to the readyQueue 
	 *  if their arrival times have reached the elapsed time 
	 *  
	 *  @effects adds processes to the readyQueue
	 */

	public abstract void transferArrivingProcesses();


	/**
	 * checkIfReadyToReturn checks if the blockedQueue has any elements 
	 * left to return
	 * 
	 * @effects sends any elements that are ready to be returned to
	 *          the readyQueue
	 * @return true if there were elements to return, and o.w false
	 */

	//public abstract boolean removeFinishedProcesses();


	/**
	 * returnToReady returns a process to the readyQueue if it's return time
	 * is less than the elapsed time
	 * 
	 * @effects removes a process from blockedQueue and adds to readyQueue
	 *          changes state to READY
	 */

	public abstract void returnToReady(Process p);


	/**
	 * sort sorts the readyQueue if on the simulation's algorithm
	 * depends on it.
	 * 
	 * @param rq
	 * @effects sorts rq
	 */
	protected abstract void sort(LinkedList<Process> rq);


	/**
	 * loadProcess removes the first element of the readyQueue and
	 * changes it's state to RUNNING
	 * 
	 * @return p the process to be loaded
	 * @effects changes the state of P to running and adds the load time
	 *          to the elapsed time
	 */

	public abstract Process loadProcess();

	/**
	 * performBurst performs a cpu burst
	 * 
	 * @param p the process currently running
	 * @effects performs a cpu burst and changes waitTime, TurnAroundTime, 
	 *          numBursts, burstFinishTime, elapsedTime, numContextSwitches,
	 *          and totalCPUBurstTime
	 */

	public abstract void performBurst(Process p);


	/**
	 * terminateProcess is called when a process has zero bursts left to process.
	 * The process is summarized and then it's state is changed to finished.
	 * 
	 * @param p the process to terminate
	 * @effects changes the state of p to finished, then adds it's wait time to totalWaitTime, 
	 *          and adds it's turnAroundTime to totalTurnAroundTime
	 */

	public abstract void terminateProcess(Process p);

	/**
	 * moveToBlocked moves a process to the blockedQueue
	 * 
	 * @param p the process to be moved to block
	 * @effects changes the state of p to BlOCKED, sets it's return time,  and adds it to the blockedQueue.
	 *          The blocked queue is also sorted by return time to ensure that processes return to the 
	 *          readyQueue in the correct order.
	 *          
	 */

	public abstract void moveToBlocked(Process p);


	/**
	 * @param t the current elapsed time
	 * @param details the interesting details of the event
	 * @param processes the queue to print
	 */

	protected void printInterestingEvent(int t, String details) {
		
		System.out.println("time " + t + "ms: " + details);
		

		/*if (!processes.isEmpty()) {

			System.out.print("time " + t + "ms: " + details + " [Q");

			for (Process p : processes) {
				System.out.print(" " + p.getProcessID());

			}

			System.out.println("]");

		} else {
			System.out.println("time " + t + "ms: " + details + " [Q empty]");
		}
		*/


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


}