package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import junit.framework.TestCase;
import static datastructure.Common.*;

public class MergeSortTestCase extends TestCase {

	SinglyLinkedList<Integer> empty = new SinglyLinkedList<Integer>();
	SinglyLinkedList<Integer> one = new SinglyLinkedList<Integer>(1);
	SinglyLinkedList<Integer> two = new SinglyLinkedList<Integer>(1, 2);
	SinglyLinkedList<Integer> two2 = new SinglyLinkedList<Integer>(2, 1);
	SinglyLinkedList<Integer> three = new SinglyLinkedList<Integer>(4, 4, 4);	
	SinglyLinkedList<Integer> sorted = new SinglyLinkedList<Integer>(1, 2, 3);	
	SinglyLinkedList<Integer> someList = new SinglyLinkedList<Integer>(1000, 4, 2, 1, 3);
	SinglyLinkedList<Integer> withNull = new SinglyLinkedList<Integer>(1000, null, 2, 1, 3);
	
	
	@SuppressWarnings("unchecked")	
	SinglyLinkedList<SinglyLinkedList<Integer>> allLists = new SinglyLinkedList<SinglyLinkedList<Integer>>(
			empty, one, two, two2, three, sorted, someList
	);
	
	public void testSort() {
		/*
		t(two.sort(), Arrays.asList(1, 2));
		t(two2.sort(), Arrays.asList(1, 2));
		t(three.sort(), Arrays.asList(4, 4, 4));
		t(sorted.sort(), Arrays.asList(1, 2, 3));
		t(someList.sort(), Arrays.asList(1, 2, 3, 4, 1000));
		*/
		for(SinglyLinkedList<Integer> list : allLists) {
			List<Integer> copy = new ArrayList<Integer>(list);
			Collections.sort(copy);
			t(list.sort(), copy);
		}
	}
	
	public void testParallelSort() {		
		for(SinglyLinkedList<Integer> list : allLists) {
			List<Integer> copy = new ArrayList<Integer>(list);
			Collections.sort(copy);
			t(list.parallelSort(), copy);
		}
	}
	
	
	public void testSublistSort() {
		SinglyLinkedList<Integer> subway = someList.subList(1, 4);
		t(subway.size() == 3);
		t(subway.sort(), Arrays.asList(1, 2, 4));
		t(someList, Arrays.asList(1000, 1, 2, 4, 3));
		//
		SinglyLinkedList<Integer> sub2 = someList.subList(0, 1);
		sub2.remove(0);
		try {
			subway.checkInvariants(3);
			fail();
		}
		catch (ConcurrentModificationException expected) {}
	}		
	
	public void testSublistParallelSort() {
		SinglyLinkedList<Integer> subway = someList.subList(1, 4);
		t(subway.size() == 3);
		t(subway.parallelSort(), Arrays.asList(1, 2, 4));
		t(someList, Arrays.asList(1000, 1, 2, 4, 3));
		//
		SinglyLinkedList<Integer> sub2 = someList.subList(0, 1);
		sub2.remove(0);
		try {
			subway.checkInvariants(3);
			fail();
		}
		catch (ConcurrentModificationException expected) {}
	}	
	
	public void testNullPointer() {
		try {
			withNull.sort();
			fail();
		}
		catch (NullPointerException expected) {}
	}	
}
