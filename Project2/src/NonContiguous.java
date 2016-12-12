/**
 *  @author Jessica Barre
 * 
 */

import java.util.ArrayList;

/**
 * This class implements a Non-Contiguous Placement Algorithm
 * 
 * Overview of Non-Contiguous Algorithms:
 * 
 * To add a process to memory:
 *  - add process to each frame that is not
 *    in use. If there are not enough frames,
 *    skip the interval
 */ 



/**
 * This class implements the Non-Contiguous First-Fit Placement Algorithm
 * 
 */

public class NonContiguous extends Algorithm {


	/**
	 * Constructor
	 */

	public NonContiguous(ArrayList<Process> processes) {
		super(processes); 
	}


	/**
	 *{@inheritDoc}
	 *
	 * adds the process's frames to a non-contiguous place in memory.
	 * 
	 */

	@Override
	public void placeProcessIntoMemory(Frames frames, Process p) {

		useFrames(frames, p, 0);

		addToQueue(getWorkingQueue(), p, new ProcessSortByReturnTime());

		printInterestingEvent(p.getNextArrivalTime(), "Placed process "+ p.getProcessID() + ":");
	}

}
