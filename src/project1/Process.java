package project1;

import java.util.Comparator;

/** This class defines processes of the form 
 *  <proc-id>|<initial-arrival-time>|<cpu-burst-time>|<num-bursts>|<io-time>
 *  A|0|168|5|287
 *    
 *  **/

public class Process {

	private String processID;
	private int initalArrivalTime;
	private int cpuBurstTime;
	private int numBursts;
	private int ioTime;
	
	private int waitTime;
	private int turnAroundTime;
	
	private int startTime = 0;
	private int finishTime = 0;
	private int currentBurst;
	private int workTimeLeft;
	
	// Other Possibilites: workTimeLeft, timeTillArrival, currentTime
		
	private ProcessState processState;
	public enum ProcessState {
		NEW,
		READY,
		BLOCKED,
		RUNNING,
		FINISHED;
	}

	public Process(String processID, int initalArrivalTime, int cpuBurstTime, int numBursts, int ioTime) {
		super();
		this.processID = processID;
		this.initalArrivalTime = initalArrivalTime;
		this.cpuBurstTime = cpuBurstTime;
		this.numBursts = numBursts;
		this.ioTime = ioTime;
		this.workTimeLeft = cpuBurstTime; 
		
		this.processState = ProcessState.NEW;
		this.currentBurst = numBursts;
	}
	

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
	 * @return the work time left
	 */
	public int getWorkTimeLeft() {
		return workTimeLeft;
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
	
	
	/**
	 * @return the process wait time state
	 */
	public int getWaitTime() {
		return waitTime;
	}
	
	/**
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(int startTime) {
		this.waitTime = waitTime + startTime;
	}
	
	/**
	 * @return the process wait time state
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
	 * @param workTimeLeft the workTimeLeft to set 
	 */
	public void setWorkTimeLeft(int time) {
		this.workTimeLeft = time;
	}
	
	
	/**
	 * @return the finishTime
	 */
	public int getFinishTime() {
		return finishTime;
	}


	/**
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
	
	/**
	 * @return the elapsedTime plus the cpuBurstTime
	 */
	
	public int getCurrentBurst() {
		return currentBurst;
	}

	/**
	 * @effects subtracts one from the number of bursts
	 */
	public void decrementNumBursts() {
		this.currentBurst = this.currentBurst - 1;
	}
	
	
	/**
	 * @return the elapsedTime plus the cpuBurstTime
	 */
	public void addCPUBurst(int elapsedTime){
		finishTime += cpuBurstTime;
	}
	
	/**
	 * @return the elapsedTime plus the ioTime
	 */
	public void addIOBurst(int elapsedTime){
		finishTime += ioTime;
	}
	
	
    /**
     * @return String representation of this process
     */
	
    public String toString() {
    	 return getProcessID() + "|" + getInitalArrivalTime() + "|" 
                + getCpuBurstTime() + "|" + getNumBursts() + "|" + getIoTime(); 
    }    
    
}


/**
 * @effects Compares processes by arrival time
 * 
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
