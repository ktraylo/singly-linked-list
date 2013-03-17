package datastructure;

public class SingleLinkedListTestCase extends AbstractSinglyLinkedListTestCase {
	
	void allocateSingleLinkedLists() {	
		list = new SinglyLinkedList<Integer>();
		oneElement = new SinglyLinkedList<Integer>();
		oneElement.add(12);
		empty = new SinglyLinkedList<Integer>();	
	}
			
}
