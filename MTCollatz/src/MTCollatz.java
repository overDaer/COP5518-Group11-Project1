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
	
	
	//main class
	public static void main(String[] args) {
		
		//args provided by user in bash/shell/command prompt
		//UpperLimit of Range N
		String upperLimitArg = args[0];
		//Number of threads
		String threadLimitArg = args[1];
		//convert string to int
		
		
		//String upperLimitArg = "10000";
		//String threadLimitArg = "2";
		
		
		int upperLimit = (int)Integer.parseInt(upperLimitArg);
		int threadLimit = (int)Integer.parseInt(threadLimitArg);
		
		//shared data class
		class DataSet {
			Instant startInstant;
			Instant endInstant;
			boolean Complete = false;
			//lock belongs in shared data class so that threads can lock unlock on shared memory
			ReentrantLock lock = new ReentrantLock();
			int[] resultArray = new int[597]; //Max possible length for N below 5 million is 597
			int Counter = 2;
			int maxLength = 0;
			
			//Calculate will try lock, then perform calculation and iterate Counter
			//this is necessary to lock when calculating because Counter is directly tied to calculation
			public void Calculate() {
				try {
					lock.lock();
					//checking Counter occurs in lock because Counter value may change outside lock
					if(this.Counter <= upperLimit) {
						int stopTime = Formula(Counter);
						if (maxLength < stopTime) maxLength = stopTime;
						if (stopTime <= 597) resultArray[stopTime-2] += 1; //increment frequency if critical code ran
						Counter++;
					}
					else {
						Complete = true;
					}
				}
				//guaranteed unlock to avoid Mutual Exclusion Lock
				finally {
					lock.unlock();
					//calculate outside unlock so other threads can access counter
					//N = 2 starts at Array[0]
					
				}
			}
			//The MTCollatz formula
			public int Formula(int num) {
				int i = 1;
				//use long in case value gets large
				long value = (long)num;
				while(value != 1) {
					if (value % 2 == 0) {
						value = value / 2;
						i++;
					}
					else {
						value = ((value * 3) + 1);
						i++;
					}
				}
				return i;
			}
			//prints out large array of results. Format is based on the assignment
			public void print(int[] array) {
				if (array.length > 0) {
					String result = "<";
					for (int i = 0; i < array.length; i ++) {
						result = result + String.valueOf(i+2);
						if (i < array.length - 1) result = result + ",";
					}
					result = result + ">,<";
					for (int i = 0; i < array.length; i++) {
						result = result + String.valueOf(array[i]);
						if (i < array.length - 1) result = result + ",";
					}
					result = result + ">";
					System.out.print(result);
				}
			}
		}
		
		//instance of shared memory class DataSet which is stored in heap
		DataSet data = new DataSet();
		class threadRunner implements Runnable {
			@Override
			public void run() {
				//must check again after lock to ensure sync
				while(data.Counter <= upperLimit) {
					data.Calculate();
				}
			}
		}
		//Creates the threads specified by user
		List<Thread> threadList = new LinkedList<Thread>();
		

		if(data.startInstant == null) data.startInstant = Instant.now();
		
		for(int i = 0; i < threadLimit; i++) {
			Thread thread = new Thread(new threadRunner(), String.valueOf(i));
			threadList.add(thread);
			thread.start();
		}
		
		for (Thread t: threadList) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(data.endInstant == null) data.endInstant = Instant.now();
		int[] array = new int[data.maxLength - 1];
		for (int i = 0 ; i < data.maxLength - 1; i++) {
			array[i] = data.resultArray[i];
		}
		data.print(array);
		//duration measure from opening of threads to closing of threads
		long duration = Duration.between(data.startInstant, data.endInstant).toMillis();
		System.out.println();
		System.err.println(upperLimit + "," +  threadLimitArg + "," + duration);
	}
}
