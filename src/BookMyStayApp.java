import java.util.*;

// Reservation: Represents booking intent
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

    public void display() {
        System.out.println("Guest: " + guestName +
                ", Room Type: " + roomType +
                ", Nights: " + nights);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added for " + reservation.getGuestName());
    }

    // View all requests (without removing)
    public void viewRequests() {
        System.out.println("\nCurrent Booking Requests (FIFO Order):\n");

        if (queue.isEmpty()) {
            System.out.println("No booking requests in queue.");
            return;
        }

        for (Reservation r : queue) {
            r.display();
        }
    }

    // Get next request (for future processing, not used for allocation yet)
    public Reservation peekNextRequest() {
        return queue.peek();
    }
}

// Main Class
public class BookMyStayApp {

    public static void main(String[] args) {

        // Step 1: Create Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Step 2: Simulate Guest Booking Requests
        Reservation r1 = new Reservation("Alice", "Single", 2);
        Reservation r2 = new Reservation("Bob", "Suite", 3);
        Reservation r3 = new Reservation("Charlie", "Double", 1);
        Reservation r4 = new Reservation("Diana", "Single", 4);

        // Step 3: Add requests to queue (FIFO order preserved)
        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);
        bookingQueue.addRequest(r4);

        // Step 4: View queued requests
        bookingQueue.viewRequests();

        // Step 5: Peek next request (no removal, no allocation)
        Reservation next = bookingQueue.peekNextRequest();

        System.out.println("\nNext request to be processed:");
        if (next != null) {
            next.display();
        }
    }
}