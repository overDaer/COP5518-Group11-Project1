ReadME

Student Name: Dae Sung, Mukesh Rathore
Assignment Number: Project 1

INSTRUCTIONS:

In order to run the java file, open the Windows PowerShell or CommandPrompt in the containing folder.
Run "java MTCollatz.java [range N] [threads T]"
Optionally "java MTCollatz.java [range N] [threads T] 2> results.csv" to get output of Range R, threads T, and runtime in milliseconds.
Translate syntax as necessary based on OS.

ISSUES:

Race Condition:

We definitely fell into a race condition issue, where we perform a while 
loop in the run() method implementing Runnable, outside of the lock.lock() lock.unlock() safe space. 
Because of this, the counter was incrementing in the short time between the while conditional 
check and the lock function running, so we had to place a second conditional check after
the lock so that there was no way the counter would increment from another thread which 
did have it locked.

Locking:

There may have been a better solution to the simple locking unlocking, since we do not account for threads attempting to use it while locked in an elegent way,
instead just continuing iterating in the while loop until it is available. But because of the speed of the program, we found it unnecessary to optimize further.

