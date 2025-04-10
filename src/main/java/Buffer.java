
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {
    private final Queue<BufElement> queue = new LinkedList<>();
    private final Semaphore availableSlots;
    private final Semaphore filledSlots = new Semaphore(0);
    private final Object lock = new Object();

    public Buffer(int capacity) {
        this.availableSlots = new Semaphore(capacity);
    }

    public void produce(BufElement item) throws InterruptedException {
        availableSlots.acquire(); // wait if buffer is full
        synchronized (lock) {
            queue.add(item);
        }
        filledSlots.release(); // signal consumer
    }

    public BufElement consume() throws InterruptedException {
        filledSlots.acquire(); // wait if buffer is empty
        BufElement item;
        synchronized (lock) {
            item = queue.poll();
        }
        availableSlots.release(); // signal producer
        return item;
    }
}










//import java.util.LinkedList;
//import java.util.Queue;
//
//public class Buffer {
//    private final Queue<BufElement> buffer;
//    private final int capacity;
//
//    public Buffer(int capacity) {
//        this.capacity = capacity;
//        this.buffer = new LinkedList<>();
//    }
//    
//    
//
//    // Producer adds to the buffer
//    public synchronized void produce(BufElement element) throws InterruptedException {
//        while (buffer.size() == capacity) {
//            wait(); // wait if full
//        }
//        buffer.add(element);
//        notifyAll(); // notify consumers
//    }
//
//    // Consumer removes from the buffer
//    public synchronized BufElement consume() throws InterruptedException {
//        while (buffer.isEmpty()) {
//            wait(); // wait if empty
//        }
//        BufElement element = buffer.poll();
//        notifyAll(); // notify producers
//        return element;
//    }
//}


















//
//
//
//
//
///*
//    public void produce(BufElement element) throws InterruptedException {
//        spaces.acquire();  // Step 1: Wait until there's space in the buffer
//        mutex.acquire();   // Step 2: Lock the critical section (only one thread can add at a time)
//        try {
//            buffer.add(element); // Step 3: Actually add the element
//        } finally {
//            mutex.release(); // Step 4: Unlock the critical section
//        }
//        items.release();   // Step 5: Signal that a new item is availab
//    */
//
//
//
//
//
//
//
///*
//public BufElement consume() throws InterruptedException {
//        items.acquire();  // Step 1: Wait for an available item
//        mutex.acquire();   // Step 2: Lock the critical section
//        BufElement element;
//        try {
//            element = buffer.remove(0);  // Step 3: Take the first item (FIFO)
//        } finally {
//            mutex.release(); // Step 4: Unlock the critical section
//        }
//        spaces.release();  // Step 5: Signal that space is now available
//        return element
//*/
//
//
///*
//
/// A generic bounded buffer implementation using semaphores.
//class Buffer<T> {
//    private final Queue<T> queue;
//    private final Semaphore availableItems;
//    private final Semaphore availableSpaces;
//    private final Semaphore mutex;
//    private final int capacity;
//
//    public Buffer(int capacity) {
//        this.capacity = capacity;
//        this.queue = new LinkedList<>();
//        this.availableItems = new Semaphore(0);
//        this.availableSpaces = new Semaphore(capacity);
//        this.mutex = new Semaphore(1);
//    }
//
//    public void produce(T item) throws InterruptedException {
//        availableSpaces.acquire();     // Wait for a free slot.
//        mutex.acquire();               // Ensure exclusive access.
//        queue.add(item);
//        mutex.release();
//        availableItems.release();      // Signal that an item is available.
//    }
//
//    public T consume() throws InterruptedException {
//        availableItems.acquire();      // Wait for an item to be available.
//        mutex.acquire();               // Ensure exclusive access.
//        T item = queue.poll();
//        mutex.release();
//        availableSpaces.release();     // Signal that a slot is now free.
//        return item;
//    }
//}
//
//*/