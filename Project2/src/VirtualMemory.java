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

	public VirtualMemory(ArrayList<String> references) {
		this.references = references;
		frameSize = 3;

		init();
	}

	public void init() {
		this.pageFaults = 0;
	}

	/**
	 * doOPT simulates the Optimal memory management
	 * algorithm
	 * 
	 * opt selects it;s victim by identifying the frame
	 * that will be accessed the longest time in the future
	 * or not at all
	 */
	public void doOPT() {

		System.out.println("Simulating OPT with fixed frame size of " + frameSize);

		ArrayList<String> frames = new ArrayList<String>();
		HashMap<Integer,Integer> opt = new HashMap<Integer, Integer>();

		for (int i = 0; i < references.size(); i++) {
			opt.put(Integer.parseInt(references.get(i)), 0);
		}
		

		for (int i = 0; i < references.size(); i++) {
		
			String ref = references.get(i);
			
			List<String> subref = references.subList(i + 1, references.size());
						
			for (int s : opt.keySet()) {
				
				int next = subref.indexOf(Integer.toString(s));
				
				if(next == -1){
				  opt.put(s, Integer.MAX_VALUE);

				} else {
				  opt.put(s, next);
				}
			}

		

			if (frames.size() < frameSize) {

				if (!frames.contains(ref)){
					frames.add(ref);
					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (no victim page)");
				} /* else it's a hit */


			} else {

				if(!frames.contains(ref)) {

					/* remove the least frequently used frame */
									
					String victim = null;
					int max = -1;

					for (int f : opt.keySet()) {
						int k  = opt.get(f);

						if ( frames.contains(Integer.toString(f)) && k > max) {
							max = k;
							victim = Integer.toString(f);

						}
					}


					int lastIndex = frames.lastIndexOf(victim);
					
					int next = subref.indexOf(victim);
					
					if(next == -1){
					  opt.put(Integer.parseInt(victim), Integer.MAX_VALUE);

					} else {
					  opt.put(Integer.parseInt(victim), next);
					}

					frames.set(lastIndex, ref);

					// add to page fault

					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (victim page " + victim + ")");



				}



			}
		}


		System.out.println("End of OPT simulation (" + pageFaults + " page faults)");


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

		System.out.println("Simulating LRU with fixed frame size of " + frameSize);

		ArrayList<String> frames = new ArrayList<String>();
		LinkedHashSet<String> lru = new LinkedHashSet<String>(); 

		for (String ref : references) {

			if(!lru.contains(ref)) {
				lru.add(ref);
			} else {
				lru.remove(ref);
				lru.add(ref);
			}


			if (frames.size() < frameSize) {

				if (!frames.contains(ref)){
					frames.add(ref);
					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (no victim page)");
				} /* else it's a hit */


			} else {

				if(!frames.contains(ref)) {


					/* remove the least frequently used frame */

					Iterator<String> i = lru.iterator();

					String victim = i.next();
					int j = 0;
					while(j < lru.size()) {

						if(frames.contains(victim)) {
							break;
						}
						victim = i.next();
					} 

					int lastIndex = frames.lastIndexOf(victim);

					frames.set(lastIndex, ref);

					// add to page fault

					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (victim page " + victim + ")");



				}



			}
		}

		System.out.println("End of LRU simulation (" + pageFaults + " page faults)");
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


	/**
	 * doLFU simulates the Least-Frequently used memory management
	 * algorithm
	 * 
	 *   The Least-Freq used algorithm chooses the page
	 *   which has been used the least main memory 
	 *   to be replaced
	 */
	public void doLFU() {

		System.out.println("Simulating LFU with fixed frame size of " + frameSize);

		ArrayList<String> frames = new ArrayList<String>();
		HashMap<Integer,Integer> lfu = new HashMap<Integer, Integer>();

		for (String ref : references) {
			
			int refAsInt = Integer.parseInt(ref);

			if(!lfu.containsKey(refAsInt)) {
				lfu.put(refAsInt, 1);
			} else {
				lfu.put(refAsInt, lfu.get(refAsInt) + 1);
			}


			if (frames.size() < frameSize) {

				if (!frames.contains(ref)){
					frames.add(ref);
					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (no victim page)");
				} /* else it's a hit */


			} else {

				if(!frames.contains(ref)) {

					/* Reset ref's freq to 1*/
					lfu.put(refAsInt, 1);

					/* remove the least frequently used frame */
					String victim = null;
					int least = Integer.MAX_VALUE;

					for (int f : lfu.keySet()) {
						int i = lfu.get(f);
						
						if ( frames.contains(Integer.toString(f)) && i < least) {
							least = i;
							victim = Integer.toString(f);

						}
					}


					int lastIndex = frames.lastIndexOf(victim);

					frames.set(lastIndex, ref);

					// add to page fault

					pageFaults++;

					System.out.println("referencing page " + ref + " " + memoryToString(frames)
					+ " PAGE FAULT (victim page " + victim + ")");



				}



			}
		}

		System.out.println("End of LFU simulation (" + pageFaults + " page faults)");

	}

}
