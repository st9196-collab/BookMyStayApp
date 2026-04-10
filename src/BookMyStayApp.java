import java.util.*;

// Reservation (from Use Case 5)
class Reservation {
    private String guestName;
    private String roomType;
    private int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }
}

// Inventory Service (STATE + MUTATION)
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Update inventory immediately after allocation
    public void decrementRoom(String type) {
        int count = inventory.getOrDefault(type, 0);
        if (count > 0) {
            inventory.put(type, count - 1);
        }
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO removal
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (CORE LOGIC)
class BookingService {

    private InventoryService inventoryService;

    // Tracks all allocated room IDs (GLOBAL UNIQUENESS)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Maps room type -> allocated room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Generate unique room ID
    private String generateRoomId(String roomType) {
        String roomId;
        do {
            roomId = roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
        } while (allocatedRoomIds.contains(roomId)); // Ensure uniqueness

        return roomId;
    }

    // Process booking (ATOMIC LOGICAL UNIT)
    public void processBooking(Reservation reservation) {

        String roomType = reservation.getRoomType();

        System.out.println("\nProcessing booking for: " + reservation.getGuestName());

        // Step 1: Check availability
        if (inventoryService.getAvailability(roomType) <= 0) {
            System.out.println("Booking FAILED: No rooms available for " + roomType);
            return;
        }

        // Step 2: Generate unique room ID
        String roomId = generateRoomId(roomType);

        // Step 3: Assign room (update structures together)
        allocatedRoomIds.add(roomId);

        roomAllocations.putIfAbsent(roomType, new HashSet<>());
        roomAllocations.get(roomType).add(roomId);

        // Step 4: Update inventory immediately
        inventoryService.decrementRoom(roomType);

        // Step 5: Confirm booking
        System.out.println("Booking CONFIRMED!");
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Room Type: " + roomType);
        System.out.println("Assigned Room ID: " + roomId);
    }

    public void displayAllocations() {
        System.out.println("\nRoom Allocations:");
        for (String type : roomAllocations.keySet()) {
            System.out.println(type + " -> " + roomAllocations.get(type));
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 2);
        inventory.addRoom("Double", 1);

        // Step 2: Setup Queue
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single", 2));
        queue.addRequest(new Reservation("Bob", "Single", 1));
        queue.addRequest(new Reservation("Charlie", "Single", 3)); // Should fail
        queue.addRequest(new Reservation("Diana", "Double", 2));

        // Step 3: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 4: Process requests in FIFO order
        while (!queue.isEmpty()) {
            Reservation r = queue.getNextRequest();
            bookingService.processBooking(r);
        }

        // Step 5: Display final state
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}