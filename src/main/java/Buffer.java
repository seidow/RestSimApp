
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