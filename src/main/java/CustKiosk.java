
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
        String simulationArrivalTime = RestSimApp.getCurrentSimulationTime();
        stats.recordArrival(customer.getCustomerID(), simulationArrivalTime);
        try {
            BufElement table;
            while ((table = tableBuffer.consume()) == null) {
                Thread.sleep(100);
            }

            String seatedTime = RestSimApp.getCurrentSimulationTime();
            System.out.printf("[%s] Customer %d seated at Table %d%n",
                    seatedTime, customer.getCustomerID(), table.getTableNumber());
            stats.recordSeated(customer.getCustomerID(), seatedTime);

            OrderedMeal order = new OrderedMeal(
                    customer.getCustomerID(),
                    customer.getOrder(),
                    customer.getArrivalTime(),
                    table.getTableNumber()
            );

            produce(order);
            System.out.printf("[%s] Customer %d orders: %s%n",
                    customer.getArrivalTime(), customer.getCustomerID(), customer.getOrder());
            stats.recordOrder(customer.getCustomerID(), simulationArrivalTime);

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
