public class OrderedMeal extends BufElement {
    private String readyTime;

    public OrderedMeal(int customerID, String order, String arrivalTime, int tableNumber) {
        super(customerID, order, arrivalTime, tableNumber);
    }

    public void setReadyTime(String time) {
        this.readyTime = time;
    }

    public String getReadyTime() {
        return this.readyTime;
    }
}