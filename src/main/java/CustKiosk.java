public class CustKiosk implements Producer, Runnable {
    private final Buffer orderBuffer;
    private final TableBuffer tableBuffer;
    private final Customer customer;

    public CustKiosk(Buffer orderBuffer, TableBuffer tableBuffer, Customer customer) {
        this.orderBuffer = orderBuffer;
        this.tableBuffer = tableBuffer;
        this.customer = customer;
    }

@Override
public void run() {
    try {
        BufElement table = tableBuffer.consume();
        System.out.printf("[%s] Customer %d seated at Table %d%n",
                customer.getArrivalTime(), customer.getCustomerID(), table.getTableNumber());

        OrderedMeal order = new OrderedMeal(
                customer.getCustomerID(),
                customer.getOrder(),
                customer.getArrivalTime(),
                table.getTableNumber()
        );

        produce(order);
        System.out.printf("[%s] Customer %d orders: %s%n",
                customer.getArrivalTime(), customer.getCustomerID(), customer.getOrder());

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
