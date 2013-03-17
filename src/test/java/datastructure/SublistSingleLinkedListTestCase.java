package datastructure;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

import static datastructure.Common.t;

import junit.framework.TestCase;

/**
 * 
 * The sublist and its parent should preserve their invariants.
 *
 * @author alexander.georgiev@gmail.com
 */
public class SublistSingleLinkedListTestCase extends TestCase {
	
	SinglyLinkedList<Integer> list = new SinglyLinkedList<Integer>();
	SinglyLinkedList<Integer> oneElement = new SinglyLinkedList<Integer>();
	SinglyLinkedList<Integer> empty = new SinglyLinkedList<Integer>();

	List<Integer> reference = new ArrayList<Integer>();
	List<Integer> noElements = Collections.emptyList();
	List<Integer> fewElements = Arrays.asList(0, 1, 2, 3, 4);

	
	@SuppressWarnings("unchecked")
	List<List<Integer>> allLists = Arrays.asList(list, oneElement, empty, reference);
	
	
	@Override public void setUp() {
		list.checkInvariants(0);
		oneElement.checkInvariants(0);
		empty.checkInvariants(empty.size());
		oneElement.add(12);
		oneElement.checkInvariants(1);
	}
				
	public void testEmptySublist() {
		Common.doTestIsEmpty(empty.subList(0, 0));
		//
		list.addAll(fewElements);
		for(int i = 0; i <= list.size(); ++i)
			Common.doTestIsEmpty(list.subList(i, i));
	}

	public void testSubListIOBE() {
		list.addAll(fewElements);
		for(List<Integer> lst : allLists) {
			sublistIOBE(lst, -1, -2);
			sublistIOBE(lst, lst.size() + 1, lst.size() + 1);
			sublistIOBE(lst, lst.size(), lst.size() - 1);
		}
	}
	
	<E> void sublistIOBE(List<E> lst, int from, int to) {
		try { t(lst.subList(from, to) == null); } 
			catch (IndexOutOfBoundsException iobe) {}
			catch (IllegalArgumentException iae) { t(to < from); }
		if(lst instanceof SinglyLinkedList<?>) ((SinglyLinkedList<?>)lst).checkInvariants(lst.size());
	}
		
	public void testSubListClearAll() {
		list.addAll(fewElements);
		SinglyLinkedList<Integer> sub = list.subList(0, list.size());
		sub.clear();
		t(sub);
		t(list);
		sub.addAll(fewElements);
		t(sub, fewElements);
		t(list, fewElements);
	}
	
	public void testSubListAddToEmpty() {
		SinglyLinkedList<Integer> sub = list.subList(0, 0);
		sub.add(2);
		t(sub, 2);
		t(list, 2);
		list.remove((Object)2);
		try {
			sub.get(0);
		} 
		catch (ConcurrentModificationException cme) {}
	}
	
	public void testSubListRemove() {
		
		list = new SinglyLinkedList<Integer>(fewElements);
		SinglyLinkedList<Integer> sub = list.subList(1, 4);
		t(list, 0, 1, 2, 3, 4);
		t(sub, 1, 2, 3);
		//
		sub.remove(1);
		list.checkInvariants(4);
		t(sub, 1, 3);
		//t(list, 0, 1, 3, 4);
		sub.add(1, 12);
		t(sub, 1, 12, 3);
		//
		//list.subList(1, 3).reverse();
	}
	
	<E> void doTestConcurrentModificationException(SinglyLinkedList<E> sll) {
		try {
			Common.throwsCME(sll, List.class.getMethod("add", Object.class), 12);
			Common.throwsCME(sll, List.class.getMethod("addAll", Collection.class), noElements);
			Common.throwsCME(sll, List.class.getMethod("remove", Object.class), 12);
			Common.throwsCME(sll, List.class.getMethod("iterator"));
			Common.throwsCME(sll, List.class.getMethod("listIterator"));
			
			

			//Common.throwsCME(it, ListIterator.class.getMethod("set", Object.class), 222);
			//Common.throwsCME(it, ListIterator.class.getMethod("remove"));
			//Common.throwsCME(it, ListIterator.class.getMethod("add", Object.class), 222);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}	
	
	
	public void testConcurrentModificationException() {
		SinglyLinkedList<Integer> sub = list.subList(0, 0);
		list.add(12);
		doTestConcurrentModificationException(sub);		
		sub = list.subList(0, 0);
		list.addAll(fewElements);
		doTestConcurrentModificationException(sub);
		sub = list.subList(0, 1);
		list.remove(2);
		doTestConcurrentModificationException(sub);
		sub = list.subList(list.size(), list.size());
		list.remove((Object)0);
		doTestConcurrentModificationException(sub);
		sub = list.subList(0, 0);
		ListIterator<Integer> it = sub.listIterator();
		list.reverse();
		Common.doTestIteratorConcurrentModificationException(it, 22);
		
	}	
	
	
}
