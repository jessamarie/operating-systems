/** 
 *  WorstFitContFrames is an extension of Frames
 *  which is best suited for the Contiguous Worse-Fit 
 *  algorithm
 *  
 *  It contains the methods needed to 
 *  scan and change frames 
 *    
 */

public class WorstFitFrames extends Frames{

	/**
	 * Constructor
	 */

	public WorstFitFrames() {

		super();

	}

	/**
	 * {@inheritDoc}
	 * 
	 * scans from the beginning to the end of memory
	 * for a place which wastes the minimum amount of spaces
	 * 
	 * @param start the index to start from
	 * @param framesRequired the number of memory blocks to fill
	 */

	public int scan(int start, int framesRequired) {

		int i;

		int startPosOfWorstFit = -1;
		int sizeofWorstFit = -1;

		int startPosOfCurrFit = 0;
		int sizeOfCurrFit = 0;

		boolean newPartitionFound = false;

		/* start from the last fit process */
		for (i = start; i < getSize(); i++) {

			if ( blocks.get(i) == "." ) {

				if (newPartitionFound == false) {

					newPartitionFound = true;
					startPosOfCurrFit = i;
					sizeOfCurrFit = 1; 

				} else {
					sizeOfCurrFit++;
				}

			} else {
				if (newPartitionFound == true) {

					if(sizeOfCurrFit >= framesRequired && sizeOfCurrFit > sizeofWorstFit){
						startPosOfWorstFit = startPosOfCurrFit;
						sizeofWorstFit = sizeOfCurrFit;


					}

					sizeOfCurrFit = 0;
					newPartitionFound = false;
				}
			}

		}	

		if(sizeOfCurrFit >= framesRequired && sizeOfCurrFit > sizeofWorstFit){
			startPosOfWorstFit = startPosOfCurrFit;
			sizeofWorstFit = sizeOfCurrFit;

		}
		

		if (sizeofWorstFit < framesRequired || sizeofWorstFit > getSize()) {
			startPosOfWorstFit = -1;
		}

		return startPosOfWorstFit;

	}

}
