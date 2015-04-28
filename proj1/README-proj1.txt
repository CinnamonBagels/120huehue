I did this by myself

======
Task 1 - KThread.java (KThread.join())
======

By using semaphores, you can implement join by using only two lines. One execution of joinSemaphore.P() when join() is called to sleep the current threaad and wait for the joining thread to complete. When the thread has completed in runThread(), I called joinSemaphore.V() to indicate that the thread has finished. 

		-------
		TESTING
		-------
KThread t2 = new KThread(new PingTest(1));
		
		t2.fork();
		//t2.join(); //commented at first
		
		new PingTest(0).run();

will output

*** awesome thread 0 looped 0 times
*** awesome thread 1 looped 0 times
*** awesome thread 0 looped 1 times
*** awesome thread 1 looped 1 times
*** awesome thread 0 looped 2 times
*** awesome thread 1 looped 2 times
*** awesome thread 0 looped 3 times
*** awesome thread 1 looped 3 times
*** awesome thread 0 looped 4 times
*** awesome thread 1 looped 4 times

KThread t2 = new KThread(new PingTest(1));
		
		t2.fork();
		t2.join(); //uncommented
		
		new PingTest(0).run();

will output

*** awesome thread 1 looped 0 times
*** awesome thread 1 looped 1 times
*** awesome thread 1 looped 2 times
*** awesome thread 1 looped 3 times
*** awesome thread 1 looped 4 times
*** awesome thread 0 looped 0 times
*** awesome thread 0 looped 1 times
*** awesome thread 0 looped 2 times
*** awesome thread 0 looped 3 times
*** awesome thread 0 looped 4 times


======
Task 2 - Condition2.java
======

Following the given code for Condition.java, I implemented similar functionality. Since the only thing that, on a high level, semaphores are doing is sleeping and waking a thread, we can do the same thing, except directly. Most of the code is actually very similar except that you sleep and wake the threads in the condition, rather than in the semaphore.

		-------
		Testing
		-------

Using the TA's tests, I was able to produce the correct statements:

Added 0
Added 1
Added 2
Added 3
Added 4
Removed 0
Removed 1
Removed 2
Removed 3
Removed 4

======
Task 3 - Alarm.java
======

I added all sleeping threads and their respective wait times to a linked list when waitUntil() is called. When timerInterrupt is called, i iterate through the linked lists, checking for wait times that the machine timer have already surpassed. I then wake that thread, and remove it from the linked list. I'm not sure if I efficiently implmeneted it. It seems that the time taken to wake up the thread is rediculously long, as seen in the test.

		-------
		Testing
		-------

Using the TA's tests, I was able to produce this statement:

Thread calling wait at time:2690
Thread woken up after:10460

======
Task 4 - Communicator.java
======

I had a number of attempts at this: counting the number of listeners,, creating a new listener inner class to hold the word... creating a linked list for each speaker and listener... but conditions by themselves seems to work perfectly. 

This one took the most time. Since I know that both listen and speak have to wait for eachother to actually be called, I have a variable to detect this. If listen is called first, listen will wait (sleep) until spoken is called. It will also wake up any speakers. /* while(spoken == false) */ . If spoken is called first, it will set the  word, but WILL NOT RETURN until a listener is made. It will also wake up any listeners.

/* if(!transferComplete) { //do nothing } */

the code will execute as normally once a speaker-listener pair has been made.

		-------
		Testing
		-------

Using the TA's tests, none of the asserttrue functions returned false.

