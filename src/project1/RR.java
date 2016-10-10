package project1;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class RR extends Algorithm{

	public RR(List<Process> processes) {
		super(processes);
	}

	@Override
	public void run() {
		
	}
	
	public void run(ArrayList<Process> processes, Statistics rr) {
		
	}


	public void printInterestingEvent(int t, String details, Queue<Process> q) {
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
