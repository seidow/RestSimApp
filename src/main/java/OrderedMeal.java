// Represents an ordered meal (a type of BufElement)
public class OrderedMeal extends BufElement {
    public OrderedMeal(int customerID, String order, String arrivalTime, int tableNumber) {
        super(customerID, order, arrivalTime, tableNumber);
    }
}
