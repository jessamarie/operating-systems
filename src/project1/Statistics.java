package project1;

import java.text.DecimalFormat;

/** 
 * 
 * This class makes generating output easier. This output is of the form:
 *
 */

public class Statistics {

	private String type;
	private double avgBurstTime;
	private double avgWaitTime;
	private double avgTurnAroundTime;
	private int totalNumContextSwitches;
	private int totalNumPreemptions;

	
	/** Empty Constructor **/
	public Statistics() {
		
	}
	
	/** Non-Empty Constructor **/
	public Statistics(String type, double avgBurstTime, double avgWaitTime, double avgTurnAroundTime,
			int totalNumContextSwitches, int totalNumPreemptions) {
		super();
		this.type = type;
		this.avgBurstTime = avgBurstTime;
		this.avgWaitTime = avgWaitTime;
		this.avgTurnAroundTime = avgTurnAroundTime;
		this.totalNumContextSwitches = totalNumContextSwitches;
		this.totalNumPreemptions = totalNumPreemptions;
	}
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public double getAvgBurstTime() {
		return avgBurstTime;
	}


	public void setAvgBurstTime(double avgBurstTime) {
		this.avgBurstTime = avgBurstTime;
	}


	public double getAvgWaitTime() {
		return avgWaitTime;
	}


	public void setAvgWaitTime(double avgWaitTime) {
		this.avgWaitTime = avgWaitTime;
	}


	public double getAvgTurnAroundTime() {
		return avgTurnAroundTime;
	}


	public void setAvgTurnAroundTime(double avgTurnAroundTime) {
		this.avgTurnAroundTime = avgTurnAroundTime;
	}


	public int getTotalNumContextSwitches() {
		return totalNumContextSwitches;
	}


	public void setTotalNumContextSwitches(int totalNumContextSwitches) {
		this.totalNumContextSwitches = totalNumContextSwitches;
	}


	public int getTotalNumPreemptions() {
		return totalNumPreemptions;
	}


	public void setTotalNumPreemptions(int totalNumPreemptions) {
		this.totalNumPreemptions = totalNumPreemptions;
	}

	
    /**
     * 
     * Returns a String representation of this object
     * 
     * @return String representation of the statistics
     */
    public String toString() {
    	
    	DecimalFormat df = new DecimalFormat("#.00"); 

    	 return 
         "Algorithm " + getType() + "\n" +
         "-- average CPU burst time: " + df.format(getAvgBurstTime()) + " ms" + "\n" +
    	 "-- average wait time: " + df.format(getAvgWaitTime()) + " ms" + "\n" +
    	 "-- average turnaround time: " + df.format(getAvgTurnAroundTime()) + " ms" + "\n" +
    	 "-- total number of context switches: " + getTotalNumContextSwitches() + "\n" +
    	 "-- total number of preemptions: " + getTotalNumPreemptions();
    	 
    }

}
