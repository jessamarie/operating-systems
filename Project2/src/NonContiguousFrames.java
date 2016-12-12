/** 
 *  WorstFitNonContFrames is an extension of Frames
 *  which is best suited for the Non-contiguous Worse-Fit 
 *  algorithm
 *  
 *  It contains the methods needed to 
 *  scan and change frames 
 *    
 */

public class NonContiguousFrames extends Frames{


	/**
	 * Constructor
	 */

	public NonContiguousFrames() {

		super();

	}


	/**
	 * useFrames
	 * 
	 * @returns the end position of the frame placement
	 */


	public int use(String str, int start, int blocksToFill) {

		int blocksFilled = 0;

		for (int i = start; i < getSize() && blocksFilled < blocksToFill; i++) {

			if (blocks.get(i) == ".") {
				blocks.set(i, str);
				blocksFilled++;

				this.setBusyFrames(this.getBusyFrames() + 1);
				this.setEmptyFrames(this.getEmptyFrames() - 1);
			}			

		}




		int endPosition;

		for (endPosition = start; endPosition < getSize() && blocksFilled < blocksToFill; endPosition++) {
			blocks.set(endPosition, str);
			blocksFilled++;

			this.setBusyFrames(this.getBusyFrames() + 1);
			this.setEmptyFrames(this.getEmptyFrames() - 1);

		}

		return endPosition;
	}

	/**
	 * clearFrames
	 * 
	 * @returns the end position of the frame placement
	 */


	public void clear(String str) {


		for (int i = 0; i < getSize(); i++) {

			if(blocks.get(i) == str) {
				blocks.set(i, ".");

				this.setBusyFrames(this.getBusyFrames() - 1);
				this.setEmptyFrames(this.getEmptyFrames() + 1);


			}
		}
	}





}
