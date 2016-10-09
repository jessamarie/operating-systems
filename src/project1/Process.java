package project1;

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
	private ProcessState processState;
	private enum ProcessState {
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
	}

	public String getProcessID() {
		return processID;
	}

	public int getInitalArrivalTime() {
		return initalArrivalTime;
	}

	public int getCpuBurstTime() {
		return cpuBurstTime;
	}

	public int getNumBursts() {
		return numBursts;
	}

	public int getIoTime() {
		return ioTime;
	}
	
	public ProcessState getProcessState() {
		return processState;
	}
	
	public void setProcessState(ProcessState ps) {
		this.processState = ps;
	}
	
    /**
     * Returns a String representation of this object
     * 
     * @return String representation of this process
     */
    public String toString() {
    	 return getProcessID() + "|" + getInitalArrivalTime() + "|" 
                + getCpuBurstTime() + "|" + getNumBursts() + "|" + getIoTime();
    	 
    }

}
