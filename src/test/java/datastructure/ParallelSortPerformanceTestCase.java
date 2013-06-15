package datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static datastructure.Common.*;

public class ParallelSortPerformanceTestCase {

	final static int SAMPLES = 1500000;

	final static ArrayList<Integer> raw = populate(SAMPLES);

	final static ForkJoinPool pooly = new ForkJoinPool();

	public static void main(String[] args) {
		ParallelSortPerformanceTestCase par = new ParallelSortPerformanceTestCase();
		par.testPerformance();
	}

	public void testPerformance() {
		perf();
		perf();
		perf();
		perf();
		perf();
	}

	void perf() {
		Runtime.getRuntime().gc();
		performanceDlist();
		Runtime.getRuntime().gc();
		performanceSlist();
		Runtime.getRuntime().gc();
		performanceAlist();
		Runtime.getRuntime().gc();
		performanceParallelSlist();
	}

	public void performanceSlist() {
		SinglyLinkedList<Integer> slist = new SinglyLinkedList<Integer>(raw);
		//
		long now = System.nanoTime();
		slist.sort();
		now = System.nanoTime() - now;
		out("slist time:" + (now / 1000 / 1000));
		if (slist.get(new java.util.Random().nextInt(SAMPLES)).hashCode() < 0) {
			out("opaaaa");
		}
	}

	public void performanceAlist() {
		ArrayList<Integer> alist = new ArrayList<Integer>(raw);
		long now = System.nanoTime();
		Collections.sort(alist);
		now = System.nanoTime() - now;
		out("alist time:" + now / 1000 / 1000);
		if (alist.get(new java.util.Random().nextInt(SAMPLES)).hashCode() < 0) {
			out("opaaaa");
		}
		//
	}

	public void performanceParallelSlist() {
		SinglyLinkedList<Integer> slist = new SinglyLinkedList<Integer>(raw);
		//
		long now = System.nanoTime();
		slist.parallelSort(pooly);
		now = System.nanoTime() - now;
		out("parallel slist time:" + (now / 1000 / 1000));
		if (slist.get(new java.util.Random().nextInt(SAMPLES)).hashCode() < 0) {
			out("opaaaa");
		}
	}

	public void performanceDlist() {
		LinkedList<Integer> dlist = new LinkedList<Integer>(raw);

		long now = System.nanoTime();
		Collections.sort(dlist);
		now = System.nanoTime() - now;
		out("dlist time:" + (now / 1000 / 1000));
		if (dlist.get(new java.util.Random().nextInt(SAMPLES)).hashCode() < -1000000) {
			out("opaaaa");
		}
	}

	static ArrayList<Integer> populate(final int SAMPLES) {
		out("populating baby!");
		ArrayList<Integer> list = new ArrayList<Integer>(SAMPLES);
		Random r = new Random();
		for (int i = 0; i < SAMPLES; ++i)
			list.add(r.nextInt());
		return list;
	}

}
