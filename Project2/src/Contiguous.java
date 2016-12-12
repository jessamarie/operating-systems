
import java.util.ArrayList;

/**
 * This class implements a Contiguous Placement Algorithm
 * 
 * Overview of Contiguous Algorithms:
 * 
 * To add a process to memory:
 *  - use proper scanning algorithm
 *  - if not enough contiguous memory, but enough frames,
 *    then defrag and try again, ow skip interval
 */ 

public class Contiguous extends Algorithm {

	
	/**
	 * Constructor
	 */

	public Contiguous(ArrayList<Process> processes) {
		super(processes);
	}


	/**
	 *{@inheritDoc}
	 *
	 * scans for a position to put the process into memory. If there
	 * is enough space, but not a big enough partition, a defragmentation
	 * occurs. If there is not enough space, the interval is skipped and
	 * the process may, or may not be marked finished.
	 */

	@Override
	public void placeProcessIntoMemory(Frames frames, Process p) {

		int start = scan(frames, p.getFrames());

		if (start < 0) {

			/* There are enough frames, just not enough contingious
			 * memory. A defrag is needed */

			defrag(frames, p);

			start = scan(frames, p.getFrames());
		} 

		useFrames(frames, p, start);

		addToQueue(getWorkingQueue(), p, new ProcessSortByReturnTime());

		printInterestingEvent(p.getNextArrivalTime(), "Placed process "+ p.getProcessID() + ":");
	}


	/**
	 * scan
	 * 
	 * sends the frames through the scanning algorithm as
	 * defined in a Frames Class
	 * 
	 * @param frames the frames to scan
	 * @param framesRequired the number of frames needed
	 * 
	 */	
	private int scan(Frames frames, int framesRequired) {

		int start = frames.scan(frames.getScanPosition(),framesRequired);

		return start;
	}


	/**
	 * defrag 
	 * 
	 * performs a defrag on a Frames object.
	 * 
	 * @param frames
	 * @param p
	 */

	private void defrag(Frames frames, Process p) {

		printInterestingEvent(p.getNextArrivalTime(), "Cannot place process "
				+ p.getProcessID() + " -- starting defragmentation");

		String defragged = frames.defrag();

		int postponementTime = frames.getNumDefraggedFrames();

		postponeAllProcesses(processes, postponementTime);

		setElapsedTime(getElapsedTime() + postponementTime);

		printInterestingEvent(p.getNextArrivalTime(), 
				"Defragmentation complete (moved "+ postponementTime + 
				" frames: " + defragged + ")");

		System.out.println(frames.toString());
	}


}
