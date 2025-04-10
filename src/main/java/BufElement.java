// Abstract class representing a buffer element in the restaurant system.
// Each element stores information about a customer's order.
public abstract class BufElement {
    // Unique identifier for the customer.
    private final int customerID;

    // The order placed by the customer (e.g., dish name).
    private final String order;

    // The time the customer placed the order.
    private final String arrivalTime;

    // The table number where the customer is seated.
    private final int tableNumber;

    // Constructor to initialize all attributes of the buffer element.
    public BufElement(int customerID, String order, String arrivalTime, int tableNumber) {
        this.customerID = customerID;
        this.order = order;
        this.arrivalTime = arrivalTime;
        this.tableNumber = tableNumber;
    }

    // Getter method for customer ID.
    public int getCustomerID() {
        return customerID;
    }

    // Getter method for the order string.
    public String getOrder() {
        return order;
    }

    // Getter method for the time the order was placed.
    public String getArrivalTime() {
        return arrivalTime;
    }

    // Getter method for the table number.
    public int getTableNumber() {
        return tableNumber;
    }

    // Override the toString method to provide a readable representation
    // of the buffer element, useful for debugging or logging.
    @Override
    public String toString() {
        return String.format("CustomerID: %d, Order: %s, Time: %s, Table: %d",
                customerID, order, arrivalTime, tableNumber);
    }
}
