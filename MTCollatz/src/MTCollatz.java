import java.lang.Thread;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;

public class MTCollatz {

	public static void main(String[] args) {
		
		//String upperLimitArg = args[0];
		//String threadLimitArg = args[1];
		Scanner get = new Scanner(System.in);
		String upperLimitArg = get.nextLine();
		String threadLimitArg = get.nextLine();
		int upperLimit = (int)Integer.parseInt(upperLimitArg);
		int threadLimit = (int)Integer.parseInt(threadLimitArg);
		
		//shared data class
		class DataSet{
			Instant startInstant;
			Instant endInstant;
			ReentrantLock lock = new ReentrantLock();
			int[] resultArray;
			//0 is 2, increment by 1 at the end
			int Counter = 2;
			public DataSet(int[] array) {
				this.resultArray = array;
			}
			public void Calculate() {
				try {
					lock.lock();
					//N = 2 starts at Array[0]
					resultArray[Counter-2] = Formula(Counter);
					Counter++;
				}
				finally {
					lock.unlock();
				}
			}
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
			
			public void printTime() {
				if (this.resultArray.length > 0) {
					String result = "<";
					for (int i = 0; i < upperLimit - 1; i ++) {
						result = result + String.valueOf(i+2);
						if (i < upperLimit - 2) result = result + ",";
					}
					result = result + "><";
					for (int i = 0; i < upperLimit - 1; i++) {
						result = result + String.valueOf(this.resultArray[i]);
						if (i < upperLimit - 2) result = result + ",";
					}
					result = result + ">";
					System.out.println(result);
				}
				
			}
		}
		
		DataSet data = new DataSet(new int[upperLimit-1]);
		
		class threadRunner implements Runnable {
			@Override
			public void run() {
				if (data.startInstant == null) data.startInstant = Instant.now();
				while(data.Counter < upperLimit) {
					data.Calculate();
				}
				//only get runtime for original thread to avoid duplicate output
				if(Thread.currentThread().getName().equals("0")) {
					data.endInstant = Instant.now();
					int duration = data.endInstant.getNano() - data.startInstant.getNano();
					System.err.println(upperLimit + "," +  threadLimitArg + "," + duration);
					data.printTime();
				}
			}
		}
		
		for(int i = 0; i < threadLimit; i++) {
			Thread thread = new Thread(new threadRunner(), String.valueOf(i));
			thread.start();
		}
		
		get.close();
		
	}
}