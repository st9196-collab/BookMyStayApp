import java.io.*;
import java.util.*;

// ----------------------
// Serializable Models
// ----------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String toString() {
        return reservationId + "," + guestName + "," + roomType;
    }

    public static Reservation fromString(String data) {
        String[] parts = data.split(",");
        return new Reservation(parts[0], parts[1], parts[2]);
    }
}

// ----------------------
// Inventory (Serializable)
// ----------------------
class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> stock = new HashMap<>();

    public void addRoom(String type, int count) {
        stock.put(type, count);
    }

    public void decrement(String type) {
        stock.put(type, stock.getOrDefault(type, 0) - 1);
    }

    public void increment(String type) {
        stock.put(type, stock.getOrDefault(type, 0) + 1);
    }

    public Map<String, Integer> getStock() {
        return stock;
    }

    public void display() {
        System.out.println("\nInventory State:");
        for (String key : stock.keySet()) {
            System.out.println(key + " -> " + stock.get(key));
        }
    }
}

// ----------------------
// Persistence Service
// ----------------------
class PersistenceService {

    private static final String FILE_NAME = "bookmystay_state.txt";

    // SAVE STATE (Serialization)
    public static void saveState(Inventory inventory, List<Reservation> reservations) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

            // Save inventory
            writer.println("INVENTORY");
            for (Map.Entry<String, Integer> entry : inventory.getStock().entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }

            // Save reservations
            writer.println("RESERVATIONS");
            for (Reservation r : reservations) {
                writer.println(r.toString());
            }

            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // LOAD STATE (Deserialization)
    public static void loadState(Inventory inventory, List<Reservation> reservations) {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            System.out.println("No previous state found. Starting fresh system.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            boolean isInventory = false;
            boolean isReservation = false;

            while ((line = reader.readLine()) != null) {

                if (line.equals("INVENTORY")) {
                    isInventory = true;
                    isReservation = false;
                    continue;
                }

                if (line.equals("RESERVATIONS")) {
                    isInventory = false;
                    isReservation = true;
                    continue;
                }

                if (isInventory) {
                    String[] parts = line.split(":");
                    inventory.addRoom(parts[0], Integer.parseInt(parts[1]));
                }

                if (isReservation) {
                    reservations.add(Reservation.fromString(line));
                }
            }

            System.out.println("System state restored successfully.");

        } catch (Exception e) {
            System.out.println("Corrupted state file. Starting fresh system.");
        }
    }
}

// ----------------------
// Main Application
// ----------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        Inventory inventory = new Inventory();
        List<Reservation> reservations = new ArrayList<>();

        // STEP 1: LOAD PREVIOUS STATE
        PersistenceService.loadState(inventory, reservations);

        // STEP 2: DISPLAY RECOVERED STATE
        inventory.display();

        System.out.println("\nReservations:");
        for (Reservation r : reservations) {
            System.out.println(r.getReservationId() + " -> "
                    + r.getGuestName() + " (" + r.getRoomType() + ")");
        }

        // STEP 3: SIMULATE NEW BOOKING ACTIVITY
        System.out.println("\nAdding new booking after recovery...");

        Reservation newBooking = new Reservation("RES105", "Alice", "Single");
        reservations.add(newBooking);

        inventory.decrement("Single");

        // STEP 4: SAVE UPDATED STATE
        PersistenceService.saveState(inventory, reservations);

        // STEP 5: FINAL DISPLAY
        inventory.display();
    }
}