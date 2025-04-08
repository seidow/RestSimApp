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
    public BufElement consume() throws InterruptedException {
        return orderBuffer.consume();
    }

    @Override
    public void produce(BufElement item) throws InterruptedException {
        mealBuffer.produce(item);
    }

    @Override
    public void run() {
        try {
            while (running) {
                BufElement order = consume();
                if (order instanceof OrderedMeal) {
                    OrderedMeal orderedMeal = (OrderedMeal) order;
                    String mealName = orderedMeal.getOrder();
                    int prepTime = mealTimes.get(mealName) * 1000; // Convert to milliseconds

                    // Simulate preparation time
                    Thread.sleep(prepTime);

                    // Produce cooked meal
                    produce(orderedMeal);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        running = false;
    }
}