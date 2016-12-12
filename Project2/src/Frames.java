import java.util.ArrayList;
import java.util.HashSet;

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
	private int numDefraggedFrames;
	private int positionOfLastPlaced;

	/**
	 * Constructor
	 */

	public Frames() {

		blocks = new ArrayList<String>();
		this.rows = 8;
		this.columns = 32;

		this.busyFrames = 0;
		this.emptyFrames = this.rows * this.columns;
		this.numDefraggedFrames = 0;
		this.positionOfLastPlaced = 0;

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
	 * use
	 * 
	 * @returns the end position of the frame placement
	 */


	public int use(String str, int start, int blocksToFill) {

		int blocksFilled = 0;

		int endPosition;

		for (endPosition = start; endPosition < getSize() && blocksFilled < blocksToFill; endPosition++) {
			blocks.set(endPosition, str);
			blocksFilled++;

			this.setBusyFrames(this.getBusyFrames() + 1);
			this.setEmptyFrames(this.getEmptyFrames() - 1);

		}

		setPositionOfLastPlaced(endPosition);


		return endPosition;
	}

	/**
	 * clear
	 * 
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


	/**
	 * defrag first removes all of the "." in the blocks ArrayList
	 * and then appends "." at the end of the ArrayList until the
	 * size of the Arraylist = rows*columns
	 * 
	 * @returns a string containing the processes defraged
	 */

	public String defrag() {

		setNumDefraggedFrames(0);

		String processes = "";
		ArrayList<String> al = new ArrayList<>();

		/* Don't defragment untill you reach a "." */
		int j = 0;
		while (j < getSize() && blocks.get(j) != ".") {
			j++;
		}

		for (int i = j; i < blocks.size(); i++) {

			if(blocks.get(i) == ".") {

				blocks.remove(i);	

				i = j - 1; /* Since the placement of all things shift left,
				           we need to restart the loop */
			} else {
				al.add(blocks.get(i));
			}
		}

		for (int i = blocks.size(); i < getSize(); i++) {
			blocks.add(".");
		}

		setNumDefraggedFrames(getBusyFrames() - j);


		/* Remove duplicates in al */

		HashSet<String> hs = new HashSet<>();
		hs.addAll(al);
		al.clear();
		al.addAll(hs);

		processes += al.get(0);
		for (int i = 1; i < al.size(); i++) {
			processes += ", " + al.get(i);

		}

		processes = processes.trim();

		return processes;


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

	/**
	 * @return the numDefraggedFrames
	 */
	public int getNumDefraggedFrames() {
		return numDefraggedFrames;
	}

	/**
	 * @param numDefraggedFrames the numDefraggedFrames to set
	 */
	public void setNumDefraggedFrames(int numDefraggedFrames) {
		this.numDefraggedFrames = numDefraggedFrames;
	}

	/**
	 * @return the positionOfLastPlaced
	 */
	public int getPositionOfLastPlaced() {
		return positionOfLastPlaced;
	}

	/**
	 * @param positionOfLastPlaced the positionOfLastPlaced to set
	 */
	public void setPositionOfLastPlaced(int positionOfLastPlaced) {
		this.positionOfLastPlaced = positionOfLastPlaced;
	}

	/**
	 * getScanPosition
	 * @return the position to scan from
	 */
	public int getScanPosition() {
		return 0;
	}

	/**
	 * scan
	 * @param position the position to scan from
	 * @param frames the memory set
	 * @return the starting position of the place to add 
	 *         the process to, o.w -1 if there is not a 
	 *         large enough sized partition
	 */
	public int scan(int position, int frames) {
		// TODO Auto-generated method stub
		return 0;
	}



}
