
public class Customer {
    private final int customerID;
    private final String arrivalTime;
    private final String order; //******?

    // Constructor now accepts the preparation times map
    public Customer(int customerID, String arrivalTime, String order) {
        this.customerID = customerID;
        this.arrivalTime = arrivalTime;
        this.order = order;
    }

    public int getCustomerID() {
        return customerID;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getOrder() {
        return order;
    }

}
