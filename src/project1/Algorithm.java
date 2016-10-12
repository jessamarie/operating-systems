package project1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

	protected int numContextSwitches;
	protected double totalCPUBurstTime;
	protected double totalWaitTime;
	protected double totalTurnAroundTime;
    
    public Algorithm(ArrayList<Process> processes) {
        super();
        this.processes = processes;
    }
        
    public abstract void run();
    
    
	/**
	 * @param t the current elapsed time
	 * @param details the interesting details of the event
	 * @param q the queue to print
	 */
    
	protected void printInterestingEvent(int t, String details, Queue<Process> q) {

		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");

	}

}