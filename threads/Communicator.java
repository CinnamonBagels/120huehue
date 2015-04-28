package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
	private  Lock lock;
	private LinkedList<Listener> listenerList;
	//private LinkedList<Socket> speakers;
	private Condition speakers;
	private Condition listeners;
	boolean spoken;
	private int word;
	private int noListeners;
	private boolean listener;
	private boolean transferComplete;
	
	
	public class Listener {
		public int word;
		
		public Listener() {
			
		}
	}
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	lock = new Lock();
    	speakers = new Condition(lock);
    	listeners = new Condition(lock);
    	noListeners = 0;
    	spoken = false;
    	transferComplete = false;
    	
    	//listeners = new LinkedList<Socket>();
    	//speakers = new LinkedList<Socket>();
    	
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	lock.acquire();
    	
    	while(spoken) {
    		speakers.sleep();
    	}
    	
    	this.word = word;
    	spoken = true;
    	//wake up listeners to receive word because spoken.
    	listeners.wake();
    	if(!transferComplete) {
    		//do nothing
    	}
    	transferComplete = false;
    	lock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	lock.acquire();
    	while(spoken == false) {
    		speakers.wake();
    		listeners.sleep();
    	}
    	int word = this.word;
    	spoken = false;
    	transferComplete = true;
    	lock.release();
    	return word;
	//return 0;
    }
    
    public static void selfTest(){
        final Communicator com = new Communicator();
        final long times[] = new long[4];
        final int words[] = new int[2];
        KThread speaker1 = new KThread( new Runnable () {
            public void run() {
            	//System.out.println("speaker1 speaking");
                com.speak(4);
                
                times[0] = Machine.timer().getTime();
            }
        });
        speaker1.setName("S1");
        KThread speaker2 = new KThread( new Runnable () {
            public void run() {
            	//System.out.println("speaker2 speaking");
                com.speak(7);
                
                times[1] = Machine.timer().getTime();
            }
        });
        speaker2.setName("S2");
        KThread listener1 = new KThread( new Runnable () {
            public void run() {
            	//System.out.println("listener1 listening");
                words[0] = com.listen();
                
                times[2] = Machine.timer().getTime();
            }
        });
        listener1.setName("L1");
        KThread listener2 = new KThread( new Runnable () {
            public void run() {
            	//System.out.println("listener2 listening");
                words[1] = com.listen();
                
                times[3] = Machine.timer().getTime();
            }
        });
        listener2.setName("L2");
        
        speaker1.fork(); speaker2.fork(); listener1.fork(); listener2.fork();
        speaker1.join(); speaker2.join(); listener1.join(); listener2.join();
        
        Lib.assertTrue(words[0] == 4, "Didn't listen back spoken word."); 
        Lib.assertTrue(words[1] == 7, "Didn't listen back spoken word.");
        Lib.assertTrue(times[0] < times[2], "speak returned before listen.");
        Lib.assertTrue(times[1] < times[3], "speak returned before listen.");
    }
    
    
}
