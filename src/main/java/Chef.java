
import java.util.Map;

public class Chef implements Consumer, Producer, Runnable {

    private final Buffer orderBuffer;
    private final Buffer mealBuffer;
    private final Map<String, Integer> mealTimes; // Pass total minutes directly, not "00:08" format
    private final long timeMultiplier;

    public Chef(Buffer orderBuffer, Buffer mealBuffer, Map<String, Integer> mealTimes, long timeMultiplier) {
        this.orderBuffer = orderBuffer;
        this.mealBuffer = mealBuffer;
        this.mealTimes = mealTimes;
        this.timeMultiplier = timeMultiplier;
    }

   @Override
    public void run() {
        try {
            // Exit loop when interrupted
            while (!Thread.currentThread().isInterrupted()) {
                BufElement order = consume();
                if (order instanceof OrderedMeal om) {
                    int prepMinutes = mealTimes.getOrDefault(om.getOrder(), 0);
                    String startPrepTime = om.getArrivalTime();
                    String endPrepTime = addMinutesToTime(startPrepTime, prepMinutes);

                    System.out.printf("[%s] Chef starts preparing %s for Customer %d%n",
                            startPrepTime, om.getOrder(), om.getCustomerID());

                    // Simulate preparation time (single sleep)
                    Thread.sleep(prepMinutes * timeMultiplier);

                    om.setReadyTime(endPrepTime);

                    System.out.printf("[%s] Chef finishes preparing %s for Customer %d%n",
                            endPrepTime, om.getOrder(), om.getCustomerID());

                    produce(om);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
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
