import java.util.*;

// Reservation (Confirmed Booking)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int nights;
    private double pricePerNight;

    public Reservation(String reservationId, String guestName, String roomType,
                       int nights, double pricePerNight) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
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

    public int getNights() {
        return nights;
    }

    public double getTotalCost() {
        return nights * pricePerNight;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Guest: " + guestName);
        System.out.println("Room Type: " + roomType);
        System.out.println("Nights: " + nights);
        System.out.println("Total Cost: ₹" + getTotalCost());
        System.out.println("---------------------------");
    }
}

// Booking History (STATE HOLDER)
class BookingHistory {
    private List<Reservation> reservations = new ArrayList<>();

    // Add confirmed booking
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        System.out.println("Booking stored: " + reservation.getReservationId());
    }

    // Read-only access
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(reservations);
    }
}

// Reporting Service (READ-ONLY)
class BookingReportService {

    // Display all bookings
    public static void showAllBookings(BookingHistory history) {
        System.out.println("\n=== Booking History ===");

        List<Reservation> reservations = history.getAllReservations();

        if (reservations.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : reservations) {
            r.display();
        }
    }

    // Generate summary report
    public static void generateSummary(BookingHistory history) {
        List<Reservation> reservations = history.getAllReservations();

        int totalBookings = reservations.size();
        double totalRevenue = 0;

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : reservations) {
            totalRevenue += r.getTotalCost();

            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("\n=== Booking Summary Report ===");
        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);

        System.out.println("\nBookings by Room Type:");
        for (String type : roomTypeCount.keySet()) {
            System.out.println(type + ": " + roomTypeCount.get(type));
        }
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Booking History
        BookingHistory history = new BookingHistory();

        // Step 2: Simulate confirmed bookings
        Reservation r1 = new Reservation("RES101", "Alice", "Single", 2, 2000);
        Reservation r2 = new Reservation("RES102", "Bob", "Double", 3, 3000);
        Reservation r3 = new Reservation("RES103", "Charlie", "Suite", 1, 5000);
        Reservation r4 = new Reservation("RES104", "Diana", "Single", 4, 2000);

        // Step 3: Store bookings (in order)
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);
        history.addReservation(r4);

        // Step 4: Admin views all bookings
        BookingReportService.showAllBookings(history);

        // Step 5: Admin generates summary report
        BookingReportService.generateSummary(history);
    }
}