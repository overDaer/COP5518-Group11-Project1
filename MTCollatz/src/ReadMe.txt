/************
Student Name: Dae Sung, Mukesh Rathore
File Name: ReadMe
Assignment Number: Project 1
************/

INSTRUCTIONS:
In order to run the java file, open the Windows PowerShell or CommandPrompt in the containing folder.
Run "java MTCollatz [range N] [threads T]" i.e. "java MTCollatz 10000 8"
Optionally "java MTCollatz [range N] [threads T] 2> results.csv" to get output of Range R, threads T, and runtime in milliseconds.
Optionally "java MTCollatz [range N] [threads T] > results.csv" to get output of stop times as csv file.

ISSUES:

Race Condition:
We definitely fell into a race condition issue, where we perform a while 
loop in the run() method implementing Runnable, outside of the lock.lock() lock.unlock() safe space. 
Because of this, the counter was incrementing in the short time between the while conditional 
check and the lock function running, so we had to place a second conditional check after
the lock so that there was no way the counter would increment from another thread which 
did have it locked.
