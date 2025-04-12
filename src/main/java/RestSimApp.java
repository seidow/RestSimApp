
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class RestSimApp {

    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    private static final long SIMULATION_TIME_MULTIPLIER = 1000; // 1 second = 1 minute
    private static final SimulationStats stats = new SimulationStats();
    private static long simulationStartTime;
    private static int baseTime;

    public static void main(String[] args) {
        simulationStartTime = System.currentTimeMillis();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            // Read configuration
            String[] config = reader.readLine().split(" ");
            int numChefs = Integer.parseInt(config[0].split("=")[1]);
            int numWaiters = Integer.parseInt(config[1].split("=")[1]);
            int numTables = Integer.parseInt(config[2].split("=")[1]);

            System.out.printf("Simulation Started with %d Chefs, %d Waiters, and %d Tables.%n",
                    numChefs, numWaiters, numTables);

            // Parse meal preparation times
            Map<String, Integer> mealTimes = parseMealTimes(reader.readLine());

            // Read customers and sort by arrival time
            List<Customer> customerList = new ArrayList<>();
            String customerLine;
            while ((customerLine = reader.readLine()) != null) {
                Map<String, String> customerInfo = parseCustomerInfo(customerLine);
                int customerID = Integer.parseInt(customerInfo.get("CustomerID"));
                String arrivalTime = customerInfo.get("ArrivalTime");
                String order = customerInfo.get("Order");
                customerList.add(new Customer(customerID, arrivalTime, order));
            }
            customerList.sort(Comparator.comparing(Customer::getArrivalTime));

            // Setup simulation components
            ExecutorService executor = Executors.newFixedThreadPool(numChefs + numWaiters + customerList.size());
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(customerList.size());

            Buffer orderBuffer = new Buffer(10);
            Buffer mealBuffer = new Buffer(10);
            TableBuffer tables = new TableBuffer(numTables);

            // Start chefs and waiters
            for (int i = 0; i < numChefs; i++) {
                executor.submit(new Chef(orderBuffer, mealBuffer, mealTimes, SIMULATION_TIME_MULTIPLIER, stats));
            }
            List<Waiter> waiters = new ArrayList<>();
            for (int i = 0; i < numWaiters; i++) {
                Waiter waiter = new Waiter(mealBuffer, tables, i + 1, SIMULATION_TIME_MULTIPLIER, stats);
                waiters.add(waiter);
                executor.submit(waiter);
            }

            // Schedule customers with proper delays
            if (!customerList.isEmpty()) {
                String firstArrival = customerList.get(0).getArrivalTime();
                baseTime = parseTimeToMinutes(firstArrival);
                setBaseTime(baseTime);

                for (Customer customer : customerList) {
                    int arrivalMinutes = parseTimeToMinutes(customer.getArrivalTime());
                    long delay = (arrivalMinutes - baseTime) * SIMULATION_TIME_MULTIPLIER;

                    scheduler.schedule(() -> {
                        String currentTime = getCurrentSimulationTime();
                        System.out.printf("[%s] Customer %d Arrives%n",
                                currentTime, customer.getCustomerID());
                        executor.submit(new CustKiosk(orderBuffer, tables, customer, stats));
                    }, delay, TIME_UNIT);

                }

            }
            // Calculate maximum preparation and eating times
            int maxPrepTime = mealTimes.values().stream().mapToInt(Integer::intValue).max().orElse(0);
            int maxEatingTime = 20; // Maximum eating time (from Waiter's random 5-20)

            // Get the last customer's arrival time
            Customer lastCustomer = customerList.get(customerList.size() - 1);
            int lastArrivalMinutes = parseTimeToMinutes(lastCustomer.getArrivalTime());
            long lastDelay = (lastArrivalMinutes - baseTime) * SIMULATION_TIME_MULTIPLIER;

            // Calculate total delay for terminators
            long terminatorDelay = lastDelay + (maxPrepTime + maxEatingTime) * SIMULATION_TIME_MULTIPLIER;
            
            // Schedule termination signals
            scheduler.schedule(() -> {
                try {
                    // Send termination to chefs
                    for (int i = 0; i < numChefs; i++) {
                        orderBuffer.produce(new BufElement(-1, "TERMINATE_CHEF", "00:00", -1) {
                        });
                    }
                    // Send termination to waiters
                    for (int i = 0; i < numWaiters; i++) {
                        mealBuffer.produce(new BufElement(-1, "TERMINATE_WAITER", "00:00", -1) {
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, terminatorDelay, TimeUnit.MILLISECONDS);

// Proceed to shutdown the scheduler
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.HOURS)) {
                    System.err.println("Scheduler timed out.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Shutdown waiter schedulers explicitly
            waiters.forEach(waiter -> {
                waiter.shutdown();
                try {
                    waiter.awaitTermination();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
//            waiters.forEach(Waiter::shutdown);

            System.out.println("[End of Simulation]");
            stats.printSummary();

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
            String[] timeParts = parts[1].split(":");
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            mealTimes.put(mealName, hours * 60 + minutes);
        }
        return mealTimes;
    }

    private static int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public static void setBaseTime(int baseTimeMinutes) {
        baseTime = baseTimeMinutes;
    }

    private static Map<String, String> parseCustomerInfo(String line) {
        Map<String, String> customerInfo = new HashMap<>();
        String[] entries = line.split(" ");
        for (String entry : entries) {
            String[] parts = entry.split("=");
            customerInfo.put(parts[0], parts[1]);
        }
        return customerInfo;
    }

    public static String getCurrentSimulationTime() {
        long elapsedMillis = System.currentTimeMillis() - simulationStartTime;
        long totalSimulationMinutes = elapsedMillis / SIMULATION_TIME_MULTIPLIER;
        long currentTimeMinutes = baseTime + totalSimulationMinutes;
        int hours = (int) (currentTimeMinutes / 60) % 24;
        int minutes = (int) (currentTimeMinutes % 60);
        return String.format("%02d:%02d", hours, minutes);
    }

}
