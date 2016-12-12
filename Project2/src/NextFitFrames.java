/** 
 *  NextFitFrame is an extension of Frames
 *  which is best suited for the Next-Fit 
 *  algorithm
 *  
 *  It contains the methods needed to 
 *  scan and change frames 
 *    
 */

public class NextFitFrames extends Frames{

	/**
	 * Constructor
	 */

	public NextFitFrames() {

		super();

	}



	/**
	 * {@inheritDoc}
	 * 
	 * scans from the end of the most recently placed process
	 * 
	 */

	public int scan(int start, int framesRequired) {

		int i;
	
		int fillableBlocks = 0;
		int endPosition = 0;

		/* start from the last fit process */
		for (i = start; i < getSize() && fillableBlocks < framesRequired; i++) {


			if ( blocks.get(i) == "." ) {
				fillableBlocks++;
				endPosition = i;

			} else {
				fillableBlocks = 0;
			}			

		}

		if (fillableBlocks < framesRequired) {
			
			fillableBlocks = 0;

			/* continue from beginning if there is no space found */
			for (i = 0; i < getSize() && fillableBlocks < framesRequired; i++) {


				if (blocks.get(i) == "." ) {
					fillableBlocks++;
					endPosition = i;

				} else {
					fillableBlocks = 0;
				}

			}
		}



		int startPosition = -1;
		if (fillableBlocks == framesRequired) {
			startPosition = endPosition - framesRequired + 1;
		}

		return startPosition;

	}

	/**
	 * {@inheritDoc} 
	 * 
	 * For next-fit this is the position of the last placed process
	 */

	@Override
	public int getScanPosition() {
		return getPositionOfLastPlaced();
	}

}
