package datastructure;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Ridiculously over-engineered suite of tests that attempts
 * to test uniformly both the {@link SinglyLinkedList} and sub-lists
 * produced by {@link SinglyLinkedList#subList(int, int}) method.
 * 
 * @author alexander.georgiev@gmail.com
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for SinglyLinkedList");
		//$JUnit-BEGIN$
		suite.addTestSuite(FullSinglyLinkedListSublistTestCase.class);
		suite.addTestSuite(CollectionsAndSinglyLinkedListTestCase.class);
		suite.addTestSuite(EmptySinglyLinkedListSublistTestCase.class);
		suite.addTestSuite(SingleLinkedListTestCase.class);
		suite.addTestSuite(SublistSingleLinkedListTestCase.class);
		suite.addTestSuite(MergeSortTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
