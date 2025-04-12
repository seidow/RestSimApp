public class CustKiosk implements Producer, Runnable {
    private final Buffer orderBuffer;
    private final TableBuffer tableBuffer;
    private final Customer customer;
    private final SimulationStats stats;

    public CustKiosk(Buffer orderBuffer, TableBuffer tableBuffer, Customer customer, SimulationStats stats) {
        this.orderBuffer = orderBuffer;
        this.tableBuffer = tableBuffer;
        this.customer = customer;
        this.stats = stats;
    }

    @Override
    public void run() {
        // Record arrival with simulation time (matches scheduled arrival)
        String simulationArrivalTime = RestSimApp.getCurrentSimulationTime();
        stats.recordArrival(customer.getCustomerID(), simulationArrivalTime);
        
        try {
            BufElement table;
            while ((table = tableBuffer.consume()) == null) {
                Thread.sleep(100); // Wait for table availability
            }

            // Time when customer actually gets seated
            String seatedTime = RestSimApp.getCurrentSimulationTime();
            System.out.printf("[%s] Customer %d seated at Table %d%n",
                    seatedTime, customer.getCustomerID(), table.getTableNumber());
            stats.recordSeated(customer.getCustomerID(), seatedTime);

            // Create order with CORRECT timestamps
            OrderedMeal order = new OrderedMeal(
                    customer.getCustomerID(),
                    customer.getOrder(),
                    seatedTime, // Use seated time as order time, NOT arrival time
                    table.getTableNumber()
            );

            // Log and record order placement
            String orderTime = RestSimApp.getCurrentSimulationTime();
            System.out.printf("[%s] Customer %d orders: %s%n",
                    orderTime, customer.getCustomerID(), customer.getOrder());
            stats.recordOrder(customer.getCustomerID(), orderTime);

            produce(order);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Customer kiosk interrupted.");
        }
    }

    @Override
    public void produce(BufElement item) throws InterruptedException {
        orderBuffer.produce(item);
    }
}