package service;

import java.util.ArrayList;
import java.util.List;
import model.*;

/**
 * Holds all data in memory using ArrayLists.
 */
public class DataStore {
    private final List<Facility> facilities = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Robot> robots = new ArrayList<>();
    private final List<DriverlessVehicle> vehicles = new ArrayList<>();
    private final List<Rental> rentals = new ArrayList<>();
    private final List<Staff> staff = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final List<MaintenanceLog> maintenanceLogs = new ArrayList<>();
    private final List<InternalOrderRequest> orderRequests = new ArrayList<>();
    private final List<Delivers> delivers = new ArrayList<>();
    private final List<Returns> returns = new ArrayList<>();

    private int nextFacId = 1;
    private int nextCustId = 1;
    private int nextAssetId = 1;
    private int nextRentalId = 1;
    private int nextPaymentId = 1;
    private int nextReviewId = 1;
    private int nextMlId = 1;
    private int nextOrderId = 1;

    public DataStore() {
        loadSampleData();
    }

    private void loadSampleData() {
        facilities.add(new Facility(1, "123 Main St", "Columbus", "614-555-0100", "John Smith", 20, 50));

        customers.add(new Customer(1, 1, "Alice", "Johnson", "100 Customer Lane", "614-555-1001",
                "alice@email.com", "01-15-2024", 5.2, "active"));

        robots.add(new Robot(1, 1, "12-31-2026", "rented", "RoboTech Inc", "HB-2000", 2023,
                "SN-R001", "In Transit", null, 24.0, "LIDAR, Camera", "trained", "cleaning", 1));

        vehicles.add(new DriverlessVehicle(2, 1, "08-20-2026", "in use", "AutoMotive", "AutoDeliver-1", 2023,
                "SN-V001", "En Route", null, 300.0, "OH-ABC1234", 45.0, 150.0));

        rentals.add(new Rental(1, 1, 1, "03-01-2025", "", 150.00, "03-15-2025"));

        delivers.add(new Delivers(1, 2, "03-01-2025", "14:00"));

        nextFacId = 2;
        nextCustId = 2;
        nextAssetId = 3;
        nextRentalId = 2;
        nextPaymentId = 1;
        nextReviewId = 1;
        nextMlId = 1;
        nextOrderId = 1;
    }

    public List<Facility> getFacilities() { return facilities; }
    public List<Customer> getCustomers() { return customers; }
    public List<Robot> getRobots() { return robots; }
    public List<DriverlessVehicle> getVehicles() { return vehicles; }
    public List<Rental> getRentals() { return rentals; }
    public List<Staff> getStaff() { return staff; }
    public List<Payment> getPayments() { return payments; }
    public List<Review> getReviews() { return reviews; }
    public List<MaintenanceLog> getMaintenanceLogs() { return maintenanceLogs; }
    public List<InternalOrderRequest> getOrderRequests() { return orderRequests; }
    public List<Delivers> getDelivers() { return delivers; }
    public List<Returns> getReturns() { return returns; }

    public int getNextFacId() { return nextFacId++; }
    public int getNextCustId() { return nextCustId++; }
    public int getNextAssetId() { return nextAssetId++; }
    public int getNextRentalId() { return nextRentalId++; }
    public int getNextPaymentId() { return nextPaymentId++; }
    public int getNextReviewId() { return nextReviewId++; }
    public int getNextMlId() { return nextMlId++; }
    public int getNextOrderId() { return nextOrderId++; }

    public int getNextRobotId() { return getNextAssetId(); }
    public int getNextVehicleId() { return getNextAssetId(); }

    public Robot findRobotById(int id) {
        return robots.stream().filter(r -> r.getRobotId() == id).findFirst().orElse(null);
    }

    public DriverlessVehicle findVehicleById(int id) {
        return vehicles.stream().filter(v -> v.getVehicleId() == id).findFirst().orElse(null);
    }

    public Customer findCustomerById(int id) {
        return customers.stream().filter(c -> c.getCustId() == id).findFirst().orElse(null);
    }

    public Facility findFacilityById(int id) {
        return facilities.stream().filter(f -> f.getFacilityId() == id).findFirst().orElse(null);
    }
}
