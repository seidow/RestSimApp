public class TableBuffer extends Buffer {
    public TableBuffer(int capacity) {
        super(capacity);
        initializeTables(capacity);
    }

    private void initializeTables(int capacity) {
        try {
            for (int i = 1; i <= capacity; i++) {
                // Use current simulation time as initial timestamp
                super.produce(new BufElement(-1, "TABLE", "00:00", i) {}); 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Corrected method name and time tracking
    public void consumeAt(OrderedMeal meal) throws InterruptedException {
        String currentTime = RestSimApp.getCurrentSimulationTime();
        BufElement table = new BufElement(-1, "TABLE", currentTime, meal.getTableNumber()) {};
        super.produce(table);
    }
}
