package datastructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import junit.framework.TestCase;

import static datastructure.Common.t;
import static datastructure.Common.doTestIOBE;


public abstract class AbstractSinglyLinkedListTestCase extends TestCase {
	
	SinglyLinkedList<Integer> list ;
	SinglyLinkedList<Integer> oneElement;
	SinglyLinkedList<Integer> empty;

	//
	List<Integer> reference = new LinkedList<Integer>();
	List<Integer> noElements = Collections.emptyList();
	List<Integer> fewElements = Arrays.asList(0, 1, 2, 3, 4);
	List<List<Integer>> allLists;
	
	
	
	@Override public void setUp() {	
		//
		allocateSingleLinkedLists();
		initAllLists();
		 //		
		list.checkInvariants(0);
		oneElement.checkInvariants(1);
		empty.checkInvariants(empty.size());
	}
	
	/** allocate {@code list}, {@code oneElement} and {@code empty} lists */
	abstract void allocateSingleLinkedLists();
	
	@SuppressWarnings("unchecked") void initAllLists() {
		allLists = Arrays.asList(list, oneElement, empty, reference);		
	}
	
	public void testEmpty() {
		Common.doTestIsEmpty(list);		
	}
	
	public void testAdd() {	
		for(int i = 10; i >= 0; --i) {
			list.add(i); reference.add(i);
			t(list, reference);
		}
    }
		
	public void testAddAtIndex() throws NoSuchMethodException {		
		// test IOBE with empty lists
		doTestIOBE(allLists, 1, List.class.getMethod("add", int.class, Object.class), 444);
		// insert elements at various locations
		int[] values =  { 12,  4, 0, 5, -3, 3, 2, 1, 44 };
		int[] atIndex  = { 0,  1, 0, 2,  4, 1, 0, 3, 2 };
		for(int i = 0; i < values.length; ++i) {
			list.add(atIndex[i], values[i]);
			reference.add(atIndex[i], values[i]);
			t(list, reference);
		}
		// test IOBE with populated lists
		doTestIOBE(allLists, 1, List.class.getMethod("add", int.class, Object.class), 444);
	}
		
	public void testAddAllAtIndex() throws NoSuchMethodException  {		
		// check IOBE
		doTestIOBE(allLists, 1, List.class.getMethod("addAll", int.class, Collection.class), fewElements);
		//
		t(!list.addAll(0, noElements));
		list.checkInvariants(0);
		for(int index : new int[] { 0, fewElements.size(), 0, 3 }) {
			list.addAll(index, fewElements);
			reference.addAll(index, fewElements);
			t(list, reference);
		}
		// IOBE with non empty list
		doTestIOBE(allLists, 1, List.class.getMethod("addAll", int.class, Collection.class), fewElements);
	}

	public void testAddAll() {
		t(!list.addAll(noElements));
		t(list);
		list.add(12);
		reference.add(12);
		t(list.addAll(fewElements));
		reference.addAll(fewElements);
		t(list, reference);
	}

	public void testReverse() {
		t(empty.reverse());
		t(oneElement.reverse(), 12);
		t(list.addAll(fewElements));
		t(list, fewElements);
		Collections.reverse(fewElements);
		t(list.reverse(), fewElements);
	}
	// check IOBE
	public void testRemoveByIndex() throws NoSuchMethodException {
		// check IOBE with empty list
		doTestIOBE(allLists, 0, List.class.getMethod("remove", int.class));
		//
		list.add(1);
		list.remove(0);
		t(list);
		//
		list.addAll(fewElements);
		reference.addAll(fewElements);
		//  check IOBE with non empty list
		doTestIOBE(allLists, 0, List.class.getMethod("remove", int.class));
		//
		for(int index : new int[] {reference.size() - 1, 0, 1, 0, 0 }) {			
			list.remove(index); 
			reference.remove(index); 
			t(list, reference);
		}
		//
		t(list);
	}
	
	// iterator tests	
	public void testEmptyIterator() {
		ListIterator<Integer> i = list.listIterator();
		Common.isExhausted(i, 0);
		t(list);
	}
	
	public void testIterator() {
		t(list.addAll(fewElements));
		Common.doTestIterator(list, list.listIterator(), 0, fewElements);
	}
		
	public void testIteratorFromIndex() throws NoSuchMethodException {
		//
		ListIterator<Integer> it = empty.listIterator(0);
		Common.isExhausted(it, empty.size());
		//
		doTestIOBE(allLists, 1, List.class.getMethod("listIterator", int.class));
		Common.isExhausted(list.listIterator(list.size()), 0);
		//
		t(list.addAll(fewElements));
		Common.isExhausted(list.listIterator(list.size()), list.size());
		Common.doTestIterator(list, list.listIterator(2), 2, fewElements);
	}	
		
