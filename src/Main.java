import db.CustomerDao;
import db.Database;
import db.DriverlessVehicleDao;
import db.RentalService;
import db.RobotDao;
import model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Rental Home Robot Database System - Text-based interface for warehouse staff.
 */
public class Main {
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        try {
            Database.initializeIfNeeded();
        } catch (Exception e) {
            System.err.println("Could not initialize database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("===========================================");
        System.out.println("  Rental Home Robot Facility - Staff Menu");
        System.out.println("===========================================\n");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> robotMenu();
                case 2 -> driverlessVehicleMenu();
                case 3 -> customerMenu();
                case 4 -> rentRobot();
                case 5 -> returnEquipment();
                case 6 -> scheduleDelivery();
                case 7 -> schedulePickup();
                case 8 -> usefulReportsMenu();
                case 0 -> {
                    System.out.println("See ya next time!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("--- MAIN MENU ---");
        System.out.println("1. Robot Management (Add/Edit/Delete/Search)");
        System.out.println("2. Driverless Vehicle Management (Add/Edit/Delete/Search)");
        System.out.println("3. Customer Management (Add/Edit/Delete/Search)");
        System.out.println("4. Rent Robot (register rental for a customer)");
        System.out.println("5. Return Equipment (register return for a customer's rental)");
        System.out.println("6. Schedule Robot Delivery (assign vehicle; customer's rental)");
        System.out.println("7. Schedule Robot Pickup (assign vehicle; customer's rental)");
        System.out.println("8. Useful Reports");
        System.out.println("0. Exit");
        System.out.println();
    }

    // ==================== ROBOT CRUD ====================
    private static void robotMenu() {
        while (true) {
            System.out.println("--- ROBOT MANAGEMENT ---");
            System.out.println("1. Add new robot");
            System.out.println("2. Edit robot");
            System.out.println("3. Delete robot");
            System.out.println("4. Search robots");
            System.out.println("0. Back to main menu");
            int choice = readInt("Enter choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> addRobot();
                case 2 -> editRobot();
                case 3 -> deleteRobot();
                case 4 -> searchRobots();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    private static void addRobot() {
        System.out.println("--- ADD NEW ROBOT ---");
        int facId = readInt("Facility ID (Fac_ID — must exist in Facility table, e.g. 1): ");
        String warrantyExp = readLine("Warranty expiration (YYYY-MM-DD or MM-DD-YYYY): ");
        String status = readLine("Status (available | in use | decommissioned): ");
        String manufacturer = readLine("Manufacturer: ");
        String model = readLine("Model: ");
        int year = readInt("Year: ");
        int serialNo = readInt("Serial number (integer): ");
        String location = readLine("Warehouse location (stored per facility): ");
        int batteryAutonomy = readInt("Battery autonomy (hours, integer): ");
        String sensors = readLine("Sensors: ");
        String training = readLine("Training level (optional, Enter to skip): ");
        String function = readLine("Function (e.g., cleaning, childcare, security): ");

        try (Connection conn = Database.getConnection()) {
            int id = RobotDao.insert(conn, facId, warrantyExp, status, manufacturer, model, year, serialNo,
                    location, batteryAutonomy, sensors, training, function);
            System.out.println("Robot added successfully. Asset ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editRobot() {
        int id = readInt("Enter Robot ID (Asset ID) to edit: ");
        try (Connection conn = Database.getConnection()) {
            Robot robot = RobotDao.findById(conn, id);
            if (robot == null) {
                System.out.println("Robot not found.");
                return;
            }
            System.out.println("Current: " + robot);
            System.out.println("Enter new values (press Enter to keep current):");

            String model = readLineOrKeep("Model [" + robot.getModel() + "]: ", robot.getModel());
            String status = readLineOrKeep("Status [" + robot.getStatus() + "]: ", robot.getStatus());
            String location = readLineOrKeep("Location [" + robot.getLocation() + "]: ", robot.getLocation());
            String training = readLineOrKeep("Training [" + robot.getTraining() + "]: ", robot.getTraining());

            RobotDao.update(conn, id, robot.getManufacturer(), model, status, location, training);
            System.out.println("Robot updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteRobot() {
        int id = readInt("Enter Robot ID to delete: ");
        try (Connection conn = Database.getConnection()) {
            if (RobotDao.delete(conn, id)) {
                System.out.println("Robot deleted successfully.");
            } else {
                System.out.println("Robot not found or could not delete (check active rentals).");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchRobots() {
        System.out.println("--- SEARCH ROBOTS ---");
        System.out.println("Search by: 1) ID  2) Model  3) Status  4) Function");
        int choice = readInt("Choice: ");
        List<Robot> results = List.of();
        try (Connection conn = Database.getConnection()) {
            switch (choice) {
                case 1 -> {
                    int id = readInt("Robot ID: ");
                    Robot r = RobotDao.findById(conn, id);
                    results = r != null ? List.of(r) : List.of();
                }
                case 2 -> {
                    String model = readLine("Model (partial match): ");
                    results = RobotDao.searchByModelLike(conn, model);
                }
                case 3 -> {
                    String status = readLine("Status: ");
                    results = RobotDao.searchByStatus(conn, status);
                }
                case 4 -> {
                    String func = readLine("Function: ");
                    results = RobotDao.searchByFunctionLike(conn, func);
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
            System.out.println("Found " + results.size() + " robot(s):");
            results.forEach(r -> System.out.println("  " + r));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== DRIVERLESS VEHICLE CRUD ====================
    private static void driverlessVehicleMenu() {
        while (true) {
            System.out.println("--- DRIVERLESS VEHICLE MANAGEMENT ---");
            System.out.println("1. Add new vehicle");
            System.out.println("2. Edit vehicle");
            System.out.println("3. Delete vehicle");
            System.out.println("4. Search vehicles");
            System.out.println("0. Back to main menu");
            int choice = readInt("Enter choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> addVehicle();
                case 2 -> editVehicle();
                case 3 -> deleteVehicle();
                case 4 -> searchVehicles();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    private static void addVehicle() {
        System.out.println("--- ADD NEW DRIVERLESS VEHICLE ---");
        int facId = readInt("Facility ID (Fac_ID — must exist in Facility table, e.g. 1): ");
        String warrantyExp = readLine("Warranty expiration (YYYY-MM-DD or MM-DD-YYYY): ");
        String status = readLine("Status (available | in use | decommissioned): ");
        String manufacturer = readLine("Manufacturer: ");
        String model = readLine("Model: ");
        int year = readInt("Year: ");
        int serialNo = readInt("Serial number (integer): ");
        String location = readLine("Location (stored per facility): ");
        int distanceAutonomy = readInt("Distance autonomy (miles, integer): ");
        String licensePlate = readLine("License plate (7 chars): ");
        int maxSpeed = readInt("Max speed (mph, integer): ");
        int payloadCapacity = readInt("Payload capacity (lbs, integer): ");

        try (Connection conn = Database.getConnection()) {
            int id = DriverlessVehicleDao.insert(conn, facId, warrantyExp, status, manufacturer, model, year,
                    serialNo, location, distanceAutonomy, licensePlate, maxSpeed, payloadCapacity);
            System.out.println("Vehicle added successfully. Asset ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editVehicle() {
        int id = readInt("Enter Vehicle ID to edit: ");
        try (Connection conn = Database.getConnection()) {
            DriverlessVehicle v = DriverlessVehicleDao.findById(conn, id);
            if (v == null) {
                System.out.println("Vehicle not found.");
                return;
            }
            System.out.println("Current: " + v);
            String model = readLineOrKeep("Model [" + v.getModel() + "]: ", v.getModel());
            String status = readLineOrKeep("Status [" + v.getStatus() + "]: ", v.getStatus());
            String location = readLineOrKeep("Location [" + v.getLocation() + "]: ", v.getLocation());
            DriverlessVehicleDao.update(conn, id, v.getManufacturer(), model, status, location);
            System.out.println("Vehicle updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteVehicle() {
        int id = readInt("Enter Vehicle ID to delete: ");
        try (Connection conn = Database.getConnection()) {
            if (DriverlessVehicleDao.delete(conn, id)) {
                System.out.println("Vehicle deleted successfully.");
            } else {
                System.out.println("Vehicle not found or could not delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchVehicles() {
        System.out.println("--- SEARCH VEHICLES ---");
        System.out.println("Search by: 1) ID  2) License plate  3) Status");
        int choice = readInt("Choice: ");
        List<DriverlessVehicle> results = List.of();
        try (Connection conn = Database.getConnection()) {
            switch (choice) {
                case 1 -> {
                    int id = readInt("Vehicle ID: ");
                    DriverlessVehicle v = DriverlessVehicleDao.findById(conn, id);
                    results = v != null ? List.of(v) : List.of();
                }
                case 2 -> {
                    String plate = readLine("License plate: ");
                    results = DriverlessVehicleDao.searchByPlateLike(conn, plate);
                }
                case 3 -> {
                    String status = readLine("Status: ");
                    results = DriverlessVehicleDao.searchByStatus(conn, status);
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
            System.out.println("Found " + results.size() + " vehicle(s):");
            results.forEach(v -> System.out.println("  " + v));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== CUSTOMER CRUD ====================
    private static void customerMenu() {
        while (true) {
            System.out.println("--- CUSTOMER MANAGEMENT ---");
            System.out.println("1. Add new customer");
            System.out.println("2. Edit customer");
            System.out.println("3. Delete customer");
            System.out.println("4. Search customers");
            System.out.println("0. Back to main menu");
            int choice = readInt("Enter choice: ");
            System.out.println();

            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> editCustomer();
                case 3 -> deleteCustomer();
                case 4 -> searchCustomers();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
            System.out.println();
        }
    }

    private static void addCustomer() {
        System.out.println("--- ADD NEW CUSTOMER ---");
        int facId = readInt("Facility ID (Fac_ID): ");
        String fName = readLine("First name (FName): ");
        String lName = readLine("Last name (LName): ");
        String address = readLine("Address: ");
        String phone = readLine("Phone: ");
        String email = readLine("Email: ");
        String startDate = readLine("Start date (MM-DD-YYYY): ");
        int facDist = readInt("Facility distance (Fac_Dist, miles, integer): ");
        String status = readLine("Status (active/deactivated): ");

        try (Connection conn = Database.getConnection()) {
            int id = CustomerDao.insert(conn, facId, fName, lName, address, phone, email, startDate, facDist, status);
            System.out.println("Customer added successfully. Cust_ID: " + id);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void editCustomer() {
        int id = readInt("Enter Customer ID (Cust_ID) to edit: ");
        try (Connection conn = Database.getConnection()) {
            Customer c = CustomerDao.findById(conn, id);
            if (c == null) {
                System.out.println("Customer not found.");
                return;
            }
            System.out.println("Current: " + c);
            String status = readLineOrKeep("Status [" + c.getStatus() + "]: ", c.getStatus());
            String address = readLineOrKeep("Address [" + c.getAddress() + "]: ", c.getAddress());
            String phone = readLineOrKeep("Phone [" + c.getPhone() + "]: ", c.getPhone());
            if (CustomerDao.update(conn, id, status, address, phone)) {
                System.out.println("Customer updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteCustomer() {
        int id = readInt("Enter Customer ID to delete: ");
        try (Connection conn = Database.getConnection()) {
            if (CustomerDao.delete(conn, id)) {
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("Customer not found or could not delete (may have rentals).");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchCustomers() {
        System.out.println("--- SEARCH CUSTOMERS ---");
        System.out.println("Search by: 1) ID  2) Name  3) Email  4) Status");
        int choice = readInt("Choice: ");
        List<Customer> results = List.of();
        try (Connection conn = Database.getConnection()) {
            switch (choice) {
                case 1 -> {
                    int id = readInt("Customer ID: ");
                    Customer c = CustomerDao.findById(conn, id);
                    results = c != null ? List.of(c) : List.of();
                }
                case 2 -> {
                    String name = readLine("Name (partial match): ");
                    results = CustomerDao.searchByNameLike(conn, name);
                }
                case 3 -> {
                    String email = readLine("Email: ");
                    results = CustomerDao.searchByEmailLike(conn, email);
                }
                case 4 -> {
                    String status = readLine("Status: ");
                    results = CustomerDao.searchByStatus(conn, status);
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
            System.out.println("Found " + results.size() + " customer(s):");
            results.forEach(c -> System.out.println("  " + c));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== RENT / RETURN / DELIVERY / PICKUP ====================
    private static void rentRobot() {
        System.out.println("--- RENT ROBOT ---");
        int custId = readInt("Customer ID (C_ID): ");
        int robotId = readInt("Robot ID (Robot_ID): ");
        String startDate = readLine("Rental start date (YYYY-MM-DD or MM-DD-YYYY): ");
        String dueDate = readLine("Due date (YYYY-MM-DD or MM-DD-YYYY): ");
        int rentalFee = readInt("Rental fee ($, integer): ");

        try (Connection conn = Database.getConnection()) {
            int rentalId = RentalService.rentRobot(conn, custId, robotId, startDate, dueDate, rentalFee);
            Customer c = CustomerDao.findById(conn, custId);
            System.out.println("Rental registered successfully. Rental_ID: " + rentalId);
            if (c != null) {
                System.out.println("Robot rented to " + c.getFName() + " " + c.getLName() + ".");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnEquipment() {
        System.out.println("--- RETURN EQUIPMENT ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int custId = readInt("Customer ID (Cust_ID -- who rented the equipment): ");
        if (custId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int rentalId = readInt("Rental ID (Rental_ID for this customer's rental): ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        String returnDate = readLine("Return date (YYYY-MM-DD or MM-DD-YYYY): ");

        try (Connection conn = Database.getConnection()) {
            RentalService.returnEquipment(conn, rentalId, custId, returnDate);
            System.out.println("Equipment returned successfully.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void scheduleDelivery() {
        System.out.println("--- SCHEDULE ROBOT DELIVERY ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int custId = readInt("Customer ID (Cust_ID -- who the rental is for): ");
        if (custId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int rentalId = readInt("Rental ID (Rental_ID for this customer): ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int dvId = readInt("Driverless vehicle ID (AA_ID / DV_ID) to assign: ");
        String dDate = readLine("Delivery date (DDate, MM-DD-YYYY): ");
        String dTime = readLine("Delivery time (DTime, e.g., 14:00): ");

        try (Connection conn = Database.getConnection()) {
            RentalService.scheduleDelivery(conn, rentalId, custId, dvId, dDate, dTime);
            Robot r = null;
            Customer c = null;
            try (Connection c2 = Database.getConnection()) {
                try (var ps = c2.prepareStatement("SELECT Robot_ID FROM Rents WHERE Rental_ID = ?")) {
                    ps.setInt(1, rentalId);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            r = RobotDao.findById(c2, rs.getInt("Robot_ID"));
                        }
                    }
                }
                try (var ps = c2.prepareStatement("SELECT C_ID FROM Rental WHERE Rental_ID = ?")) {
                    ps.setInt(1, rentalId);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            c = CustomerDao.findById(c2, rs.getInt("C_ID"));
                        }
                    }
                }
                DriverlessVehicle v = DriverlessVehicleDao.findById(c2, dvId);
                System.out.println("Robot delivered.");
                System.out.println("  Rental ID: " + rentalId + ", Robot: " + (r != null ? r.getModel() : "N/A") +
                        ", Customer: " + (c != null ? c.getFName() + " " + c.getLName() : "N/A") +
                        ", Vehicle: " + (v != null ? v.getLicensePlate() : "N/A") + ", Scheduled: " + dDate + " " + dTime);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void schedulePickup() {
        System.out.println("--- SCHEDULE ROBOT PICKUP ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int custId = readInt("Customer ID (Cust_ID -- who the rental is for): ");
        if (custId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int rentalId = readInt("Rental ID (Rental_ID for this customer): ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int dvId = readInt("Driverless vehicle ID (AA_ID / DV_ID) to assign: ");
        String rDate = readLine("Pickup date (RDate, MM-DD-YYYY): ");
        String rTime = readLine("Pickup time (RTime, e.g., 10:00): ");

        try (Connection conn = Database.getConnection()) {
            RentalService.schedulePickup(conn, rentalId, custId, dvId, rDate, rTime);
            Robot r = null;
            Customer c = null;
            try (Connection c2 = Database.getConnection()) {
                try (var ps = c2.prepareStatement("SELECT Robot_ID FROM Rents WHERE Rental_ID = ?")) {
                    ps.setInt(1, rentalId);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            r = RobotDao.findById(c2, rs.getInt("Robot_ID"));
                        }
                    }
                }
                try (var ps = c2.prepareStatement("SELECT C_ID FROM Rental WHERE Rental_ID = ?")) {
                    ps.setInt(1, rentalId);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            c = CustomerDao.findById(c2, rs.getInt("C_ID"));
                        }
                    }
                }
                DriverlessVehicle v = DriverlessVehicleDao.findById(c2, dvId);
                System.out.println("Robot pickup scheduled.");
                System.out.println("  Rental ID: " + rentalId + ", Robot: " + (r != null ? r.getModel() : "N/A") +
                        ", Customer: " + (c != null ? c.getFName() + " " + c.getLName() : "N/A") +
                        ", Vehicle: " + (v != null ? v.getLicensePlate() : "N/A") + ", Scheduled: " + rDate + " " + rTime);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void usefulReportsMenu() {
        System.out.println("--- USEFUL REPORTS (implement in next milestone) ---");
        System.out.println("1. Robots in field vs total inventory");
        System.out.println("2. Customers with overdue rentals");
        System.out.println("3. Maintenance due by asset");
        System.out.println("4. Revenue by facility");
        System.out.println("5. Low-rated robots with comments");
        System.out.println("0. Back");
        int choice = readInt("Choice: ");
        if (choice >= 1 && choice <= 5) {
            System.out.println("Reports will query the database in the reports checkpoint.");
        }
    }

    // ==================== HELPERS ====================
    private static String readLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? "" : line;
    }

    private static String readLineOrKeep(String prompt, String current) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? current : line;
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
