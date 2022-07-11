
/**************************************************************
Student Name: Dae Sung, Mukesh Rathore
File Name: MTCollatz
Assignment Number: Project 1

MTCollatz contains the main class to run the program. It calls the classes
DataSet and DataSetHelper, where DataSet is the shared memory class, and
DataSetHelper is 1 per thread. This helps maintain separation between the calculations
and the incrementing, such that only the incrementing requires lock, unlock.
**************************************************************/

import java.lang.Thread;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import MTCollatzPackage.*;

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

		//String upperLimitArg = "200";
		//String threadLimitArg = "1";

		int upperLimit = (int) Integer.parseInt(upperLimitArg);
		int threadLimit = (int) Integer.parseInt(threadLimitArg);


		// instance of shared memory class DataSet which is stored in heap
		MTCollatzPackage.DataSet data = new MTCollatzPackage.DataSet();
		
		//class threadRunner in main to reference single instance of DataSet data
		class threadRunner implements Runnable {
			// one instance DataSetHelper for each thread
			MTCollatzPackage.DataSetHelper dataHelp = new MTCollatzPackage.DataSetHelper(upperLimit);

			@Override
			public void run() {
				// must check again after lock to ensure sync
				while (data.counter <= upperLimit) {
					this.dataHelp.Calculate(data);
				}
			}
		}
		//list of type thread to maintain reference
		List<Thread> threadList = new LinkedList<Thread>();
		
		// Creates the threads specified by user
		for (int i = 0; i < threadLimit; i++) {
			Thread thread = new Thread(new threadRunner(), String.valueOf(i));
			threadList.add(thread);
			if (data.startInstant == null)
				data.startInstant = Instant.now();
			thread.start();
		}
		
		//join threads
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
