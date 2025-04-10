import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class RestSimApp {

    private static final int BUFFER_CAPACITY = 10;

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            // Read simulation configuration
            String[] config = reader.readLine().split(" ");
            int numChefs = Integer.parseInt(config[0].split("=")[1]);
            int numWaiters = Integer.parseInt(config[1].split("=")[1]);
            int numTables = Integer.parseInt(config[2].split("=")[1]);

            System.out.printf("Simulation Started with %d Chefs, %d Waiters, and %d Tables.%n",
                    numChefs, numWaiters, numTables);

            // Read meal preparation times
            Map<String, Integer> mealTimes = parseMealTimes(reader.readLine());

            // Initialize buffers
            Buffer orderBuffer = new Buffer(BUFFER_CAPACITY);
            Buffer mealBuffer = new Buffer(BUFFER_CAPACITY);
            TableBuffer tableBuffer = new TableBuffer(numTables);

            // Fill tables
            for (int i = 1; i <= numTables; i++) {
                tableBuffer.produce(new BufElement(-1, "", "", i) {});
            }

            // Thread pool
            ExecutorService executor = Executors.newFixedThreadPool(numChefs + numWaiters + 20);

            // Start Chef threads
            for (int i = 0; i < numChefs; i++) {
                executor.submit(new Chef(orderBuffer, mealBuffer, mealTimes));
            }

            // Start Waiter threads
            for (int i = 0; i < numWaiters; i++) {
                executor.submit(new Waiter(mealBuffer, tableBuffer));
            }

            // Read and submit customer tasks (without delays)
            List<Customer> customers = readCustomers(reader);
            customers.sort(Comparator.comparing(Customer::getArrivalTime));

            for (Customer c : customers) {
                executor.submit(new CustKiosk(orderBuffer, tableBuffer, c));
            }

            // Shutdown simulation
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.HOURS);

            System.out.println("[End of Simulation]");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> parseMealTimes(String line) {
        Map<String, Integer> mealTimes = new HashMap<>();
        String[] meals = line.split(" ");
        for (String meal : meals) {
            String[] parts = meal.split("=");
            String mealName = parts[0];
            int time = Integer.parseInt(parts[1].split(":")[1]); // assume MM only
            mealTimes.put(mealName, time);
        }
        return mealTimes;
    }

    private static List<Customer> readCustomers(BufferedReader reader) throws Exception {
        List<Customer> customers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int id = Integer.parseInt(parts[0].split("=")[1]);
            String arrival = parts[1].split("=")[1];
            String order = parts[2].split("=")[1];
            customers.add(new Customer(id, arrival, order));
        }
        return customers;
    }
}
