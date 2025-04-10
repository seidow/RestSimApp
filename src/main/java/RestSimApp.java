
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class RestSimApp {


    public static void main(String[] args) {
        
        Thread restThread = new Thread();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            // Read simulation configuration
            String[] config = reader.readLine().split(" ");
            int numChefs = Integer.parseInt(config[0].split("=")[1]);
            int numWaiters = Integer.parseInt(config[1].split("=")[1]);
            int numTables = Integer.parseInt(config[2].split("=")[1]);

            System.out.printf("Simulation Started with %d Chefs, %d Waiters, and %d Tables.%n",
                    numChefs, numWaiters, numTables);

            // Burger=00:8 Pizza=00:10 Pasta=00:10 Salad=00:5 Steak=00:8 Sushi=00:5 Tacos=00:5 Soup=00:10
//
            // Read meal preparation times
            Map<String, Integer> mealTimes = parseMealTimes(reader.readLine());

            
            Map<String, String> customerInfo = parseCustomerInfo(reader.readLine());
            ////////////// CustomerID=1 ArrivalTime=08:00 Order=Burger
            int customerID = Integer.parseInt(customerInfo.get("CustomerID"));
            String arrivalTime = customerInfo.get("ArrivalTime");
            String order = customerInfo.get("Order");
            ///print customer infor
            System.out.println("[" + arrivalTime + "] " + "Customer " + customerID + " Arrived");
            /// Customer waits a table
            ///
            TableBuffer tables = new TableBuffer(numTables);
            Customer customer = new Customer(customerID, arrivalTime, order);
            tables.consume();
            OrderedMeal orderBuffer = new OrderedMeal(customerID , order, arrivalTime, numTables);
            System.out.println("[" + arrivalTime + "] " + "Customer " + customerID + " has seated in table " + orderBuffer.getTableNumber());
            CustKiosk customerKiosk = new CustKiosk(orderBuffer, tables, customer);
            
        
            // Takes arrival time and preparation time and adds together
//        String Caculate = addMinutesToTime("18:15", mealTimes.get("Burger"));

       
//
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private static Map<String, Integer> parseMealTimes(String line) {
        Map<String, Integer> mealTimes = new HashMap<>();
        String[] meals = line.split(" "); // Split the line into individual meal entries

        for (String meal : meals) {
            String[] parts = meal.split("=");     // e.g., "Burger=00:08" -> ["Burger", "00:08"]
            String mealName = parts[0];           // Meal name: "Burger"
            String[] timeParts = parts[1].split(":");
            int hours = Integer.parseInt(timeParts[0]);   // Usually 0
//        System.out.println("Hours: " + hours);
            int minutes = Integer.parseInt(timeParts[1]); // e.g., 8
//        System.out.println("Minutes: " + minutes);

            int totalMinutes = hours * 60 + minutes;      // Convert to total minutes
            mealTimes.put(mealName, totalMinutes);        // Store in the map
        }

        return mealTimes;
    }
    /// Function that parses customer information
     private static Map<String, String> parseCustomerInfo(String line) {
        Map<String, String> customerInfo = new HashMap<>();
        String[] customer = line.split(" "); // Split the line into individual meal entries
        for (String cust : customer) {
  
            String[] parts = cust.split("=");     
            String custKey = parts[0];           // custKey : "CustomerID"
            String custKeyValue = parts[1];

            customerInfo.put(custKey, custKeyValue);        // Store in the map
        }

        return customerInfo;
    }
    

    public static String addMinutesToTime(String startTime, int minutesToAdd) {
        String[] parts = startTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        int totalMinutes = hour * 60 + minute + minutesToAdd;

        int newHour = totalMinutes / 60;
        int newMinute = totalMinutes % 60;

        return String.format("%02d:%02d", newHour, newMinute);
    }
}
