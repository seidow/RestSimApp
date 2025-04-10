public class CustKiosk implements Producer, Runnable {
    private final OrderedMeal orderBuffer;
    private final TableBuffer tableBuffer;
    private final Customer customer;

    public CustKiosk(OrderedMeal orderBuffer, TableBuffer tableBuffer, Customer customer) {
        this.orderBuffer = orderBuffer;
        this.tableBuffer = tableBuffer;
        this.customer = customer;
    }

    @Override
    public void run() {
        try {
            // Wait for an available table
            BufElement table = tableBuffer.consume();

            // Customer is seated
            System.out.printf("[%s] Customer %d seated at Table %d%n",
                    customer.getArrivalTime(), customer.getCustomerID(), table.getTableNumber());

            // Create order
            OrderedMeal order = new OrderedMeal(
                    customer.getCustomerID(),
                    customer.getOrder(),
                    customer.getArrivalTime(),
                    table.getTableNumber()
            );

            // Place the order
            produce(order);

            System.out.printf("[%s] Customer %d orders: %s%n",
                    customer.getArrivalTime(), customer.getCustomerID(), customer.getOrder());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void produce(BufElement item) throws InterruptedException {
//        orderBuffer.produce(item);
        
    }
}
