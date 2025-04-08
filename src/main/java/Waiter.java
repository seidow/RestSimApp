public class Waiter implements Consumer, Runnable {
    private final Buffer mealBuffer;
    private final TableBuffer tableBuffer;

    public Waiter(Buffer mealBuffer, TableBuffer tableBuffer) {
        this.mealBuffer = mealBuffer;
        this.tableBuffer = tableBuffer;
    }

    @Override
    public BufElement consume() throws InterruptedException {
        return mealBuffer.consume();
    }

    @Override
    public void run() {
        try {
            while (true) {
                BufElement meal = consume();
                if (meal instanceof OrderedMeal) {
                    OrderedMeal orderedMeal = (OrderedMeal) meal;
                    // Simulate serving and eating time
                    Thread.sleep(5000); // Eating time simulation
                    // Release table
                    tableBuffer.produce(new TableElement(orderedMeal.getTableNumber()));
                    System.out.printf("[%s] Customer %d leaves. Table %d is free%n",
                        orderedMeal.getArrivalTime(), orderedMeal.getCustomerID(), orderedMeal.getTableNumber());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}