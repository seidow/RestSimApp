public class Waiter implements Consumer, Runnable {
    private final Buffer mealBuffer;
    private final TableBuffer tableBuffer;

    public Waiter(Buffer mealBuffer, TableBuffer tableBuffer) {
        this.mealBuffer = mealBuffer;
        this.tableBuffer = tableBuffer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                BufElement meal = consume();
                if (meal instanceof OrderedMeal om) {
                    System.out.printf("[%s] Waiter serves %s to Customer %d at Table %d%n",
                            om.getArrivalTime(), om.getOrder(), om.getCustomerID(), om.getTableNumber());

                    Thread.sleep(5000); // Simulate eating time

                    // Notify table is available again
                    tableBuffer.consumAt(om);
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
}
