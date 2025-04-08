public class TableBuffer extends Buffer {

    public TableBuffer(int capacity) {
        super(capacity); // Initialize the buffer with the number of available tables
    }

    // This method is used by the waiter to free a table after the customer finishes their meal
    public void consumeAt(OrderedMeal orderedMeal) throws InterruptedException {
        // Re-add the table as a generic BufElement with minimal required data
        BufElement freedTable = new BufElement(
            orderedMeal.getCustomerID(),   // Could be -1 if not needed
            orderedMeal.getOrder(),  // Could be null if not needed
            orderedMeal.getArrivalTime(),  // Can keep for timestamp/logging
            orderedMeal.getTableNumber()   // The important part
        ) {};

        // Use inherited produce method to make the table available again
        produce(freedTable);

        System.out.printf("[%s] Customer %d finishes their meal. Table %d is now available.%n",
            orderedMeal.getArrivalTime(), orderedMeal.getCustomerID(), orderedMeal.getTableNumber());
    }
}
