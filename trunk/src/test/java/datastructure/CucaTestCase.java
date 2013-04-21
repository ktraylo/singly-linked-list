package datastructure;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

import junit.framework.TestCase;

import static datastructure.Common.t;
import static datastructure.Common.doTestIOBE;


public class CucaTestCase extends TestCase {
	

	SinglyLinkedList<Integer> list = new SinglyLinkedList<Integer>(Arrays.asList(0, 1, 2, 3, 4));
	
	<T extends Comparable<T>> void merge(ListIterator<T> one, ListIterator<T> two) {
		if(one.hasNext() && two.hasNext()) {
			T a = one.next(), b = two.next();
			do {
				if(a.compareTo(b) <= 0) {
					if(!one.hasNext()) break;
					a = one.next();
				}
				else {
					one.add(b); two.remove();
					if(!two.hasNext()) break;					
					b = two.next();
				}
			}
			while(true);
		}
		// drain what's left from j
		while(two.hasNext()) {
			one.add(two.next()); two.remove();
		}		
	}
	
	
	public void testTwoSublists() {
		List<Integer> one = list.subList(0, 2);
		List<Integer> two = list.subList(3, 5);
		try {
			one.add(0, 11);
			two.add(0, 22);
			fail();
		}
		catch (ConcurrentModificationException cme) {
			// expected
		}
	}
	
}
