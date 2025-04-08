import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Buffer {
    private final Queue<BufElement> buffer = new LinkedList<>();
    private final Semaphore mutex = new Semaphore(1);
    private final Semaphore spaces;
    private final Semaphore items = new Semaphore(0);

    public Buffer(int capacity) {
        this.spaces = new Semaphore(capacity);
    }

    public void produce(BufElement item) throws InterruptedException {
        spaces.acquire();
        mutex.acquire();
        buffer.add(item);
        mutex.release();
        items.release();
    }

    public BufElement consume() throws InterruptedException {
        items.acquire();
        mutex.acquire();
        BufElement item = buffer.poll();
        mutex.release();
        spaces.release();
        return item;
    }
}