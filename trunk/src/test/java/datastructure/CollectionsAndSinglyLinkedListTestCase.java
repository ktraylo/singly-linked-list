package datastructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import junit.framework.TestCase;


public class CollectionsAndSinglyLinkedListTestCase extends TestCase {
	
	
	SinglyLinkedList<Integer> list = new SinglyLinkedList<Integer>();
	List<Integer> sample = Arrays.asList(10, 11, 12);
	
	@Override public void setUp() {
		for(int i = 0; i < 10000; ++i)
			list.add(i);
	}
	
	
	public void testReverse() {
		SinglyLinkedList<Integer> other = list.clone();
		other.reverse();
		Collections.reverse(list);
		Common.t(other, list);		
	}
	
	
	void out(Object o) { System.out.println(o); }
			
}
