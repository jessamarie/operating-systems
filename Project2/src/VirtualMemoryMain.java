import java.io.IOException;
import java.util.ArrayList;

public class VirtualMemoryMain {
	
	public static void main(String args[]) {
		
		
		ArrayList<String> references = new ArrayList<String>();
		
		
		try {
			Project2.readReferences(args[0], references);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* Append memory placement algorithms to the end */
		
		VirtualMemory vm = new VirtualMemory(references);
		
		vm.doOPT();
		
		vm.init();
		
		System.out.println();
		
		vm.doLRU();
		
		vm.init();
		
		System.out.println();
		
		vm.doLFU();
		
	}

}
