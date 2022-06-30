/**************************************************************
Student Name: Dae Sung, Mukesh Rathore
File Name: MTCollatz
Assignment Number: Project 1

This file contains all necessary classes to calculate the MTCollatz Stopping time
for ranges and thread provided by the user within Command Prompt or equivalent.
**************************************************************/

import java.lang.Thread;
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
		int upperLimit = (int)Integer.parseInt(upperLimitArg);
		int threadLimit = (int)Integer.parseInt(threadLimitArg);
		
		//shared data class
		class DataSet {
			Instant startInstant;
			Instant endInstant;
			boolean Complete = false;
			//lock belongs in shared data class so that threads can lock unlock on shared memory
			ReentrantLock lock = new ReentrantLock();
			int[] resultArray;
			//resultArray[0] will start with Counter = 2
			int Counter = 2;
			//Constructor
			public DataSet(int[] array) {
				this.resultArray = array;
			}
			//Calculate will try lock, then perform calculation and iterate Counter
			//this is necessary to lock when calculating because Counter is directly tied to calculation
			public void Calculate() {
				try {
					lock.lock();
					//checking Counter occurs in lock because Counter value may change outside lock
					if(this.Counter <= upperLimit) {
						//N = 2 starts at Array[0]
						resultArray[Counter-2] = Formula(Counter);
						Counter++;
					}
					else {
						Complete = true;
					}
				}
				//guaranteed unlock to avoid Mutual Exclusion Lock
				finally {
					lock.unlock();
				}
			}
			//The MTCollatz formula
			public static int Formula(int num) {
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
			public void print() {
				if (this.resultArray.length > 0) {
					String result = "<";
					for (int i = 0; i < upperLimit - 1; i ++) {
						result = result + String.valueOf(i+2);
						if (i < upperLimit - 2) result = result + ",";
					}
					result = result + ">,<";
					for (int i = 0; i < upperLimit - 1; i++) {
						result = result + String.valueOf(this.resultArray[i]);
						if (i < upperLimit - 2) result = result + ",";
					}
					result = result + ">";
					System.out.print(result);
				}
			}
		}
		//instance of shared memory class DataSet which is stored in heap
		DataSet data = new DataSet(new int[upperLimit-1]);
		
		class threadRunner implements Runnable {
			@Override
			
			public void run() {
				//startInstant is set by first thread to run
				//must check again after lock to ensure sync
				if(data.startInstant == null) data.startInstant = Instant.now();
				while(data.Counter <= upperLimit) {
					data.Calculate();
				}
				data.endInstant = Instant.now();
				int duration = data.endInstant.getNano() - data.startInstant.getNano();
				//prints to stderr
				System.err.println(upperLimit + "," +  threadLimitArg + "," + duration);
				//data.print();
			}
		}
		//Creates the threads specified by user
		List<Thread> threadList = new LinkedList<Thread>();
		data.startInstant = Instant.now();
		
		for(int i = 0; i < threadLimit; i++) {
			Thread thread = new Thread(new threadRunner(), String.valueOf(i));
			//unfortunately, the adding thread to list to reference after the loop is time cost
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
		
	}
}