	public void testIteratorRemove() {
		try { list.listIterator().remove(); t(false); } catch (IllegalStateException ise) {}		
		list.addAll(fewElements);
		reference.addAll(fewElements);
		ListIterator<Integer> els = list.listIterator();
		try { els.remove(); t(false); } catch (IllegalStateException ise) {}
		//
		t(els.next() == 0);
		t(els.next() == 1);
		els.remove(); t(els.nextIndex() == 1); reference.remove(1);
		t(list, reference);
		t(els.next() == 2);
		els.remove(); reference.remove(1);
		t(els.next() == 3);		
		t(list, reference);
	}
	
	public void testIteratorEmptyAdd() {
		ListIterator<Integer> it = empty.listIterator();
		for(int i = 0; i < fewElements.size(); ++i) it.add(fewElements.get(i));
		t(!it.hasNext());
		t(empty, fewElements);
	}

	public void testIteratorAdd() {
		list.addAll(fewElements);
		reference.addAll(fewElements);
		ListIterator<Integer> it = list.listIterator();
		it.add(-1); reference.add(0, -1);
		t(list, reference);
		t(it.next() == 0);
		t(it.next() == 1);
		it.add(-44); reference.add(3, -44);
		t(it.next() == 2);
		t(list, reference);
		t(it.next() == 3);
		t(it.next() == 4);
		t(!it.hasNext());
		it.add(124); reference.add(124);
		t(list, reference);
		t(!it.hasNext());
	}
	
	public void testClear() {
		empty.clear();
		t(empty);
		for(int i = 0; i < 2; ++i) {
			list.addAll(fewElements);
			list.clear();
			Common.doTestIsEmpty(list);
		}		
	}
		
	public void testIteratorConcurrentModificationException() {
		ListIterator<Integer> it = list.listIterator();
		list.add(12);
		Common.doTestIteratorConcurrentModificationException(it, 222);
		it = list.listIterator();
		list.addAll(fewElements);
		Common.doTestIteratorConcurrentModificationException(it, 222);
		it = list.listIterator();
		list.remove(2);
		Common.doTestIteratorConcurrentModificationException(it, 222);
		it = list.listIterator();
		list.remove((Object)0);
		Common.doTestIteratorConcurrentModificationException(it, 222);
		it = list.listIterator();
		list.reverse();
		Common.doTestIteratorConcurrentModificationException(it, 222);
	}
	
	public void testSelfAddAll() {
		list.addAll(fewElements);
		try {
			t(list.addAll(list) & false);
		}
		catch (ConcurrentModificationException cme) {}
		list.checkInvariants(list.size()); // list is still good for use after this attempt
	}
	
	
	public void testRemoveObject() {		
		t(!list.remove((Object)2));
		t(list);
		list.addAll(Arrays.asList(0, 1, 1, 2, 1, 3));
		t(list.remove((Object)1));
		t(list, 0, 1, 2, 1, 3);
		t(list.remove((Object)1));
		t(list, 0, 2, 1, 3);
		t(list.remove((Object)1));
		t(list, 0, 2, 3);
		t(list.remove((Object)0));
		t(list, 2, 3);	
		t(!list.remove((Object)0));
		t(list.remove((Object)3));
		t(list, 2);	
		t(list.remove((Object)2));
		t(list);
		t(list.add(2));
		t(list, 2);
	}
	
	//
	public void testSetGet() throws NoSuchMethodException {
		doTestIOBE(allLists, 0, List.class.getMethod("set", int.class, Object.class), 12);
		t(list.addAll(fewElements));
		reference.addAll(fewElements);
		doTestIOBE(allLists, 0, List.class.getMethod("set", int.class, Object.class), 12);
		//
		t(list.set(0, 5) == 0); 
		t(reference.set(0, 5) == 0);
		t(list.get(0) == 5);
		t(list.set(list.size() - 1, 44) == 4); 
		t(list.get(list.size()-1) == 44);
		t(reference.set(reference.size() - 1, 44) == 4);
		t(list.set(3, 14) == 3);
		t(reference.set(3, 14) == 3);
		t(list, reference);
	}
			
	public void testIndexOf() {
		t(empty.indexOf(5) == -1);
		list.addAll(fewElements);
		t(list.indexOf(5)== -1);
		int v = 0;
		for(int e : list)
			t(v++ == e);
	}
		
	public void testLastIndexOf() {
		t(list.lastIndexOf(0) == -1);
		list.addAll(Arrays.asList(0, 1, 1, 2, 1, 3));
		t(list.lastIndexOf(0) == 0);
		t(list.lastIndexOf(1) == 4);
		t(list.lastIndexOf(3) == 5);
		t(list.lastIndexOf(5) == -1);
		t(list, Arrays.asList(0, 1, 1, 2, 1, 3));
	}

	public void testClone() {
		SinglyLinkedList<Integer> clone = empty.clone();
		t(empty != clone);
		t(clone, empty);
		//
		list.addAll(fewElements);
		clone = list.clone();
		t(list != clone);
		t(clone, list);
	}
	
	public void testSerialize() throws IOException, ClassNotFoundException {
		list.addAll(fewElements);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(list);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		assertEquals(list, ois.readObject());
	}	
}
