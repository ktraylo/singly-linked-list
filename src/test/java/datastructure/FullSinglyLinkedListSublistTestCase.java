package datastructure;

import java.util.Arrays;


public class FullSinglyLinkedListSublistTestCase extends AbstractSinglyLinkedListTestCase {
	
	void allocateSingleLinkedLists() {
		list = new SinglyLinkedList<Integer>(Arrays.asList(44, 55)).subList(0, 0);
		oneElement = new SinglyLinkedList<Integer>(Arrays.asList(11, 12)).subList(1, 2);
		empty = new SinglyLinkedList<Integer>(Arrays.asList(44, 55)).subList(0, 0);		
	}
			
}
