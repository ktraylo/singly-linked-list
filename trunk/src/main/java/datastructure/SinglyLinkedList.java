package datastructure;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSequentialList;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Singly linked implementation of the {@link List} interface.
 * 
 * Due to its singly linked nature, only forward iteration operations are
 * supported. Iterators produced by {@link #listIterator()} and
 * {@link #listIterator(int)} methods will throw
 * {@link UnsupportedOperationException} if backward traversal methods
 * {@link ListIterator#hasPrevious()} or {@link ListIterator#previous()} are
 * invoked.
 * <p>
 * The singly linked list implements {@link RandomAccess} to prevent various
 * {@link Collections} methods from invoking the above two methods. Thus the
 * {@link Collections} methods remain operational at the expense of 
 * computation time, as indexing operations {@link #get(int)} and
 * {@link #set(int, Object)} always traverse from the beginning the list and
 * consume linear time. Therefore it is better to use implementation's
 * custom methods like {@link #reverse()} instead of 
 * {@link Collections#reverse(List)}.
 * <p>
 * Permits {@code null} elements.
 * <p>
 * Iterators and sub-lists returned by {@link #iterator()},
 * {@link #listIterator()} and {@link #subList(int, int)} methods are
 * <i>fail-fast</i>: if the list is structurally modified at any time after the
 * iterator or the sub-list is created, in any way except through the iterator's
 * or sub-list's own {@code remove} or {@code add} methods, the
 * iterator/sub-list will throw a {@link ConcurrentModificationException}. Thus,
 * in case of a concurrent modification performed in the same thread, the
 * iterator/sub-list will fail quickly and cleanly, preserving class invariants.
 * <p>
 * The class is thread unsafe. The class invariants and fail-fast behavior of
 * iterator/sub-list cannot be guaranteed in case of a multithreaded concurrent 
 * modification. Use the synchronised wrapper 
 * {@link Collections#synchronizedList(List)} to achieve thread safety.
 * 
 * @author alexander.georgiev@gmail.com
 * 
 * @param <E>
 *            the type of the elements held in the list.
 */
public final class SinglyLinkedList<E> extends AbstractSequentialList<E> implements Cloneable, Serializable, RandomAccess {
	
	private static final long serialVersionUID = 1L;

	private static class Node<T> {
		T e;
		Node<T> next;
		Node(T e, Node<T> next) {
			this.e = e;
			this.next = next;
		}
		@Override public String toString() { // to facilitate debugging
			return e + " -> " + (next == null ? "next is null" : next.e );
		}
	}
	
	transient private int modCount = 0; // to intercept concurrent modifications
	transient private int size = 0;
	transient private final Node<E> head; // points to the first element in the list
	transient private final Node<E> end;  // List's last element always points to 'end'. 
	                                      // As the same implementation is used to implement sublists a
										  // sentinel reference to hold the end of the list is required.
	
	transient private final Node<E> tail; // points to the last element in the list to speed up operation "append"
	transient private SinglyLinkedList<E> parent = this; // if this is a sublist, keeps a reference to the parent list
		
	private Node<E> begin() { return head.next; } // stl style accessors
	private Node<E> end() { return end; }
	
	
	/** 
	 * To be used in unit tests - verifies the class' invariants:
	 * 
	 * <li>calculated size == number of elements
	 * <li>head -> first
	 * <li>tail -> last <==> tail.next.next == end
	 * <li>head->end - starting from head one can reach end and the number of nodes 
	 * between is equal to the <tt>expectedSize</tt>
	 * 
	 * @param expectedSize expected size of the list to be compared with the real size of the list
	 * @throws IllegalStateException if any invariant is violated
	 * @throws IllegalArgumentException if <tt>expectedSize</tt> is less than zero.
	 */
	void checkInvariants(int expectedSize) {
		if(expectedSize < 0) throw new IllegalArgumentException();
		if(parent != this)
			parent.checkInvariants(parent.size()); // better than nothing
		//
		checkForComodification();
		t(size == expectedSize);
		//
		final boolean empty = (begin() == end() & isEmpty());
		t(size == 0 ? empty : !empty);
		//
		t(tail.next.next == end());
		// count the nodes between begin() and end()
		int nNodes = 0;
		Node<E> c = head;
		while(tail.next != c) {
			++nNodes; c = c.next;
			t(nNodes <= size); // looping should traverse no more than size() - 1 elements
		}
		t(c.next == end);
		t(nNodes == size);
	}
	
	// everybody up the chain should have the same modifications count
	private void checkForComodification() {
		if(parent != this) {
 			parent.checkForComodification();
 			if(parent.modCount != modCount) {
				throw new ConcurrentModificationException(
					"sublist has a different modcount " + modCount 
				  + " from parent: " + parent.modCount
				);
 			}
		}
 	}
	
	private void incrementModCount() {
		++modCount;
		if(parent != this)
			parent.incrementModCount();
	}
	
	private void changeSize(int with) {
		size += with;
		if(parent != this)
			parent.changeSize(with);
	}
	
	/** Creates an empty list */
	public SinglyLinkedList() {
		head = new Node<E>(null, null);
		tail = new Node<E>(null, null);
		end = head;		  
		head.next = end;  // because begin() always points to the first and when list's empty begin() == end()
		tail.next = head; // because teail.next == last() is always the node before the "end"	
		size = 0;
	}
	
	/**
	 * Creates a list populated with elements.
	 * @param elements to populate with
	 */
	public SinglyLinkedList(E... elements) {
		this();
		for(E e : elements)
			add(e);
	}
		
	/**
    * Constructs a list containing the elements of the specified
    * collection, in the order they are returned by the collection's
    * iterator.
    *
    * @param  c the collection whose elements are to be placed into this list
    * @throws NullPointerException if the specified collection is null
    */
	public SinglyLinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}
	
	/**
     * Returns a shallow copy of this <tt>SingleLinkedList</tt>. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this <tt>SingleLinkedList</tt> instance
     */	
	@Override public SinglyLinkedList<E> clone() { 
		return new SinglyLinkedList<E>(this); 
	}
	
	/**
	 * A Serialisation Proxy class to override the default Java serialisation 
	 * mechanism for the singly-linked list.
	 * 
	 * Because the business fields {@code head, tail} and {@code end} are final, 
	 * they cannot be re-initialised in a 
	 * {@link SinglyLinkedList#readObject(ObjectInputStream)} method and values
	 * deserialised by the default Java serialisation mechanism will be used. 
	 * From security point of view such values cannot be trusted, the remedy 
	 * is to use the {@code Serialisation Proxy} prescription taken from 
	 * {@code Effective Java second edition}.
	 */
	private static class SerializationProxy<T> implements Serializable 
	{	
		private static final long serialVersionUID = 1L;
		private transient SinglyLinkedList<T> list;
		
		public SerializationProxy(SinglyLinkedList<T> lst) { this.list = lst; }
		
		private void writeObject(ObjectOutputStream oos) throws IOException {
			oos.defaultWriteObject();
			oos.writeInt(list.size());
			for(Object o : list)
				oos.writeObject(o);
		}
		
		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
			ois.defaultReadObject();
			final int size = ois.readInt();
			if(size < 0) {
				throw new InvalidObjectException(
					"A serialised SingleLinkedList with negative size: " + size
				);
			}
			list = new SinglyLinkedList<T>();
			for(int i = 0; i < size; ++i) 
				append(list, ois.readObject());
		}
		
		@SuppressWarnings("unchecked") 
		private <S> void append(List<S> lst, Object o) {
			lst.add((S)o);
		}
		
		private Object readResolve() { return this.list; }
	}
	
	private Object writeReplace() { return new SerializationProxy<E>(this); }
	
	private void readObject(ObjectInputStream ois) throws InvalidObjectException { 
		throw new InvalidObjectException("use the proxy, dear"); 
	}
	
	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns. 
	 */
	@Override public void clear() {
		for(Iterator<E> i = iterator(); i.hasNext(); i.remove()) {
			i.next();
		}
	}	

	/** 
	 * The only method that directly changes the structure adding an element 
	 * @param after the node to insert after, assumption is that this is a valid node
	 * from the list in the range [head, last]
	 * @param value the value to store
	 * @return reference to the added element
	 * */
	private Node<E> addNodeAfter(Node<E> after, E value) {
		Node<E> n = new Node<E>(value, after.next);
		after.next = n;
		if(n.next == end()) // is "n" the new last node?
			tail.next = n; // tail is not a proper element of the list
		changeSize(+1);    // and must be manually updated
		incrementModCount();
		return n;
	}
	
	/** 
	 * The method that directly changes the structure to remove an element 
	 * @param after the node whose next is to be detached from the list and nullified, 
	 * assumption is that {@code after} is a valid node in the range [ head, last )
	 * */
	private E removeTheNodeAfter(Node<E> after) {
		Node<E> removed = after.next;
		after.next = removed.next;
		removed.next = null;
		E toBeReturned = removed.e;
		removed.e = null; 
		// is after node the new last node?
		if(after.next == end())
			tail.next = after;
		// 
		changeSize(-1);
		incrementModCount();
		return toBeReturned;
	}
	
	/**
     * Reverses the order of the elements in the list, runs in linear time.
     * @return {@code this} for chaining
	 */
	public SinglyLinkedList<E> reverse() {
		if(size() > 1) {
			checkForComodification();
			for(Node<E> current = begin(), previous = end(); current != end(); ) {
				Node<E> next = current.next;
				current.next = previous;
				previous = current;
				current = next;
			}
			Node<E> oldBegin = head.next;
			head.next = tail.next;
			tail.next = oldBegin;
			incrementModCount();
		}
		return this;
	}
		
	/**
	 * Sorts the list according to their natural order, using   
	 * <a href="http://www.sorting-algorithms.com/merge-sort">merge sort algorithm</a> 
	 * <p>
	 * Properties:
	 * <li>stable sort
	 * <li>lg(n) recursion stack depth
	 * <li>n * lg(n) running time
	 * <p>
	 * Assumption: {@code E implements Comparable>
	 * @param compir the comparator to order the elements
	 * @return the sorted list for chaining
	 * @throws ClassCastException when elements of the list
	 * are not {@link Comparable}
	 */
	public SinglyLinkedList<E> sort() {
		return sort( new Comparator<E>() {					
						 @SuppressWarnings("unchecked") 
						 @Override public int compare(final E o1, final E o2) {
							 return ((Comparable)o1).compareTo(o2);
						 }
					 });
	}

	/**
	 * Sorts the list as ordered by the {@code compir}, using   
	 * <a href="http://www.sorting-algorithms.com/merge-sort">merge sort algorithm</a> 
	 * <p>
	 * Properties
	 * <li>stable sort
	 * <li>lg(n) recursion stack depth
	 * <li>n * lg(n) running time
	 * 
	 * @param compir the comparator to order the elements
	 * @return the sorted list for chaining
	 */	
	public SinglyLinkedList<E> sort(Comparator<E> compir) {
		checkForComodification();
		mergeSort(this.head, this.tail, size(), compir);
		incrementModCount();
		return this;		
	}
	
	static private <T> 
	void mergeSort(final Node<T> h, final Node<T> t, final int len, final Comparator<T> comp) {
		if(len > 1) {
			final int m = len >>> 1;
			final Node<T> t1 = new Node<T>(null, findNodeBefore(h, m)); // need a full blown Node in order to change where it refers to
			mergeSort(h, t1, m, comp);
			mergeSort(t1.next, t, len - m, comp);
			if(comp.compare(t1.next.e, t1.next.next.e) > 0) // skip merging if already ordered
				merge(h, t1, t1.next, t, comp);
		}
	}

	// The last element of the first list is head element of the second, 
	// and the first element of the second list is the end() element of the first list.
	// The end() sentinel node cannot be used for end-of-list checks, 
	// as during merging, the first element of the second list (the end() of the first list)
	// can be merged somewhere into the first list, thus losing forever the sentinel 
	// value / meaning. Therefore the lists must
	// be designated as [head, tail] rather than [head, end)
	static private <T> 
	void merge(Node<T> h1, final Node<T> t1, final Node<T> h2, final Node<T> t2, final Comparator<T> comp) {
		while(t1.next != h1 && t2.next != h2) {
			if(comp.compare(h1.next.e, h2.next.e) > 0) { // insert at found position
				final Node<T> unlinked = h2.next; // unlink from second list
				h2.next = unlinked.next;
				unlinked.next = h1.next;    // link/insert into first list
				h1.next = unlinked;
				if(t2.next == unlinked) // as tail is not a proper element from the list, it must be manually updated
					t2.next = h2; // the new last element. Basically the second list was emptied
			}
			h1 = h1.next;
		}
	}
	
	/**
	 * Sorts the list using parallel merge algorithm
	 * @param pooly fork join pool to use for task parallelisation
	 * @param compir to order the elements
	 * @return current list for method chaining
	 */
	public SinglyLinkedList<E> parallelSort(final ForkJoinPool pooly, final Comparator<E> compir) {
		checkForComodification();
		final int threshold = 1 + size() / ( 8 * Runtime.getRuntime().availableProcessors());
		doParallelSort(this.head, this.tail, size(), compir, Math.max(threshold, 256), pooly);		
		incrementModCount();
		return this;		
	}

	private Comparator<E> naturalOrder() {
		return new Comparator<E>() {					
			 @SuppressWarnings({ "rawtypes", "unchecked" }) 
			 @Override public int compare(final E o1, final E o2) {
				 return ((Comparable)o1).compareTo(o2);
			 }
		};
	}
	
	/**
	 * Sorts the list using parallel merge algorithm according 
	 * to the natural order of the elements
	 * @param pooly fork join pool to use for task parallelisation
	 * @return current list for method chaining
	 */
	public SinglyLinkedList<E> parallelSort(final ForkJoinPool pooly) {
		return parallelSort(pooly, naturalOrder());
	}	
	
	/**
	 * Sorts the list using parallel merge algorithm and a 
	 * default {@link ForkJoinPool}
	 * according the natural order of the elements, using 
	 * 
	 * @return current list for method chaining
	 */
	public SinglyLinkedList<E> parallelSort() {
		return parallelSort(naturalOrder());
	}
	
	/**
	 * Sorts the list using parallel merge algorithm and
	 * a default @link {@link ForkJoinPool}
	 * @param compir to order the elements
	 * @return current list for method chaining
	 */	
	public SinglyLinkedList<E> parallelSort(final Comparator<E> compir) {
		ForkJoinPool pooly = new ForkJoinPool();
		parallelSort(pooly, compir);
		pooly.shutdown();
		return this;		
	}
	
	@SuppressWarnings("serial")
	private static <T> void doParallelSort(final Node<T> h, final Node<T> t, final int len
			                             , final Comparator<T> comp, final int THRESHOLD
			                             , final ForkJoinPool pooly) {
		if(len < THRESHOLD) {
			mergeSort(h, t, len, comp);
		}
		else {
			final Node<T> originalEnd = t.next.next; // in case this is a sublist, end() != head
			final int m = len >>> 1;
			// Detach list1 from list2 to divide and concur in parallel
			// list1 requires a new tail and end , list2 a new head 
			final Node<T> t1 = new Node<T>(null, findNodeBefore(h, m)); 
			final Node<T> h2 = new Node<T>(null, t1.next.next);
			t1.next.next = h; // the new end1, end() is always the head unless it's a sublist
			t.next.next = h2; // and new end2 -	but the originalEnd accounts for the sublist case
			final RecursiveAction right = new RecursiveAction() {
				@Override protected void compute() {
					doParallelSort(h, t1, m, comp, THRESHOLD, pooly);
				}
			};
			pooly.execute(right);
			doParallelSort(h2, t, len - m, comp, THRESHOLD, pooly);
			right.join();
			//
			if(comp.compare(t1.next.e, h2.next.e) > 0) {
				merge(h, t1, h2, t, comp);
			}
			if(t.next == h2) { //list2 is empty, just fix the tail
				t.next = t1.next;
			}
			else { // re-attach list1 and list2
				t1.next.next = h2.next;
			}
			t.next.next = originalEnd; // fix the end of the list
		}
	}
		
	/**
	 * Appends the argument to the end of the list.
	 * 
	 * @param e the element to append, can be {@code null}
	 * @return {@code true}
	 */
	@Override public boolean add(E e) {
		checkForComodification();
		return addNodeAfter(tail.next, e) != null; 
	}
	
	/**
	 * Inserts the specified element at the specified position in the list.
	 * 
	 * @param index the position to insert at. 
	 * @param value the element to insert, can be {@code null}
	 * @throws IndexOutOfBoundsException {@inheritDoc}
	 */
	@Override public void add(int index, E value) {
		if(checkRange(index, size()) == size()) {
			add(value); // speedy appending, will check for co-modification
		}
		else {
			checkForComodification();
			addNodeAfter(findNodeBefore(head, index), value);
		}
	}

	@Override public boolean addAll(Collection<? extends E> c) {
		return insertAllAfter(tail.next, c);
	}

	// to remove this one - I need a InnerIterator.add(E e) implemented
	@Override public boolean addAll(int index, Collection<? extends E> c) {
		checkRange(index, size());
		return insertAllAfter(findNodeBefore(head, index), c);				
	}
	
	private boolean insertAllAfter(Node<E> insertAfter, Collection<? extends E> c) {
		boolean hasChanged = false;
		checkForComodification();
		for(E e : c) {
			insertAfter = addNodeAfter(insertAfter, e);
			hasChanged = true;
		}
		return hasChanged;		
	}
	
	/**
     * Removes the element at the specified position in this list and returns 
     * the element that was removed from the list.
     *
     * @param index {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */	
	@Override public E remove(int index) {
		checkRange(index, size() - 1);
		checkForComodification();
		return removeTheNodeAfter(findNodeBefore(head, index));
	}	
	
	
	/**
     * Returns a forward only implementation of {@link ListIterator} over the 
     * elements in this list. 
     * <p>
     * This implementation does not support backward traversal methods 
     * {@link ListIterator#previous previous} and {@link ListIterator#hasPrevious()}
     *
     * @return {@inheritDoc}
     */ 	
	@Override public ListIterator<E> listIterator() {		
		return new ForwardOnlyIterator(0);
	}

	 /**
     * Returns a forward only implementation of {@link ListIterator} over the 
     * elements in this list, starting at the specified position in this list. 
     * The specified index indicates the first element that would be
     * returned by an call to {@link ListIterator#next next}.
     * <p>
     * This implementation does not support the backwards traversal methods 
     * {@link ListIterator#previous previous} and {@link ListIterator#hasPrevious()}
     *
     * @param index index of first element to be returned from the
     *              list iterator (by a call to the {@link ListIterator#next()} method)
     * @return a forward only list iterator of the elements in this list (in proper
     *         sequence), starting at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *         {@code (index < 0 || index > size())}
     */ 	
	@Override public ListIterator<E> listIterator(int fromIndex) {
		return new ForwardOnlyIterator(fromIndex);
	}
		
	/** implements just the minimum set of forward iteration methods */
	final class ForwardOnlyIterator implements ListIterator<E> {

		Node<E> previous = head; // previous is used for to enable removal of node pointed bu current
		Node<E> current = head; // current == previous means next() has not been invoked
		int expectedModCount = modCount;
		int nextIndex = 0; 
		
		ForwardOnlyIterator(int fromIndex) {
			checkRange(fromIndex, size());			
			for(int i = 0; i < fromIndex; ++i)
				next();
		}		

		void checkForComodification() { 
			SinglyLinkedList.this.checkForComodification(); // make sure parent and grandparents
			if(expectedModCount != modCount) {              // are Ok with this modification too
				throw new ConcurrentModificationException(
					"iterator has a different modcount " + expectedModCount 
					+ " from parent: " + parent.modCount				
				);
			}
		}
				
		//@Override public boolean hasNext() { return current.next != end(); }
		@Override public boolean hasNext() { return nextIndex != size(); }
		
		@Override public int nextIndex() { return nextIndex; }

		@Override public void set(E e) {
			checkForComodification();
			if(current == previous)
				throw new IllegalStateException("invoke next() before set(E e)");
			current.e = e;
		}
		
		@Override public void remove() {
			checkForComodification();
			if(current == previous)
				throw new IllegalStateException("invoke next() before remove()");
			removeTheNodeAfter(previous);
			// effectively after the removal the cursor shifts back to the previous element
			current = previous;
			--nextIndex;
			++expectedModCount;
		}			
		
		@Override public void add(E e) {
			checkForComodification();			
			addNodeAfter(current, e);
			previous = current;
			current = current.next;
			++nextIndex; //increment because the number of elements before the next element has just increased
			++expectedModCount;
		}		
						
		@Override public E next() {
			checkForComodification();
			if(nextIndex == size) {
				throw new NoSuchElementException("list size: " + size 
						                       + " currentindex = " + nextIndex);
			}
			previous = current;
			current = current.next;
			++nextIndex;
			return current.e;
		}		
		
		@Override public boolean hasPrevious() { throw new UnsupportedOperationException(); }

		@Override public E previous() {	 throw new UnsupportedOperationException(); }

		/** 
		 * {@inheritDoc} 
		 * <p>
		 * Required by the implementation of {@link AbstractList#indexOf(Object)} 
		 * @return {@inheritDoc}
		 */
		@Override public int previousIndex() { return nextIndex - 1; }	
	}	
	
	/**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations supported
     * by this list.<p>
     *
     * This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).   Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for <tt>indexOf</tt> and
     * <tt>lastIndexOf</tt>
     * <p>
     * The sublist is <i>fail-fast</i>: if the list is structurally
     * modified by the same thread at any time after the sublist is created, in any way except
     * through the sublist methods or via the methods of an iterator spawned by the sublist,
     * the sublist will throw a <tt>ConcurrentModificationException</tt>.  Thus, in the face of
     * concurrent modification, the sublist fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future. Mind that fail-fast behavior does not mean thread safe behavior.
     *
     * @param {@inheritDoc}
     * @param {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException for an illegal end-point index value
     *         {@code fromIndex < 0 || toIndex > size }
     *         
     * @throws IllegalArgumentException for {@code fromIndex > toIndex}. This should be a {@link IndexOutOfBoundsException}
     * 		   however all implementations of {@link List} in JDK violate the contract of {@link List#subList(int, int), 
     * 		   so conform to violation practice.
     */
	@Override public SinglyLinkedList<E> subList(int fromIndex, int toIndex) 
	{
		checkRange(fromIndex, size());
		checkRange(toIndex, size());
		final int len = toIndex - fromIndex;
		if(len < 0) {
			throw new IndexOutOfBoundsException("toIndex " + toIndex + " must be >= fromIndex " + fromIndex);
		}
		checkForComodification();
		Node<E> h = findNodeBefore(head, fromIndex); // the head of the sublist
		Node<E> last = findNodeBefore(h, len);       // the last element in the sublist
		Node<E> sTail = (tail.next != last ? new Node<E>(null, last) : tail);
		return new SinglyLinkedList<E>(h, last.next, sTail, len, this);
	}	
	
	private SinglyLinkedList(Node<E> head, Node<E> end, Node<E> tail, int len, SinglyLinkedList<E> parent) {
		this.head = head;
		this.end  = end;
		this.tail = tail;
		this.size = len;
		this.parent = parent;
		this.modCount = parent.modCount;
	}

	
	private static <T> Node<T> findNodeBefore(Node<T> start, int index) {		
		while(index-- > 0) 
			start = start.next;		
		return start;
	}	
	
	 // Must be implemented because AbstractList.lastIndexOf requires proper previous() method.
	@Override public int lastIndexOf(Object o) {
		int i = 0, lastIndex = -1;		
		for(E e : this) {
			if(areEqual(o, e)) {
				lastIndex = i;
			}
			++i;
		}
		return lastIndex;
	}
	
	@Override public int size() { return size; }
		
	boolean areEqual(Object one, Object two) {
		return (one == two) || (one == null ? two == null : one.equals(two));
	}
	
	void t(boolean condition) { if(!condition) throw new IllegalStateException(); }
	
	int checkRange(int index, int end) {
		if(index < 0 || index > end) {
			throw new IndexOutOfBoundsException(
				String.format("index %d is outside of permissible range [ 0, %d] ", index, end)
			);
		}
		return index;
	}
		
}
