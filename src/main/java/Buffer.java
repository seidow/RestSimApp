import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {
    private final Queue<BufElement> buffer = new LinkedList<>();
    private final Semaphore mutex;      // Controls access to the queue (binary semaphore)
    private final Semaphore spaces;     // Tracks available slots (empty spaces)
    private final Semaphore items;      // Tracks filled slots (items to consume)

    public Buffer(int capacity) {
        this.mutex = new Semaphore(1); // Acts as a lock for mutual exclusion
        this.spaces = new Semaphore(capacity);
        this.items = new Semaphore(0);
    }

    public void produce(BufElement item) throws InterruptedException {
        spaces.acquire(); // Wait for empty space
        mutex.acquire();  // Lock the queue
        try {
            buffer.add(item);
        } finally {
            mutex.release(); // Unlock the queue
        }
        items.release(); // Signal that an item is available
    }

    public BufElement consume() throws InterruptedException {
        items.acquire();  // Wait for an item to consume
        mutex.acquire(); // Lock the queue
        BufElement item;
        try {
            item = buffer.poll();
        } finally {
            mutex.release(); // Unlock the queue
        }
        spaces.release(); // Signal that a space is free
        return item;
    }
}