package project1;

import java.util.Comparator;

/** 
 *  Processes is a class that represents a process
 * 
 *  <proc-id>|<initial-arrival-time>|<cpu-burst-time>|<num-bursts>|<io-time>
 *  A|0|168|5|287
 *    
 */

public class Process {

	/* Immutable variables  */

	private String processID;
	private int initalArrivalTime;
	private int cpuBurstTime;
	private int numBursts;
	private int ioTime;


	/* Mutable variables that may need to be reinitialized, or changed */

	private int arrivalTime;
	private int startTime;
	private int burstFinishTime;
	private int numBurstsLeft;
	private int returnTime;
	private int waitTime;
	private int turnAroundTime;

	private int workTimeLeft;
	private ProcessState processState;
	public enum ProcessState {
		NEW,
		READY,
		BLOCKED,
		RUNNING,
		FINISHED;
	}

	// Other Possibilites: workTimeLeft, timeTillArrival, currentTime



	/**
	 * Constructor
	 */

	public Process(String processID, int initalArrivalTime, int cpuBurstTime, int numBursts, int ioTime) {
		super();

		this.processID = processID;
		this.initalArrivalTime = initalArrivalTime;
		this.cpuBurstTime = cpuBurstTime;
		this.numBursts = numBursts;
		this.ioTime = ioTime;

		init();

	}


	/**
	 * init resets a process back to it's original state
	 */

	public void init(){

		this.processState = ProcessState.NEW;
		this.numBurstsLeft = numBursts;
		this.workTimeLeft = cpuBurstTime; 

		arrivalTime = initalArrivalTime;
		waitTime = 0;
		turnAroundTime = 0;
		startTime = 0;
		returnTime = 0;
		burstFinishTime = 0;

	}


	/*/*****************************************************************************************
	 *  Immutable variable get methods                                                                *
	 *******************************************************************************************/	

	/**
	 * @return the processID
	 */

	public String getProcessID() {
		return processID;
	}


	/**
	 * @return the initial arrival time
	 */

	public int getInitalArrivalTime() {
		return initalArrivalTime;
	}


	/**
	 * @return the cput burst time
	 */

	public int getCpuBurstTime() {
		return cpuBurstTime;
	}


	/**
	 * @return the number of bursts
	 */

	public int getNumBursts() {
		return numBursts;
	}


	/**
	 * @return the io time
	 */

	public int getIoTime() {
		return ioTime;
	}


	/*/*****************************************************************************************
	 *  mutable variable get/set methods                                                                *
	 *******************************************************************************************/

	/**
	 * @return the processState
	 */

	public ProcessState getProcessState() {
		return processState;
	}


	/**
	 * @param processState the processState to set
	 */	

	public void setProcessState(ProcessState processState) {
		this.processState = processState;
	}


	/**
	 * @return the arrivalTime
	 */	

	public int getArrivalTime() {
		return arrivalTime;
	}


	/**
	 * @param arrivalTime the arrivalTime to set
	 */	

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	/**
	 * @return the startTime
	 */

	public int getStartTime() {
		return startTime;
	}


	/**
	 * @param startTime the startTime to set
	 */

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}


	/**
	 * @return the finishTime
	 */

	public int getBurstFinishTime() {
		return burstFinishTime;
	}


	/**
	 * @param finishTime the finishTime to set
	 */

	public void setBurstFinishTime(int finishTime) {
		this.burstFinishTime = finishTime;
	}


	/**
	 * @return the elapsedTime plus the cpuBurstTime
	 */

	public int getNumCurrentBurst() {
		return numBurstsLeft;
	}


	/**
	 * @param numBurstsLeft the numBurstsLeft to set
	 */

	public void setNumBursts(int numBurstsLeft) {
		this.numBurstsLeft = numBurstsLeft;
	}


	/**
	 * @return the returnTime
	 */

	public int getReturnTime() {
		return returnTime;
	}


	/**
	 * @param returnTime the returnTime to set
	 */

	public void setReturnTime(int returnTime) {
		this.returnTime = returnTime;
	}


	/**
	 * @return the waitTime
	 */

	public int getWaitTime() {
		return waitTime;
	}


	/**
	 * @param waitTime the waitTime to set
	 */

	public void setWaitTime(int time) {
		this.waitTime = time;
	}


	/**
	 * @return the turnAroundTime
	 */

	public int getTurnAroundTime() {
		return turnAroundTime;
	}


	/**
	 * @param turnAroundTime the turnAroundTime to set
	 */

	public void setTurnAroundTime(int turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}


	/**
	 * @return the workTimeLeft
	 */

	public int getWorkTimeLeft() {
		return workTimeLeft;
	}


	/**
	 * @param workTimeLeft the workTimeLeft to set 
	 */

	public void setWorkTimeLeft(int time) {
		this.workTimeLeft = time;
	}

	/**
	 * @return String representation of this process
	 */

	public String toString() {
		return getProcessID();
		//return getProcessID() + "|" + getInitalArrivalTime() + "|" 
		//		+ getCpuBurstTime() + "|" + getNumBursts() + "|" + getIoTime(); 
	}  

}


/**
 * The following classes define different ways to sort a collection of processes
 */


/**
 * ProcessSortByArrivalTime sorts a Collection of processes first by
 * initialArrivalTime, then by processID
 */

class ProcessSortByArrivalTime implements Comparator<Process>{

	@Override
	public int compare(Process p1, Process p2) {

		int i = 0;

		if (p1.getInitalArrivalTime() > p2.getInitalArrivalTime()) {
			i = 1;
		}
		else if (p1.getInitalArrivalTime() == p2.getInitalArrivalTime()) {
			i = 0;
		}
		else {
			i = -1;
		}

		if (i != 0) {
			return i;
		} else {
			return p1.getProcessID().compareTo(p2.getProcessID());
		}

	}
}


/**
 * ProcessSortByCPUBurstTime sorts a Collection of processes first by
 * cpuBurst, then by initialArrivalTime, then by processID
 */

class ProcessSortByCPUBurstTime implements Comparator<Process>{

	@Override
	public int compare(Process p1, Process p2) {

		int i = 0;

		if (p1.getCpuBurstTime() > p2.getCpuBurstTime()) {
			i = 1;
		}
		else if (p1.getCpuBurstTime() == p2.getCpuBurstTime()) {
			i = 0;
		}
		else {
			i = -1;
		}

		if (i != 0) {
			return i;
		} else {
			ProcessSortByArrivalTime p = new ProcessSortByArrivalTime();
			return p.compare(p1, p2);
			//			return p1.getProcessID().compareTo(p2.getProcessID());
		}

	}
}


/**
 * ProcessSortByReturnTime sorts a Collection of processes first by
 * returnTime, then by processID
 */

class ProcessSortByReturnTime implements Comparator<Process>{

	@Override
	public int compare(Process p1, Process p2) {

		int i = 0;

		if (p1.getReturnTime() > p2.getReturnTime()) {
			i = 1;
		}
		else if (p1.getReturnTime() == p2.getReturnTime()) {
			i = 0;
		}
		else {
			i = -1;
		}

		if (i != 0) {
			return i;
		} else {
			return p1.getProcessID().compareTo(p2.getProcessID());
		}

	}
}
