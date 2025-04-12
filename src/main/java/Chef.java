import java.util.Map;

public class Chef implements Consumer, Producer, Runnable {
    private final Buffer orderBuffer;
    private final Buffer mealBuffer;
    private final Map<String, Integer> mealTimes;
    private final long timeMultiplier;
    private final SimulationStats stats;

    public Chef(Buffer orderBuffer, Buffer mealBuffer, Map<String, Integer> mealTimes, 
                long timeMultiplier, SimulationStats stats) {
        this.orderBuffer = orderBuffer;
        this.mealBuffer = mealBuffer;
        this.mealTimes = mealTimes;
        this.timeMultiplier = timeMultiplier;
        this.stats = stats;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                BufElement order = consume();
                
                // Termination check
                if (order != null && "TERMINATE_CHEF".equals(order.getOrder())) {
                    break; // Exit on termination signal
                }

                if (order instanceof OrderedMeal om) {
                    // Use ACTUAL start time, not customer's arrival time
                    String startTime = RestSimApp.getCurrentSimulationTime();
                    int prepMinutes = mealTimes.getOrDefault(om.getOrder(), 0);

                    // Log start time correctly
                    System.out.printf("[%s] Chef starts preparing %s for Customer %d%n",
                            startTime, om.getOrder(), om.getCustomerID());
                    System.out.flush();

                    // Simulate preparation time (SLEEP ONCE)
                    Thread.sleep(prepMinutes * timeMultiplier);

                    // Calculate end time correctly
                    String endTime = addMinutesToTime(startTime, prepMinutes);
                    om.setReadyTime(endTime);

                    // Log completion
                    System.out.printf("[%s] Chef finishes preparing %s for Customer %d%n",
                            endTime, om.getOrder(), om.getCustomerID());
                    System.out.flush();
                    stats.recordMealReady(om.getCustomerID(), endTime);

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

    public static String addMinutesToTime(String startTime, int minutesToAdd) {
        String[] parts = startTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        int totalMinutes = hour * 60 + minute + minutesToAdd;
        int newHour = totalMinutes / 60;
        int newMinute = totalMinutes % 60;

        return String.format("%02d:%02d", newHour, newMinute);
    }
}
