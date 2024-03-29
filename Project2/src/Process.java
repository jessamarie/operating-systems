/**  
 *  @author Jessica Barre
 */


import java.util.Comparator;

/** 
 *  Processes is a class that represents a process
 * 
 *  <proc-id> <required-frames> <arrival-times> <tun-times>
 *    
 */

public class Process {

	/* Immutable variables  */

	private String processID;
	private int frames;
	private int[] arrivalTimes;
	private int[] runTimes;
	private int numRuns;


	/* Mutable variables that may need to be reinitialized, or changed */
	private int nextArrivalTime;
	private int nextRunTime;
	private int nthRun;
	private int postpone;

	private ProcessState processState;
	public enum ProcessState {
		NEW,
		READY,
		BLOCKED,
		RUNNING,
		FINISHED;
	}

	/**
	 * Constructor
	 */

	public Process(String processID, int frames, int[] arrivalTimes, int[] runTimes) {
		super();

		this.processID = processID;
		this.frames = frames;
		this.arrivalTimes = arrivalTimes;
		this.runTimes = runTimes;
		this.numRuns = arrivalTimes.length;

		init();

	}


	/**
	 * init resets a process back to it's original state
	 */

	public void init(){

		this.processState = ProcessState.NEW;
		this.nextArrivalTime = arrivalTimes[0];
		this.nextRunTime = runTimes[0];
		this.nthRun = 0;
		this.postpone = 0;

	}

	/**
	 * @return String representation of this process
	 */

	public String toString() {

		/* String firstHalf = getProcessID() + " " + getFrames();

		   String secondHalf = "";

		for (int i = 0; i < numRuns; i++) {
			secondHalf += " " + arrivalTimes[i] + "/" + runTimes[i]; 
		} */

		//return firstHalf + secondHalf;
		return getProcessID();
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
	 * @return the number of frames required
	 */

	public int getFrames() {
		return frames;
	}


	/**
	 * @return the arrivalTimes
	 */

	public int[] getArrivalTimes() {
		return arrivalTimes;
	}


	/**
	 * @return the runTimes
	 */

	public int[] getRunTimes() {
		return runTimes;
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

	public int getNextArrivalTime() {
		return nextArrivalTime + getPostpone();
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */	

	public void setNextArrivalTime(int nextArrivalTime) {
		this.nextArrivalTime = nextArrivalTime;
	}

	public int getPostpone() {
		return postpone;
	}


	public void setPostpone(int postpone) {
		this.postpone = postpone;
	}


	/**
	 * @return the nextRunTime
	 */	

	public int getNextRunTime() {
		return nextRunTime;
	}


	/**
	 * @param nextRunTime the nextRunTime to set
	 */	

	public void setNextRunTime(int nextRunTime) {
		this.nextRunTime = nextRunTime;
	}


	/**
	 * @return the returnTime
	 */

	public int getReturnTime() {
		return getNextArrivalTime() + getNextRunTime();
	}

	/**
	 * setNextTimes sets to next pair of arrival/run time
	 * @return -1 if no pairs left, o.w 0 if successful
	 **/

	public int setNextTimes() {

		int done;

		if ( getCurrentRun() == numRuns - 1) {
			done = -1;
		} else {

			setToNextRun();

			setNextArrivalTime(getArrivalTimes()[nthRun]);	
			setNextRunTime(getRunTimes()[nthRun]);	

			done = 0;

		}


		return done;

	}


	/**
	 * @return the nthRun
	 */

	public int getCurrentRun() {

		return nthRun;

	}


	/**
	 * @effects increments nthRun by 1
	 */

	public void setToNextRun() {
		nthRun++;
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

		if (p1.getNextArrivalTime() > p2.getNextArrivalTime()) {
			i = 1;
		}
		else if (p1.getNextArrivalTime() == p2.getNextArrivalTime()) {
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
