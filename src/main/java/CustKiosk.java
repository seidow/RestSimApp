public class CustKiosk implements Producer, Runnable {
    private final Buffer orderBuffer;        // Buffer to place orders for chefs
    private final TableBuffer tableBuffer;   // Buffer to manage table availability
    private final Customer customer;         // Customer associated with this kiosk

    public CustKiosk(Buffer orderBuffer, TableBuffer tableBuffer, Customer customer) {
        this.orderBuffer = orderBuffer;
        this.tableBuffer = tableBuffer;
        this.customer = customer;
    }

    @Override
    public void produce(BufElement item) throws InterruptedException {
        orderBuffer.produce(item); // Add the order to the order buffer
    }

    @Override
    public void run() {
        try {
            // Step 1: Wait until a table is available
            BufElement table = tableBuffer.consume();

            int tableNumber = table.getTableNumber(); // Extract the table number

            // Step 2: Create an order with customer and table details
            OrderedMeal order = new OrderedMeal(
                customer.getCustomerID(),
                customer.getOrder(),
                customer.getArrivalTime(),
                tableNumber
            );

            // Step 3: Place the order in the order buffer for chefs to consume
            produce(order);

            // Log the event
            System.out.printf("[%s] Customer %d is seated at Table %d and placed an order: %s%n",
                customer.getArrivalTime(), customer.getCustomerID(), tableNumber, customer.getOrder());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption cleanly
        }
    }
}
