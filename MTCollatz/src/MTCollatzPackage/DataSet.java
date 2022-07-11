package MTCollatzPackage;

/**************************************************************
Student Name: Dae Sung, Mukesh Rathore
File Name: MTCollatz
Assignment Number: Project 1

The DataSet class is the shared class which tracks the time it takes to
between start and join of thread, and the shared Array that contains the frequency
of Stopping Times for Collatz Calculation. Lock and unlock is used here
to avoid race conditions.
**************************************************************/

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

// shared data class
public class DataSet {
	public ReentrantLock lock = new ReentrantLock();
	public int[] resultArray = new int[1000];
	public Instant startInstant = null;
	public Instant endInstant = null;
	public int counter = 1;
	public int maxLength = 0;

	// lock, get counter, increment, unlock
	public int GetCounter() {
		try {
			lock.lock();
			return this.counter;
		} finally {
			this.counter++;
			lock.unlock();
		}
	}

	// if input is greater than maxLength then update
	public void SetMax(int stopTime) {
		try {
			lock.lock();
			if (this.maxLength < stopTime) {
				maxLength = stopTime;
			}
		} finally {
			lock.unlock();
		}
	}

	// increments frequency of stoppingTime in resultArray
	public void increment(int i) {
		try {
			lock.lock();
			resultArray[i] += 1;
		} finally {
			lock.unlock();
		}
	}

	public void print(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println((i + 1) + ", " + array[i]);
		}
	}
}
