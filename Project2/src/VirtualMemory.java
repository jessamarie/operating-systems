/**  
 *  @author Jessica Barre
 *  @name:  Project2.java (main class)
 *  Date Due: Dec 12, 2016
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
public class VirtualMemory {

	ArrayList<String> references;

	private int pageFaults;
	private int frameSize;
	private int size;

	ArrayList<String> frames;

	public VirtualMemory(ArrayList<String> references) {

		this.references = references;

		this.frameSize = 3;
		this.size = references.size();
		this.frames = new ArrayList<String>();

		init();
	}

	public void init() {
		this.pageFaults = 0;
		frames.clear();
	}

	/**
	 * doOPT simulates the Optimal memory management
	 * algorithm
	 * 
	 * opt selects its victim by identifying the frame
	 * that will be accessed the longest time in the future
	 * or not at all
	 */

	public void doOPT() {

		HashMap<Integer,Integer> opt = new HashMap<Integer, Integer>();

		startSim("OPT");


		/* Initialize a hashmap with one of every page contained
		 * in the reference string */

		for (int i = 0; i < size; i++) {
			opt.put(Integer.parseInt(references.get(i)), 0);
		}


		/* iterate through the reference string */

		for (int i = 0; i < size; i++) {

			String ref = references.get(i);


			/* Track how far ahead each page is from the current
			 * location i */

			List<String> subref = references.subList(i + 1, size);

			for (int s : opt.keySet()) {

				int next = subref.indexOf(Integer.toString(s));

				if(next == -1){
					opt.put(s, Integer.MAX_VALUE);

				} else {
					opt.put(s, next);
				}
			}


			/* Things are done differently for the
			 * first 3 page faults
			 */

			if (frames.size() < frameSize) {


				/* If page fault */

				if (!frames.contains(ref)){

					frames.add(ref);
					pageFaults++;

					printWithNoVictim(ref);

				} /* else it's a hit */


			} else {

				/* If page fault */

				if(!frames.contains(ref)) {


					/* remove the page that is furthest ahead
					 * in the reference string */

					String victim = null;

					int max = -1;

					for (int f : opt.keySet()) {

						int k  = opt.get(f);

						if ( frames.contains(Integer.toString(f)) && k > max) {
							max = k;
							victim = Integer.toString(f);

						}
					}


					/* Find the position of this victim in the
					 * frames and replace it with the new ref */

					int lastIndex = frames.lastIndexOf(victim);

					frames.set(lastIndex, ref);


					/* increase by one page fault */

					pageFaults++;

					printWithVictim(ref, victim);

				}

			}

		} /* end for */

		endSim("OPT");

	}


	/**
	 * doLRU simulates the Least-Recently used memory management algorithm
	 * 
	 * The Least-Recently used algorithm chooses the page
	 * which has not been used for the longest time in main memory 
	 * to be replaced
	 *
	 */

	public void doLRU() {

		LinkedHashSet<String> lru = new LinkedHashSet<String>(); 

		startSim("LRU");


		/* iterate through the references */

		for (String ref : references) {


			/* use a LinkedHashSet to keep track of the least
			 * recently used page -- it will be the first 
			 * page in the set */

			if(!lru.contains(ref)) {
				lru.add(ref);
			} else {
				lru.remove(ref);
				lru.add(ref);
			}


			/* Things are done differently for the
			 * first 3 page faults
			 */

			if (frames.size() < frameSize) {


				/* If page fault */

				if (!frames.contains(ref)){

					frames.add(ref);
					pageFaults++;

					printWithNoVictim(ref);

				} /* else it's a hit */


			} else {

				/* If page fault */

				if(!frames.contains(ref)) {


					/* find the least recently used frame 
					 * in the lru set -- the first element
					 * that is crossed which is contained 
					 * in the frames list, hence the break */

					Iterator<String> i = lru.iterator();

					String victim = i.next();
					int j = 0;
					while(j < lru.size()) {

						if(frames.contains(victim)) {
							break;
						}
						victim = i.next();
					} 

					/* Find the position of this victim in the
					 * frames and replace it with the new ref */

					int lastIndex = frames.lastIndexOf(victim);

					frames.set(lastIndex, ref);


					/* increase by one page fault */

					pageFaults++;

					printWithVictim(ref, victim);

				}

			}
			
		}/* end for */

		endSim("LRU");
	}

	/**
	 * doLFU simulates the Least-Frequently used memory management
	 * algorithm
	 * 
	 *   The Least-Freq used algorithm chooses the page
	 *   which has been used the least main memory 
	 *   to be replaced
	 */
	public void doLFU() {
		
		HashMap<Integer,Integer> lfu = new HashMap<Integer, Integer>();
		
		startSim("LFU");
		

		/* iterate through the references */

		for (String ref : references) {
			
			
			/* use a hashmap to keep track of the least frequently
			 * used page, which will be the page (key) with the
			 * lowest number (value) */

			int refAsInt = Integer.parseInt(ref);

			
			/* if not yet in lfu, add new key with value 1, 
			 * else add to the current value */
			
			if(!lfu.containsKey(refAsInt)) {
				
				lfu.put(refAsInt, 1);
				
			} else {
				
				lfu.put(refAsInt, lfu.get(refAsInt) + 1);
			
			}

			
			/* Things are done differently for the
			 * first 3 page faults
			 */
			
			if (frames.size() < frameSize) {
				
				
				/* If page fault */

				if (!frames.contains(ref)){
					
					frames.add(ref);
					pageFaults++;

					printWithNoVictim(ref);

				} /* else it's a hit */


			} else {
		
				
				/* If page fault */

				if(!frames.contains(ref)) {

					/* Reset ref's frequency to 1*/
					lfu.put(refAsInt, 1);
					
					/* find the least frequently used frame 
					 * in the lfu map -- which with be the one
					 * with the lowest number */

					String victim = null;
					int least = Integer.MAX_VALUE;

					for (int f : lfu.keySet()) {
						
						int i = lfu.get(f);

						if ( frames.contains(Integer.toString(f)) && i < least) {
						
							least = i;
							
							victim = Integer.toString(f);

						}
					}


					/* remove the least frequently used frame and
					 * replace with the new reference */

					int lastIndex = frames.lastIndexOf(victim);

					frames.set(lastIndex, ref);


					/* increase by one page fault */

					pageFaults++;

					printWithVictim(ref, victim);

				}

			}
			
		} /* end for */

		endSim("LFU");

	}

	private void startSim(String string) {
		System.out.println("Simulating " + string + " with fixed frame size of " + frameSize);

	}

	private void printWithVictim(String ref, String victim) {
		System.out.println("referencing page " + ref + " " + memoryToString(frames)
		+ " PAGE FAULT (victim page " + victim + ")");

	}

	private void printWithNoVictim(String ref) {
		System.out.println("referencing page " + ref + " " + memoryToString(frames)
		+ " PAGE FAULT (no victim page)");
	}

	private void endSim(String string) {
		System.out.println("End of " + string + " simulation (" + pageFaults + " page faults)");

	}

	public String memoryToString(ArrayList<String> frames) {

		String str = "[mem:";

		int i = frames.size();


		for (String f : frames) {
			str += " " + f;
		}

		while ( i < frameSize ) {
			str += " " + ".";
			i++;
		}

		str += "]";

		return str;

	}

}
