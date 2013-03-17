package datastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Assert;

public class Common {

	
	static <E> void doTestIOBE(List<? extends List<E>> underTest
			                 , int upperOffset
			                 , Method op
			                 , Object... argument) 
	{
		for(List<E> lst : underTest) {
			List<Object> args = new LinkedList<Object>(Arrays.asList(argument));
			args.add(0, new Object());
			int[] invalidIndices = { -2, -1, 100, lst.size() + upperOffset};
			for(int index : invalidIndices) { 
				int size = lst.size();
				args.set(0, index);
				expectException(lst, op, IndexOutOfBoundsException.class, args.toArray());
				// verify invariants. Lists should remain operational after IOBE
				if(lst instanceof SinglyLinkedList<?>) { 
					((SinglyLinkedList<?>)lst).checkInvariants(size);
				}
				else {
					t(size == lst.size());
				}
			}
		}		
	}	
	
	static <E> void doTestIsEmpty(SinglyLinkedList<E> lst) {
		t(lst.isEmpty());
		t(lst.size() == 0);
		isExhausted(lst.listIterator(), lst.size());
		t(lst);
	}	
	
	static <T> void doTestIterator(SinglyLinkedList<T> lst, ListIterator<T> i, int offset, List<T> compareWith) {
		t(i.hasNext());
		t(i.nextIndex() == offset);
		t(i.previousIndex() == offset - 1);
		t(lst, compareWith);
		while(i.hasNext()) {
			T e = i.next();
			t(lst.areEqual(e, compareWith.get(offset++)));
			t(lst, compareWith);
		}
		t(!i.hasNext());
		t(i.nextIndex() == compareWith.size());
		t(i.previousIndex() == compareWith.size() - 1);
		t(lst, compareWith);
	}
		
	
	static <E> void isExhausted(ListIterator<E> i, int size) {		
		try { i.next(); t(false); } catch (NoSuchElementException nsee) {}
		t(i.nextIndex() == size);
		t(i.previousIndex() == size - 1);
		t(!i.hasNext());
	}
	
	static void expectException(Object o, Method m, Class<?> expected, Object... arg) {
		try {
			m.invoke(o, arg);
		}
		catch (InvocationTargetException ite) { 
			t(ite.getCause().getClass() == expected); 
		} 
		catch (Exception e) {
			throw new RuntimeException("reflection invocation failed", e);
		}		
	}
	
	static <E> void throwsCME(Object o, Method m, Object... arguments) {
		expectException(o, m, ConcurrentModificationException.class, arguments);
	}
	
	static <E> void doTestIteratorConcurrentModificationException(ListIterator<E> it, E arg) {
		try {
			throwsCME(it, ListIterator.class.getMethod("next"));
			throwsCME(it, ListIterator.class.getMethod("set", Object.class), arg);
			throwsCME(it, ListIterator.class.getMethod("remove"));
			throwsCME(it, ListIterator.class.getMethod("add", Object.class), arg);
			
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} 
	}	
	
	static void t(boolean condition) {
		Assert.assertTrue(condition);
	}
	
	static <E> void t(SinglyLinkedList<E> list, E... array) {
		t(list, Arrays.asList(array));
	}
	
	static <E> void t(SinglyLinkedList<E> list, List<E> expected) {
		list.checkInvariants(expected.size());
		if(expected instanceof SinglyLinkedList<?>)
			((SinglyLinkedList<?>)expected).checkInvariants(list.size());
		Assert.assertTrue(expected.equals(list));	
	}

	@SuppressWarnings("unused")
	private static void out(Object o) {
		System.out.println(o);
	}	
}
