public abstract class BufElement {
    private final int customerID;
    private final String arrivalTime;
    private final int tableNumber;
    private Buffer buffer;
    private String order;
    public BufElement(int customerID, String order, String arrivalTime, int tableNumber) {
        this.customerID = customerID;
        this.arrivalTime = arrivalTime;
        this.tableNumber = tableNumber;
        this.order = order;
    }

    public int getCustomerID() { return customerID; }
    public String getArrivalTime() { return arrivalTime; }
    public int getTableNumber(){
        return tableNumber;
    }
    public String getOrder(){
        return order;
    }
}