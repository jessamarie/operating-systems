import java.util.ArrayList;

/** 
 *  Frames is a class which represents a set of memory units.
 *  
 * ================================
 * AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
 * AAAAAAAAAAAAA...................
 * ................................
 * ................................
 * ................................
 * ................................
 * ................................
 * ................................
 * ================================
 * 
 * It is represented by and ArrayList of size n, where rows*columns = n
 *    
 */

public class Frames {

	ArrayList<String> blocks;
	private int rows;
	private int columns;
	private int busyFrames;
	private int emptyFrames;




	/**
	 * Constructor
	 */

	public Frames() {
		super();

		blocks = new ArrayList<String>();
		this.rows = 8;
		this.columns = 32;

		init();

	}


	/**
	 * init resets the frames back to it's original state
	 */

	public void init(){

		this.setBusyFrames(0);
		this.setEmptyFrames(getSize());

		for (int i = 0; i < getSize(); i++) {
			blocks.add(".");
		}
	}

	/**
	 * toString is a string representation of the current
	 * state of the frames
	 */
	public String toString() {

		String divider1 = "================================\n";
		String divider2 = "================================";

		String frames = "";

		int position = 0;

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				frames += blocks.get(position);
				position++;
			}

			frames += "\n";
		}

		return divider1 + frames + divider2;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}

	public int getSize() {
		return getRows()*getColumns();
	}


	/**
	 * scanForNextFit returns the starting position of
	 * the next possible fit or -1 if it's not possible
	 */

	public int scanForNextFit(int start, int blocksToFill) {

		int i, j;

		int fillableBlocks = 0;
		int endPosition = 0;

		/* start from the last fit process */
		for (i = start; i < getSize() && fillableBlocks < blocksToFill; i++) {


			if ( blocks.get(i) == "." ) {
				fillableBlocks++;
				endPosition = i;

			} else {
				fillableBlocks = 0;
			}

		}

		/* continue from beginning if there is no space found */
		for (i = 0; i < start && fillableBlocks < blocksToFill; i++) {


			if ( blocks.get(i) == "." ) {
				fillableBlocks++;
				endPosition = i;

			} else {
				fillableBlocks = 0;
			}

		}

		int startPosition = -1;
		if (fillableBlocks == blocksToFill) {
			startPosition = endPosition - blocksToFill + 1;
		}

		return startPosition;


	} 

	/**
	 * vchangeFrames
	 */


	public int changeFrames(String str, int start, int blocksToFill) {

		int blocksFilled = 0;

		int i;

		for (i = start; i < getSize() && blocksFilled < blocksToFill; i++) {
			blocks.set(i, str);
			blocksFilled++;


			/* change busyFrames and emptyFrames */ 	
			if(str == ".") {
				this.setBusyFrames(this.getBusyFrames() - 1);
				this.setEmptyFrames(this.getEmptyFrames() + 1);
			} else {
				this.setBusyFrames(this.getBusyFrames() + 1);
				this.setEmptyFrames(this.getEmptyFrames() - 1);
			}

		}

		return i;
	}


	/**
	 * defrag first removes all of the "." in the blocks ArrayList
	 * and then appends "." at the end of the ArrayList until the
	 * size of the Arraylist = rows*columns
	 */

	public void defrag() {

		for (int i = 0; i < blocks.size(); i++) {

			if(blocks.get(i) == ".") {
				blocks.remove(i);				
				i = -1; /* Since the placement of all things shift left,
				           we need to restart the loop */
			}

		}

		for (int i = blocks.size(); i < getSize(); i++) {
			blocks.add(".");
		}


	}


	/**
	 * @return the busyFrames
	 */
	public int getBusyFrames() {
		return busyFrames;
	}


	/**
	 * @param busyFrames the busyFrames to set
	 */
	public void setBusyFrames(int busyFrames) {
		this.busyFrames = busyFrames;
	}


	/**
	 * @return the emptyFrames
	 */
	public int getEmptyFrames() {
		return emptyFrames;
	}


	/**
	 * @param emptyFrames the emptyFrames to set
	 */
	public void setEmptyFrames(int emptyFrames) {
		this.emptyFrames = emptyFrames;
	}



}
