import java.util.*;

// Domain Model: Room
class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + String.join(", ", amenities));
        System.out.println("---------------------------");
    }
}

// Inventory: Holds availability (STATE HOLDER)
class Inventory {
    private Map<String, Integer> availabilityMap = new HashMap<>();

    public void addRoom(String type, int count) {
        availabilityMap.put(type, count);
    }

    // Read-only access
    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(availabilityMap); // Defensive programming
    }
}

// Service: Search (READ-ONLY)
class SearchService {

    public static void searchAvailableRooms(Inventory inventory, Map<String, Room> roomCatalog) {
        System.out.println("Available Rooms:\n");

        Map<String, Integer> availability = inventory.getAllAvailability();

        for (String roomType : availability.keySet()) {

            int count = availability.get(roomType);

            // Validation Logic: Only show available rooms
            if (count > 0) {

                Room room = roomCatalog.get(roomType);

                if (room != null) { // Defensive check
                    room.displayDetails();
                    System.out.println("Available Count: " + count);
                    System.out.println("===========================");
                }
            }
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Create Room Catalog (Domain Layer)
        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single",
                new Room("Single", 2000,
                        Arrays.asList("WiFi", "TV", "AC")));

        roomCatalog.put("Double",
                new Room("Double", 3500,
                        Arrays.asList("WiFi", "TV", "AC", "Mini Bar")));

        roomCatalog.put("Suite",
                new Room("Suite", 6000,
                        Arrays.asList("WiFi", "TV", "AC", "Mini Bar", "Jacuzzi")));

        // Step 2: Setup Inventory (STATE)
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0); // Should NOT appear
        inventory.addRoom("Suite", 2);

        // Step 3: Guest triggers search (READ-ONLY OPERATION)
        SearchService.searchAvailableRooms(inventory, roomCatalog);
    }
}