package project2.tests;
/** 
 *  Frames is a class which represents a set of memory units.
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
 */

public class Frames {

	String[][] block;
	int rows;
	int columns;
	int framesPerLine;




	/**
	 * Constructor
	 */

	public Frames() {
		super();

		this.rows = 8;
		this.columns = 32;



		init(rows, columns);

	}


	/**
	 * init resets the frames back to it's original state
	 */

	public void init(int rows, int columns){

		block = new String[rows][columns];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				block[i][j] = ".";
			}
		}
	}

	/**
	 * toString is a string representation of the current
	 * state of the frames
	 */
	public String toString() {

		String divider = "================================\n";
		String frames = "";

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				frames += block[i][j];
			}

			frames += "\n";
		}

		return divider + frames + divider;
	}

	/*
	public void scanForNextFit(int startRow, int startCol, int blocksToFill) {
		
		int i, j, fitRow, fitCol;
		
		for (i = startRow + 1; i < rows && blocksFilled < blocksToFill; i++) {
			for (j = 0; j < columns && blocksFilled < blocksToFill; j++) {
				block[i][j] = str;
				blocksFilled++;
			}
		}
		
		

	} */

	public void changeFrames(String str, int startRow, int startCol, int blocksToFill) {

		int blocksFilled = 0;

		int i = startRow;
		int j = startCol;

		for (j = startCol; j < columns && blocksFilled < blocksToFill; j++) {
			block[i][j] = str;
			blocksFilled++;
		}

		for (i = startRow + 1; i < rows && blocksFilled < blocksToFill; i++) {
			for (j = 0; j < columns && blocksFilled < blocksToFill; j++) {
				block[i][j] = str;
				blocksFilled++;
			}
		}
	}



}
