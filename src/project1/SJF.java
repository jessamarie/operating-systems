package project1;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class SJF extends Algorithm{

	public SJF(List<Process> processes) {
		super(processes);
	}

	@Override
	public void run() {
		
	}
	
	public void run(ArrayList<Process> processes, Statistics sjf) {
		
	}
	
	public void printInterestingEvent(int t, String details, Queue<Process> q){
		System.out.println("time " + t + "ms: " + details + " [Q" + q.toString() + "]");
	}

}
