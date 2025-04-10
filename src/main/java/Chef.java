
import java.util.Map;

public class Chef implements Consumer, Producer, Runnable {
    private final Buffer orderBuffer;
    private final Buffer mealBuffer;
    private final Map<String, Integer> mealTimes;
    private volatile boolean running = true;

    public Chef(Buffer orderBuffer, Buffer mealBuffer, Map<String, Integer> mealTimes) {
        this.orderBuffer = orderBuffer;
        this.mealBuffer = mealBuffer;
        this.mealTimes = mealTimes;
    }

    @Override
    public void run() {
        try {
            while (running) {
                BufElement order = consume();
                if (order instanceof OrderedMeal om) {
                    int prepTime = mealTimes.getOrDefault(om.getOrder(), 5) * 1000;

                    System.out.printf("[%s] Chef starts preparing %s for Customer %d%n",
                            om.getArrivalTime(), om.getOrder(), om.getCustomerID());
                    Thread.sleep(prepTime);
                    System.out.printf("[%s] Chef finishes preparing %s for Customer %d%n",
                            om.getArrivalTime(), om.getOrder(), om.getCustomerID());

                    produce(om);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public BufElement consume() throws InterruptedException {
        return orderBuffer.consume();
    }

    @Override
    public void produce(BufElement item) throws InterruptedException {
        mealBuffer.produce(item);
    }

    public void stop() {
        running = false;
    }
}
