package datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
		for(SinglyLinkedList<Integer> list : allLists) {
			List<Integer> copy = new ArrayList<Integer>(list);
			Collections.sort(copy);
			t(list.sort(), copy);
		}
	}
	
	public void testSublist() {
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
	
	public void testNullPointer() {
		try {
			withNull.sort();
			fail();
		}
		catch (NullPointerException expected) {}
	}
	
	public void testPerformance() {
		LinkedList<Integer> dlist = new LinkedList<Integer>();
		populate(dlist, 100000);
		SinglyLinkedList<Integer> slist = new SinglyLinkedList<Integer>(dlist);
		ArrayList<Integer> alist = new ArrayList<Integer>(dlist);
		long now = System.nanoTime();
		Collections.sort(dlist);
		now = System.nanoTime() - now;
		out("dlist time:" + now / 1000 / 1000);
		//
		now = System.nanoTime();
		slist.sort();		
		now = System.nanoTime() - now;
		out("slist time:" + (now / 1000 / 1000));		
		//dump(slist);
		//dump(dlist);
		now = System.nanoTime();
		Collections.sort(alist);		
		now = System.nanoTime() - now;
		out("alist time:" + (now / 1000 / 1000));		

		
		t(slist, dlist);
		
	}

	void populate(List<Integer> list, final int size) {
		Random r = new Random();
		for(int i = 0; i < size; ++i)
			list.add(r.nextInt());
	}
	
}
