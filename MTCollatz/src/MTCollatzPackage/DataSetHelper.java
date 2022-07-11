package MTCollatzPackage;

/**************************************************************
Student Name: Dae Sung, Mukesh Rathore
File Name: MTCollatz
Assignment Number: Project 1

The DataSetHelper class is used to calculate the formula per thread
outside of lock and unlock as to not bottleneck the other Threads.
**************************************************************/
// 1 per thread
public class DataSetHelper {
	
	// local thread safe count
	int safeCount = 0;
	int upperLimit;
	
	
	//Constructor
	public DataSetHelper(int upper) {
		this.upperLimit = upper;
	}
	
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
		while (value > 1) {
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