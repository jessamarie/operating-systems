package project1;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class Algorithm {
	
    protected List<Process> processes;
    protected ArrayList<Process> queue;
    
    public Algorithm(List<Process> processes) {
        super();
        this.processes = processes;
    }
        
    public abstract void run();
    // update current job by 1 tick
    // check if the job queue might need to be changed.
    // check for jobs to add to the queue
}