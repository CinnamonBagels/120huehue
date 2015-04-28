package nachos.threads;

import java.util.Iterator;
import java.util.LinkedList;

import nachos.machine.*;

//this shit won't work
//import java.util.Comparator;
//import java.util.PriorityQueue;
//import java.util.

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    Lock waitLock;
    //priority queue won't work.
//    Comparator<Long> comparator;
//    PriorityQueue<Pair<KThread, Long>> queue;
    
    
    LinkedList<KThread> threads;
    LinkedList<Long> times;
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
    	this.waitLock = new Lock();
    	//queue = new PriorityQueue()
    	
    	threads = new LinkedList<KThread>();
    	times = new LinkedList<Long>();
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
    	Machine.interrupt().disable();
    	Iterator<KThread> threadIter = threads.iterator();
    	Iterator<Long> timeIter = times.iterator();
    	KThread thread;
    	while(threadIter.hasNext()) {
    		thread = threadIter.next();
    		//Long class, need to get primitive value.
    		if(Machine.timer().getTime() > timeIter.next()) {
    			thread.ready();
    			threadIter.remove();
    			timeIter.remove();
    			
    		}
    	}
    	//for(Iterator<KThread> threadIter = threads.iterator(), Iterator<Long> timeIter = times.iterator();  )
    	Machine.interrupt().enable();
    	
	KThread.currentThread().yield();
    }
    
    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
    	//do we need to halt interrupts?
    	Machine.interrupt().disable();
		long wakeTime = Machine.timer().getTime() + x;
		waitLock.acquire();
		threads.add(KThread.currentThread());
		times.add(wakeTime);
		waitLock.release();
		KThread.currentThread().sleep();
		Machine.interrupt().disable();
//	while (wakeTime > Machine.timer().getTime())
//	    KThread.yield();
    }
    
    public static void selftest() {
        KThread t1 = new KThread(new Runnable() {
            public void run() {
                long time1 = Machine.timer().getTime();
                int waitTime = 10000;
                System.out.println("Thread calling wait at time:" + time1);
                ThreadedKernel.alarm.waitUntil(waitTime);
                System.out.println("Thread woken up after:" + (Machine.timer().getTime() - time1));
                Lib.assertTrue((Machine.timer().getTime() - time1) > waitTime, " thread woke up too early.");
                
            }
        });
        t1.setName("T1");
        t1.fork();
        t1.join();
    }
}
