package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import static datastructure.Common.t;

public class CucaTestCase extends TestCase {
	

	final SinglyLinkedList<Integer> list1 = new SinglyLinkedList<Integer>(Arrays.asList(1, 2, 3, 4));
	final SinglyLinkedList<Integer> list2 = new SinglyLinkedList<Integer>(Arrays.asList(2, 4, 1, 3));
	final SinglyLinkedList<Integer> list3 = new SinglyLinkedList<Integer>(Arrays.asList(3, 4, 1, 2));
	final SinglyLinkedList<Integer> list4 = new SinglyLinkedList<Integer>(Arrays.asList(1, 4, 2, 3));
	
	final List<Integer> empty = new ArrayList<Integer>();
	
	
	// assumption one and two are adjocent sublist or the one.end() == two.first();
    // and once the end of one is reached, the merging is over
	<T extends Comparable<T>> void merge(SinglyLinkedList<T>.ForwardOnlyIterator one, SinglyLinkedList<T>.ForwardOnlyIterator two) {
		if(one.hasNext() && two.hasNext()) {
			T a = one.next(), b = two.next();
			for(;;) {
				if(a.compareTo(b) < 0) {
					if(!one.hasNext()) {
						break; // must append what's left from two to one but the lists are concatenated anyways
					}
					a = one.next();
				}
				else {
					one.insertBefore(b);
					two.remove();
					if(!two.hasNext()) 
						break;					
					b = two.next();
				}
			}
		}
	}
	
	void out(Object o) { System.out.println(o); }
	
	public void testTheTest() {
		mergeHalfs(list1);
		mergeHalfs(list2);
		mergeHalfs(list3);
	}
	
	void mergeHalfs(SinglyLinkedList<Integer> list) {
		
		SinglyLinkedList<Integer> one = list.subList(0, list.size() / 2);
		SinglyLinkedList<Integer> two = list.subList(2, list.size());
		//merge(two.forwardOnlyIterator(), one.forwardOnlyIterator());
		//merge(one.forwardOnlyIterator(), two.forwardOnlyIterator());
				
		out("The list:");
		for(Integer i : list)
			out(i);
		t(list, Arrays.asList(1, 2, 3, 4));
	}
		
	/*
	public void testMerge1() {
		SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(Arrays.asList(1, 2));
		SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(Arrays.asList(3, 4));;
		merge(one.forwardOnlyIterator(), two.forwardOnlyIterator());
		t(one, Arrays.asList(1, 2, 3, 4));
		t(two, empty);
	}	
	
	public void testMerge2() {
		SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(Arrays.asList(1, 2));
		SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(Arrays.asList(3, 4));;
		merge(two.forwardOnlyIterator(), one.forwardOnlyIterator());
		t(two, Arrays.asList(1, 2, 3, 4));
		t(one, empty);
	}

	public void testMerge3() {
		SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(Arrays.asList(1, 3));
		SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(Arrays.asList(2, 4));;
		merge(two.forwardOnlyIterator(), one.forwardOnlyIterator());
		t(two, Arrays.asList(1, 2, 3, 4));
		t(one, empty);
	}
	
	public void testMerge4() {
		SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(Arrays.asList(2, 4));
		SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(Arrays.asList(1, 3));;
		merge(two.forwardOnlyIterator(), one.forwardOnlyIterator());
		t(two, Arrays.asList(1, 2, 3, 4));
		t(one, empty);
	}
	
	public void testMerge5() {
		SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(Arrays.asList(2, 4));
		SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(Arrays.asList(1, 3));;
		merge(two.forwardOnlyIterator(), one.forwardOnlyIterator());
		t(two, Arrays.asList(1, 2, 3, 4));
		t(one, empty);
	}
	*/
	
}
