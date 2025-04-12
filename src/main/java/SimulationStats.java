import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

class SimulationStats {
    private final Map<Integer, CustomerRecord> records = new ConcurrentHashMap<>();

    static class CustomerRecord {
        long arrivalTime = -1;
        long seatedTime = -1;
        long orderTime = -1;
        long mealReadyTime = -1;
        long departureTime = -1;
    }

    public void recordArrival(int customerId, String arrivalTime) {
        CustomerRecord cr = new CustomerRecord();
        cr.arrivalTime = convertTimeToMinutes(arrivalTime);
        records.put(customerId, cr);
    }

    public void recordSeated(int customerId, String time) {
        updateRecord(customerId, time, (cr, t) -> cr.seatedTime = t);
    }

    public void recordOrder(int customerId, String time) {
        updateRecord(customerId, time, (cr, t) -> cr.orderTime = t);
    }

    public void recordMealReady(int customerId, String time) {
        updateRecord(customerId, time, (cr, t) -> cr.mealReadyTime = t);
    }

    public void recordDeparture(int customerId, String time) {
        updateRecord(customerId, time, (cr, t) -> cr.departureTime = t);
    }

    private void updateRecord(int customerId, String time, 
                             BiConsumer<CustomerRecord, Long> updater) {
        CustomerRecord cr = records.get(customerId);
        if (cr == null) {
            throw new IllegalStateException("Customer " + customerId + " not found");
        }
        updater.accept(cr, convertTimeToMinutes(time));
    }

    private long convertTimeToMinutes(String time) {
        if (!time.matches("^\\d{2}:\\d{2}$")) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }
        
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Invalid time value: " + time);
        }
        
        return (long) hours * 60 + minutes;
    }

    public void printSummary() {
        if (records.isEmpty()) {
            System.out.println("No customers processed");
            return;
        }

        // Calculate time bounds
        long firstArrival = records.values().stream()
            .mapToLong(r -> r.arrivalTime)
            .min().orElse(0);

        long lastDeparture = records.values().stream()
            .filter(r -> r.departureTime != -1)
            .mapToLong(r -> r.departureTime)
            .max().orElse(firstArrival);

        // Calculate averages
        double avgTableWait = records.values().stream()
            .filter(r -> r.seatedTime != -1)
            .mapToLong(r -> r.seatedTime - r.arrivalTime)
            .average().orElse(0);

        double avgPrepTime = records.values().stream()
            .filter(r -> r.orderTime != -1 && r.mealReadyTime != -1)
            .mapToLong(r -> r.mealReadyTime - r.orderTime)
            .average().orElse(0);

        // Print results
        System.out.println("\nSummary:");
        System.out.println("Total Customers Served: " + records.size());
        System.out.printf("Average Wait Time for Table: %.1f Minutes%n", avgTableWait);
        System.out.printf("Average Order Preparation Time: %.1f Minutes%n", avgPrepTime);
        System.out.println("Total Simulation Time: " + (lastDeparture - firstArrival) + " Minutes");
    }
}