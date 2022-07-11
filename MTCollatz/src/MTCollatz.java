
/**************************************************************
Student Name: Dae Sung, Mukesh Rathore
File Name: MTCollatz
Assignment Number: Project 1

This file contains all necessary classes to calculate the MTCollatz Stopping time
for ranges and thread provided by the user within Command Prompt or equivalent.
**************************************************************/

import java.lang.Thread;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

//parent class to contain all MTCollatz classes

public class MTCollatz {

	// main class
	public static void main(String[] args) {

		// args provided by user in bash/shell/command prompt
		// UpperLimit of Range N
		String upperLimitArg = args[0];
		// Number of threads
		String threadLimitArg = args[1];
		// convert string to int

		//String upperLimitArg = "100";
		//String threadLimitArg = "1";

		int upperLimit = (int) Integer.parseInt(upperLimitArg);
		int threadLimit = (int) Integer.parseInt(threadLimitArg);

		// shared data class
		class DataSet {
			ReentrantLock lock = new ReentrantLock();
			int[] resultArray = new int[1000];
			Instant startInstant = null;
			Instant endInstant = null;
			int counter = 1;
			int maxLength = 0;

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
		// 1 per thread
		class DataSetHelper {
			// local thread safe count
			int safeCount = 0;

			public void Calculate(DataSet d) {
				safeCount = d.GetCounter();
				if (safeCount <= upperLimit && safeCount > 0) {
					// calculated outside lock
					int stopTime = Formula(safeCount);
					d.SetMax(stopTime);
					if (stopTime <= 1000)
						d.increment(stopTime - 1); // increment frequency if critical code ran
				}
			}

			// The MTCollatz formula
			public static int Formula(int num) {
				int i = 1;
				// use long in case value gets large
				long value = (long) num;
				while (value != 1) {
					if (value % 2 == 0) {
						value = value / 2;
						i++;
					} else {
						value = ((value * 3) + 1);
						i++;
					}
				}
				return i;
			}
			// prints out large array of results. Format is based on the assignment
		}

		// instance of shared memory class DataSet which is stored in heap
		DataSet data = new DataSet();

		class threadRunner implements Runnable {
			// one instance for each thread
			DataSetHelper dataHelp = new DataSetHelper();

			@Override
			public void run() {
				// must check again after lock to ensure sync
				while (data.counter <= upperLimit) {
					this.dataHelp.Calculate(data);
				}
			}
		}
		// Creates the threads specified by user
		List<Thread> threadList = new LinkedList<Thread>();

		for (int i = 0; i < threadLimit; i++) {
			Thread thread = new Thread(new threadRunner(), String.valueOf(i));
			threadList.add(thread);
			if (data.startInstant == null)
				data.startInstant = Instant.now();
			thread.start();
		}

		for (Thread t : threadList) {
			try {
				t.join();
				if (data.endInstant == null)
					data.endInstant = Instant.now();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		int[] array = new int[data.maxLength];
		for (int i = 0; i < data.maxLength; i++) {
			array[i] = data.resultArray[i];
		}
		data.print(array);
		// duration measure from opening of threads to closing of threads
		float duration = Duration.between(data.startInstant, data.endInstant).toMillis();
		System.out.println();
		System.err.println(upperLimit + ", " + threadLimitArg + ", " + duration + " milliseconds");
	}
}
