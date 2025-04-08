import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class RestSimApp {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            // Read configuration
            String[] config = reader.readLine().split(" ");
            int numChefs = Integer.parseInt(config[0].split("=")[1]);
            int numWaiters = Integer.parseInt(config[1].split("=")[1]);
            int numTables = Integer.parseInt(config[2].split("=")[1]);

            // Meal preparation times
            Map<String, Integer> mealTimes = new HashMap<>();
            String[] meals = reader.readLine().split(" ");
            for (String meal : meals) {
                String[] parts = meal.split("=");
                mealTimes.put(parts[0], Integer.parseInt(parts[1].split(":")[1]));
            }

            // Initialize buffers
            Buffer orderBuffer = new Buffer(10);
            Buffer mealBuffer = new Buffer(10);
            TableBuffer tableBuffer = new TableBuffer(numTables);
            for (int i = 1; i <= numTables; i++) {
                tableBuffer.produce(new TableElement(i));
            }

            // Thread pool
            ExecutorService executor = Executors.newFixedThreadPool(numChefs + numWaiters + 10); // Adjust as needed

            // Start chefs and waiters
            for (int i = 0; i < numChefs; i++) {
                executor.submit(new Chef(orderBuffer, mealBuffer, mealTimes));
            }
            for (int i = 0; i < numWaiters; i++) {
                executor.submit(new Waiter(mealBuffer, tableBuffer));
            }

            // Process customers
            String line;
            List<Customer> customers = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                int id = Integer.parseInt(parts[0].split("=")[1]);
                String arrival = parts[1].split("=")[1];
                String order = parts[2].split("=")[1];
                customers.add(new Customer(id, arrival, order, mealTimes));
            }

            // Schedule customer arrivals
            customers.sort(Comparator.comparing(Customer::getArrivalTime));
            long prevTime = 0;
            for (Customer c : customers) {
                long delay = c.getArrivalTimeInMinutes() - prevTime;
                TimeUnit.SECONDS.sleep(delay); // Simulate arrival delay
                executor.submit(new CustKiosk(orderBuffer, tableBuffer, c));
                prevTime = c.getArrivalTimeInMinutes();
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
            System.out.println("[End of Simulation]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}