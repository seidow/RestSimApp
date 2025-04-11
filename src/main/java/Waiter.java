import java.util.Random;
import java.util.concurrent.*;

public class Waiter implements Consumer, Runnable {
    private final Buffer mealBuffer;
    private final TableBuffer tableBuffer;
    private final int waiterID;
    private final long timeMultiplier;
    private final ScheduledExecutorService scheduler;
    private final Random random; // For generating random eating durations

    public Waiter(Buffer mealBuffer, TableBuffer tableBuffer, int waiterID, long timeMultiplier) {
        this.mealBuffer = mealBuffer;
        this.tableBuffer = tableBuffer;
        this.waiterID = waiterID;
        this.timeMultiplier = timeMultiplier;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.random = new Random(); // Initialize random generator
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                BufElement meal = consume();
                if (meal instanceof OrderedMeal om) {
                    String readyTime = om.getReadyTime();

                    System.out.printf("[%s] Waiter %d serves %s to Customer %d at Table %d%n",
                            readyTime, waiterID, om.getOrder(), om.getCustomerID(), om.getTableNumber());

                    // Generate random eating duration (5-20 minutes)
                    int eatingDuration = random.nextInt(16) + 5; // 5 <= x <= 20
                    String leaveTime = Chef.addMinutesToTime(readyTime, eatingDuration);
                    long delay = eatingDuration * timeMultiplier;

                    scheduler.schedule(() -> {
                        System.out.printf("[%s] Customer %d finishes eating and leaves the restaurant.%n",
                                leaveTime, om.getCustomerID());
                        try {
                            tableBuffer.consumAt(om);
                            System.out.printf("[%s] Table %d is now available.%n",
                                    leaveTime, om.getTableNumber());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }, delay, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public BufElement consume() throws InterruptedException {
        return mealBuffer.consume();
    }

    // Shutdown hook for the scheduler
    public void shutdown() {
        scheduler.shutdown();
    }
}
