package datastructure;

import java.util.Arrays;

public class EmptySinglyLinkedListSublistTestCase extends AbstractSinglyLinkedListTestCase {
	
	void allocateSingleLinkedLists() {
		list = new SinglyLinkedList<Integer>().subList(0, 0);
		oneElement = new SinglyLinkedList<Integer>(Arrays.asList(12, 11)).subList(0, 1);
		empty = new SinglyLinkedList<Integer>().subList(0, 0);		
	}			
}
