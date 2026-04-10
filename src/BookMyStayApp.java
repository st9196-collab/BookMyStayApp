import java.util.*;

// Reservation Request
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Shared Inventory (THREAD SAFE)
class InventoryService {

    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {}

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public synchronized boolean allocateRoom(String roomType, String guestName) {

        int available = inventory.getOrDefault(roomType, 0);

        System.out.println(Thread.currentThread().getName()
                + " attempting booking for " + guestName
                + " | Room: " + roomType
                + " | Available: " + available);

        // Critical Section starts
        if (available <= 0) {
            System.out.println("❌ Booking FAILED for " + guestName + " (No rooms left)");
            return false;
        }

        // Simulate processing delay (exposes race condition if not synchronized)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.put(roomType, available - 1);

        System.out.println("✅ Booking SUCCESS for " + guestName
                + " | Room allocated: " + roomType
                + " | Remaining: " + (available - 1));

        return true;
    }

    public void showInventory() {
        System.out.println("\nFinal Inventory State:");
        for (String key : inventory.keySet()) {
            System.out.println(key + " -> " + inventory.get(key));
        }
    }
}

// Booking Processor Thread
class BookingThread extends Thread {

    private Queue<Reservation> queue;
    private InventoryService inventoryService;

    public BookingThread(Queue<Reservation> queue,
                         InventoryService inventoryService,
                         String name) {
        super(name);
        this.queue = queue;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run() {

        while (true) {

            Reservation reservation = null;

            // Synchronize queue access (shared resource)
            synchronized (queue) {
                if (!queue.isEmpty()) {
                    reservation = queue.poll();
                }
            }

            if (reservation == null) {
                break;
            }

            inventoryService.allocateRoom(
                    reservation.getRoomType(),
                    reservation.getGuestName()
            );
        }
    }
}

// Main Class
public class BookMyStayApp{

    public static void main(String[] args) throws InterruptedException {

        // Step 1: Shared Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 2);
        inventory.addRoom("Double", 1);

        // Step 2: Shared Booking Queue
        Queue<Reservation> queue = new LinkedList<>();

        queue.add(new Reservation("Alice", "Single"));
        queue.add(new Reservation("Bob", "Single"));
        queue.add(new Reservation("Charlie", "Single"));
        queue.add(new Reservation("Diana", "Double"));
        queue.add(new Reservation("Eve", "Double"));

        // Step 3: Create multiple threads (simulating users)
        Thread t1 = new BookingThread(queue, inventory, "Thread-1");
        Thread t2 = new BookingThread(queue, inventory, "Thread-2");
        Thread t3 = new BookingThread(queue, inventory, "Thread-3");

        // Step 4: Start threads concurrently
        t1.start();
        t2.start();
        t3.start();

        // Step 5: Wait for completion
        t1.join();
        t2.join();
        t3.join();

        // Step 6: Final state
        inventory.showInventory();
    }
}