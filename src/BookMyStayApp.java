import java.util.*;

// Add-On Service (Represents optional service)
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    public void display() {
        System.out.println(serviceName + " - ₹" + cost);
    }
}

// Reservation (Simplified with ID)
class Reservation {
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
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map: Reservation ID -> List of Services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to a reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);

        System.out.println("Added service: " + service.getServiceName() +
                " to Reservation ID: " + reservationId);
    }

    // View services for a reservation
    public void viewServices(String reservationId) {
        System.out.println("\nServices for Reservation ID: " + reservationId);

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService service : services) {
            service.display();
        }
    }

    // Calculate total cost of add-ons
    public double calculateTotalCost(String reservationId) {
        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null) return 0;

        double total = 0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Create reservations (Already confirmed bookings)
        Reservation r1 = new Reservation("RES101", "Alice", "Single");
        Reservation r2 = new Reservation("RES102", "Bob", "Suite");

        // Step 2: Create Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa Access", 1500);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 800);

        // Step 3: Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Step 4: Guest selects services
        manager.addService(r1.getReservationId(), breakfast);
        manager.addService(r1.getReservationId(), spa);

        manager.addService(r2.getReservationId(), airportPickup);

        // Step 5: View services
        manager.viewServices(r1.getReservationId());
        manager.viewServices(r2.getReservationId());

        // Step 6: Calculate cost
        System.out.println("\nTotal Add-On Cost for " + r1.getReservationId() +
                ": ₹" + manager.calculateTotalCost(r1.getReservationId()));

        System.out.println("Total Add-On Cost for " + r2.getReservationId() +
                ": ₹" + manager.calculateTotalCost(r2.getReservationId()));
    }
}