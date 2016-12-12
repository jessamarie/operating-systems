/** 
 *  BestFitFrame is an extension of Frames
 *  which is best suited for the Best-Fit 
 *  algorithm
 *  
 *  It contains the methods needed to 
 *  scan and change frames 
 *    
 */

public class BestFitFrames extends Frames{

	Frames frames;

	/**
	 * Constructor
	 */

	public BestFitFrames() {

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

		int startPosOfBestFit = Integer.MAX_VALUE;
		int sizeofBestFit = Integer.MAX_VALUE;

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

					if(sizeOfCurrFit >= framesRequired && sizeOfCurrFit < sizeofBestFit){
						startPosOfBestFit = startPosOfCurrFit;
						sizeofBestFit = sizeOfCurrFit;

						sizeOfCurrFit = 0;
						newPartitionFound = false;

					} else {
						sizeOfCurrFit = 0;
					}
				}
			}

		}	

		if(sizeOfCurrFit >= framesRequired && sizeOfCurrFit < sizeofBestFit){
			startPosOfBestFit = startPosOfCurrFit;
			sizeofBestFit = sizeOfCurrFit;

		}


		if (sizeofBestFit < framesRequired || sizeofBestFit > getSize()) {
			startPosOfBestFit = -1;
		}

		return startPosOfBestFit;


	}


}
