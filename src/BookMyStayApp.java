import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation
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

// Inventory Service
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrementRoom(String type) throws InvalidBookingException {
        int count = inventory.getOrDefault(type, 0);

        if (count <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + type);
        }

        inventory.put(type, count - 1);
    }
}

// Validator (Fail-Fast)
class InvalidBookingValidator {

    public static void validate(Reservation reservation, InventoryService inventory)
            throws InvalidBookingException {

        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
        }

        if (reservation.getNights() <= 0) {
            throw new InvalidBookingException("Number of nights must be greater than zero.");
        }

        if (inventory.getAvailability(reservation.getRoomType()) <= 0) {
            throw new InvalidBookingException("No availability for room type: "
                    + reservation.getRoomType());
        }
    }
}

// Booking Service
class BookingService {

    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void processBooking(Reservation reservation) {

        try {
            // Step 1: Validate input (Fail-Fast)
            InvalidBookingValidator.validate(reservation, inventory);

            // Step 2: Proceed with allocation
            inventory.decrementRoom(reservation.getRoomType());

            // Step 3: Confirm booking
            System.out.println("Booking SUCCESS for " + reservation.getGuestName()
                    + " [Room: " + reservation.getRoomType() + "]");

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking FAILED: " + e.getMessage());
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Single", 1);
        inventory.addRoom("Double", 0);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Test Cases

        // Valid booking
        Reservation r1 = new Reservation("Alice", "Single", 2);

        // Invalid room type
        Reservation r2 = new Reservation("Bob", "Suite", 1);

        // Invalid nights
        Reservation r3 = new Reservation("Charlie", "Single", 0);

        // No availability
        Reservation r4 = new Reservation("Diana", "Double", 1);

        // Empty guest name
        Reservation r5 = new Reservation("", "Single", 1);

        // Step 4: Process bookings
        bookingService.processBooking(r1);
        bookingService.processBooking(r2);
        bookingService.processBooking(r3);
        bookingService.processBooking(r4);
        bookingService.processBooking(r5);
    }
}