public class TableBuffer extends Buffer {
    public TableBuffer(int capacity) {
        super(capacity);
    }

    // Called by Waiter to mark table as available again
    public void consumAt(OrderedMeal meal) throws InterruptedException {
        BufElement table = new BufElement(-1, "TABLE", "N/A", meal.getTableNumber()) {};
        super.produce(table);

        System.out.printf("[%s] Table %d is now available again (from Customer %d)%n",
                meal.getArrivalTime(), meal.getTableNumber(), meal.getCustomerID());
    }
}














//public class TableBuffer extends Buffer {
//    public TableBuffer(int capacity) {
//        super(capacity);
//    }
//
//    // Called by Waiter to mark table as available again
//    public void consumeAt(OrderedMeal om) throws InterruptedException {
//        //TODO1: Get table number from the OrderedMeal buffer
//        // tableID om.tableNumber
//        /*
//         BufElement freedTable = new BufElement(
//            orderedMeal.getCustomerID(),   // Could be -1 if not needed
//            orderedMeal.getOrder(),  // Could be null if not needed
//            orderedMeal.getArrivalTime(),  // Can keep for timestamp/logging
//            orderedMeal.getTableNumber()   // The important part
//        ) {};
//        */
//
//        System.out.printf("[%s] Table %d is now available again (from Customer %d)%n",
//                meal.getArrivalTime(), meal.getTableNumber(), meal.getCustomerID());
//    }
//}
//
///**
// * A specialized Buffer that supports removing a specific item at a known index
// * (e.g., freeing a table that matches a particular customer).
// *
// * This is one suggested solution for the design problem where
// * the buffer is FIFO but must also allow remove-at-customer's-table.
// */
//public class TableBuffer extends Buffer {
//
//    public TableBuffer(int capacity) {
//        super(capacity);
//    }
//
//    /**
//     * Removes and returns the BufElement at the specified index.
//     * In a real system, you would find the matching "table" or "customer order"
//     * in the buffer and remove exactly that. This method is a simple example.
//     *
//     * Note that we still need to coordinate using semaphores to avoid
//     * concurrency issues.
//     */
//    public BufElement consumeAt(int index) throws InterruptedException {
//        // We need to wait for items to be available, 
//        // but conceptually, we might also search for the correct item.
//        items.acquire();         
//        mutex.acquire();         
//        try {
//            // A real system might do a more careful search,
//            // ensuring the item at 'index' is truly the correct one to remove.
//            BufElement elem = elements.remove(index);
//            return elem;
//        } finally {
//            mutex.release();
//            spaces.release(); 
//        }
//    }
//}
//
//
///*
//public class TableBuffer extends Buffer {
//
//    public TableBuffer(int capacity) {
//        super(capacity); // Initialize the buffer with the number of available tables
//    }
//
//    // This method is used by the waiter to free a table after the customer finishes their meal
//    public void consumeAt(OrderedMeal orderedMeal) throws InterruptedException {
//        // Re-add the table as a generic BufElement with minimal required data
//        BufElement freedTable = new BufElement(
//            orderedMeal.getCustomerID(),   // Could be -1 if not needed
//            orderedMeal.getOrder(),  // Could be null if not needed
//            orderedMeal.getArrivalTime(),  // Can keep for timestamp/logging
//            orderedMeal.getTableNumber()   // The important part
//        ) {};
//
//        // Use inherited produce method to make the table available again
//        produce(freedTable);
//
//        System.out.printf("[%s] Customer %d finishes their meal. Table %d is now available.%n",
//            orderedMeal.getArrivalTime(), orderedMeal.getCustomerID(), orderedMeal.getTableNumber());
//    }
//}
//*/
//
///**
// * TableBuffer class represents a collection of tables in the restaurant.
// * It allows clearing a table used by a customer after they finish their meal.
// */
////import java.util.ArrayList;
////import java.util.List;
////
////public class TableBuffer {
////    private final List<BufElement> tables;
////
////    /**
////     * Constructs the TableBuffer with a fixed number of BufElement tables.
////     * @param capacity The number of tables available in the restaurant.
////     */
////    public TableBuffer(int capacity) {
////        tables = new ArrayList<>();
////        for (int i = 0; i < capacity; i++) {
////            tables.add(new BufElement(i));
////        }
////    }
////    
////    /**
////     * Frees the table used by the customer who ordered the given meal.
////     * This is called by a waiter after the customer finishes eating.
////     * 
////     * @param om the OrderedMeal object, which includes the table ID.
////     * @return the same OrderedMeal after table is cleared.
////     */
////    public BufElement consumeAt(OrderedMeal om) {
////        int tableId = om.getTableId();
////        if (tableId >= 0 && tableId < tables.size()) {
////            BufElement table = tables.get(tableId);
////            table.setOccupied(false);  // Mark the table as free
////            System.out.println("Table " + tableId + " is now available.");
////            return table;
////        } else {
////            System.err.println("Invalid table ID: " + tableId);
////            return null;
////        }
////    }
////}