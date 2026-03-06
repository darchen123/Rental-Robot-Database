import model.*;
import service.DataStore;

import java.util.List;
import java.util.Scanner;

/**
 * Rental Home Robot Database System - Text-based interface for warehouse staff.
 */
public class Main {
    private static DataStore dataStore;
    private static Scanner scanner;

    public static void main(String[] args) {
        dataStore = new DataStore();
        scanner = new Scanner(System.in);

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
        System.out.println("4. Rent Robot");
        System.out.println("5. Return Equipment");
        System.out.println("6. Schedule Robot Delivery");
        System.out.println("7. Schedule Robot Pickup");
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
        int facId = readInt("Facility ID (Fac_ID): ");
        String warrantyExp = readLine("Warranty expiration (MM-DD-YYYY): ");
        String status = readLine("Status (e.g., available, rented, maintenance): ");
        String manufacturer = readLine("Manufacturer: ");
        String model = readLine("Model: ");
        int year = readInt("Year: ");
        String serialNo = readLine("Serial number: ");
        String location = readLine("Warehouse location: ");
        double batteryAutonomy = readDouble("Battery autonomy (hours): ");
        String sensors = readLine("Sensors: ");
        String training = readLine("Training level: ");
        String function = readLine("Function (e.g., cleaning, childcare, security): ");

        int id = dataStore.getNextRobotId();
        Robot robot = new Robot(id, facId, warrantyExp, status, manufacturer, model, year,
                serialNo, location, null, batteryAutonomy, sensors, training, function, null);
        dataStore.getRobots().add(robot);
        System.out.println("Robot added successfully. Asset ID: " + id);
    }

    private static void editRobot() {
        int id = readInt("Enter Robot ID (Asset ID) to edit: ");
        Robot robot = dataStore.findRobotById(id);
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

        robot.setModel(model);
        robot.setStatus(status);
        robot.setLocation(location);
        robot.setTraining(training);
        System.out.println("Robot updated successfully.");
    }

    private static void deleteRobot() {
        int id = readInt("Enter Robot ID to delete: ");
        Robot robot = dataStore.findRobotById(id);
        if (robot == null) {
            System.out.println("Robot not found.");
            return;
        }
        dataStore.getRobots().remove(robot);
        System.out.println("Robot deleted successfully.");
    }

    private static void searchRobots() {
        System.out.println("--- SEARCH ROBOTS ---");
        System.out.println("Search by: 1) ID  2) Model  3) Status  4) Function");
        int choice = readInt("Choice: ");
        List<Robot> results = null;
        switch (choice) {
            case 1 -> {
                int id = readInt("Robot ID: ");
                Robot r = dataStore.findRobotById(id);
                results = r != null ? List.of(r) : List.of();
            }
            case 2 -> {
                String model = readLine("Model (partial match): ");
                results = dataStore.getRobots().stream()
                        .filter(r -> r.getModel() != null && r.getModel().toLowerCase().contains(model.toLowerCase()))
                        .toList();
            }
            case 3 -> {
                String status = readLine("Status: ");
                results = dataStore.getRobots().stream()
                        .filter(r -> r.getStatus().equalsIgnoreCase(status))
                        .toList();
            }
            case 4 -> {
                String func = readLine("Function: ");
                results = dataStore.getRobots().stream()
                        .filter(r -> r.getFunction() != null && r.getFunction().toLowerCase().contains(func.toLowerCase()))
                        .toList();
            }
            default -> { System.out.println("Invalid choice."); return; }
        }
        System.out.println("Found " + results.size() + " robot(s):");
        results.forEach(r -> System.out.println("  " + r));
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
        int facId = readInt("Facility ID (Fac_ID): ");
        String warrantyExp = readLine("Warranty expiration (MM-DD-YYYY): ");
        String status = readLine("Status (e.g., available, in use): ");
        String manufacturer = readLine("Manufacturer: ");
        String model = readLine("Model: ");
        int year = readInt("Year: ");
        String serialNo = readLine("Serial number: ");
        String location = readLine("Location: ");
        double distanceAutonomy = readDouble("Distance autonomy (miles): ");
        String licencePlate = readLine("Licence plate: ");
        double maxSpeed = readDouble("Max speed (mph): ");
        double payloadCapacity = readDouble("Payload capacity (lbs): ");

        int id = dataStore.getNextVehicleId();
        DriverlessVehicle v = new DriverlessVehicle(id, facId, warrantyExp, status, manufacturer, model, year,
                serialNo, location, null, distanceAutonomy, licencePlate, maxSpeed, payloadCapacity);
        dataStore.getVehicles().add(v);
        System.out.println("Vehicle added successfully. Asset ID: " + id);
    }

    private static void editVehicle() {
        int id = readInt("Enter Vehicle ID to edit: ");
        DriverlessVehicle v = dataStore.findVehicleById(id);
        if (v == null) {
            System.out.println("Vehicle not found.");
            return;
        }
        System.out.println("Current: " + v);
        String status = readLineOrKeep("Status [" + v.getStatus() + "]: ", v.getStatus());
        String location = readLineOrKeep("Location [" + v.getLocation() + "]: ", v.getLocation());
        v.setStatus(status);
        v.setLocation(location);
        System.out.println("Vehicle updated successfully.");
    }

    private static void deleteVehicle() {
        int id = readInt("Enter Vehicle ID to delete: ");
        DriverlessVehicle v = dataStore.findVehicleById(id);
        if (v == null) {
            System.out.println("Vehicle not found.");
            return;
        }
        dataStore.getVehicles().remove(v);
        System.out.println("Vehicle deleted successfully.");
    }

    private static void searchVehicles() {
        System.out.println("--- SEARCH VEHICLES ---");
        System.out.println("Search by: 1) ID  2) Licence plate  3) Status");
        int choice = readInt("Choice: ");
        List<DriverlessVehicle> results = null;
        switch (choice) {
            case 1 -> {
                int id = readInt("Vehicle ID: ");
                DriverlessVehicle v = dataStore.findVehicleById(id);
                results = v != null ? List.of(v) : List.of();
            }
            case 2 -> {
                String plate = readLine("Licence plate: ");
                results = dataStore.getVehicles().stream()
                        .filter(v -> v.getLicencePlate() != null && v.getLicencePlate().toLowerCase().contains(plate.toLowerCase()))
                        .toList();
            }
            case 3 -> {
                String status = readLine("Status: ");
                results = dataStore.getVehicles().stream()
                        .filter(v -> v.getStatus().equalsIgnoreCase(status))
                        .toList();
            }
            default -> { System.out.println("Invalid choice."); return; }
        }
        System.out.println("Found " + results.size() + " vehicle(s):");
        results.forEach(v -> System.out.println("  " + v));
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
        double facDist = readDouble("Facility distance (Fac_Dist, miles): ");
        String status = readLine("Status (active/deactivated): ");

        int id = dataStore.getNextCustId();
        Customer c = new Customer(id, facId, fName, lName, address, phone, email, startDate, facDist, status);
        dataStore.getCustomers().add(c);
        System.out.println("Customer added successfully. Cust_ID: " + id);
    }

    private static void editCustomer() {
        int id = readInt("Enter Customer ID (Cust_ID) to edit: ");
        Customer c = dataStore.findCustomerById(id);
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.println("Current: " + c);
        String status = readLineOrKeep("Status [" + c.getStatus() + "]: ", c.getStatus());
        String address = readLineOrKeep("Address [" + c.getAddress() + "]: ", c.getAddress());
        String phone = readLineOrKeep("Phone [" + c.getPhone() + "]: ", c.getPhone());
        c.setStatus(status);
        c.setAddress(address);
        c.setPhone(phone);
        System.out.println("Customer updated successfully.");
    }

    private static void deleteCustomer() {
        int id = readInt("Enter Customer ID to delete: ");
        Customer c = dataStore.findCustomerById(id);
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        dataStore.getCustomers().remove(c);
        System.out.println("Customer deleted successfully.");
    }

    private static void searchCustomers() {
        System.out.println("--- SEARCH CUSTOMERS ---");
        System.out.println("Search by: 1) ID  2) Name  3) Email  4) Status");
        int choice = readInt("Choice: ");
        List<Customer> results = null;
        switch (choice) {
            case 1 -> {
                int id = readInt("Customer ID: ");
                Customer c = dataStore.findCustomerById(id);
                results = c != null ? List.of(c) : List.of();
            }
            case 2 -> {
                String name = readLine("Name (partial match): ");
                results = dataStore.getCustomers().stream()
                        .filter(c -> ((c.getFName() != null ? c.getFName() : "") + " " + (c.getLName() != null ? c.getLName() : "")).toLowerCase().contains(name.toLowerCase()))
                        .toList();
            }
            case 3 -> {
                String email = readLine("Email: ");
                results = dataStore.getCustomers().stream()
                        .filter(c -> c.getEmail() != null && c.getEmail().toLowerCase().contains(email.toLowerCase()))
                        .toList();
            }
            case 4 -> {
                String status = readLine("Status: ");
                results = dataStore.getCustomers().stream()
                        .filter(c -> c.getStatus().equalsIgnoreCase(status))
                        .toList();
            }
            default -> { System.out.println("Invalid choice."); return; }
        }
        System.out.println("Found " + results.size() + " customer(s):");
        results.forEach(c -> System.out.println("  " + c));
    }

    // ==================== RENT / RETURN / DELIVERY / PICKUP ====================
    private static void rentRobot() {
        System.out.println("--- RENT ROBOT ---");
        int custId = readInt("Customer ID (C_ID): ");
        int robotId = readInt("Robot ID (Robot_ID): ");
        String startDate = readLine("Rental start date (MM-DD-YYYY): ");
        String dueDate = readLine("Due date (MM-DD-YYYY): ");
        double rentalFee = readDouble("Rental fee ($): ");

        Customer c = dataStore.findCustomerById(custId);
        Robot r = dataStore.findRobotById(robotId);

        if (c == null) System.out.println("Customer not found.");
        else if (r == null) System.out.println("Robot not found.");
        else {
            int rentalId = dataStore.getNextRentalId();
            Rental rental = new Rental(rentalId, robotId, custId, startDate, "", rentalFee, dueDate);
            dataStore.getRentals().add(rental);
            r.setStatus("rented");
            r.setRentalId(rentalId);
            System.out.println("Rental registered successfully. Rental_ID: " + rentalId);
            System.out.println("Robot rented to " + c.getFName() + " " + c.getLName() + ".");
        }
    }

    private static void returnEquipment() {
        System.out.println("--- RETURN EQUIPMENT ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int rentalId = readInt("Rental ID: ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        String returnDate = readLine("Return date (MM-DD-YYYY): ");

        Rental rental = dataStore.getRentals().stream()
                .filter(r -> r.getRentalId() == rentalId)
                .findFirst()
                .orElse(null);

        if (rental == null) {
            System.out.println("Rental not found.");
            return;
        }
        rental.setEndDate(returnDate);
        Robot r = dataStore.findRobotById(rental.getRobotId());
        if (r != null) {
            r.setStatus("available");
            r.setRentalId(null);
        }
        System.out.println("Equipment returned successfully.");
    }

    private static void scheduleDelivery() {
        System.out.println("--- SCHEDULE ROBOT DELIVERY ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int rentalId = readInt("Rental ID: ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int dvId = readInt("Vehicle ID (DV_ID) to assign: ");
        String dDate = readLine("Delivery date (DDate, MM-DD-YYYY): ");
        String dTime = readLine("Delivery time (DTime, e.g., 14:00): ");

        Rental rental = dataStore.getRentals().stream()
                .filter(r -> r.getRentalId() == rentalId)
                .findFirst()
                .orElse(null);
        DriverlessVehicle v = dataStore.findVehicleById(dvId);
        if (rental != null && v != null) {
            Robot r = dataStore.findRobotById(rental.getRobotId());
            Customer c = dataStore.findCustomerById(rental.getCustId());
            dataStore.getDelivers().add(new Delivers(rental.getRobotId(), dvId, dDate, dTime));
            v.setStatus("in use");
            System.out.println("Robot delivered.");
            System.out.println("  Rental ID: " + rentalId + ", Robot: " + (r != null ? r.getModel() : "N/A") +
                    ", Customer: " + (c != null ? c.getFName() + " " + c.getLName() : "N/A") +
                    ", Vehicle: " + v.getLicencePlate() + ", Scheduled: " + dDate + " " + dTime);
        } else {
            System.out.println("Rental or vehicle not found.");
        }
    }

    private static void schedulePickup() {
        System.out.println("--- SCHEDULE ROBOT PICKUP ---");
        System.out.println("Enter 0 to cancel and return to main menu.");
        int rentalId = readInt("Rental ID: ");
        if (rentalId == 0) {
            System.out.println("Cancelled.");
            return;
        }
        int dvId = readInt("Vehicle ID (DV_ID) to assign: ");
        String rDate = readLine("Pickup date (RDate, MM-DD-YYYY): ");
        String rTime = readLine("Pickup time (RTime, e.g., 10:00): ");

        Rental rental = dataStore.getRentals().stream()
                .filter(r -> r.getRentalId() == rentalId)
                .findFirst()
                .orElse(null);
        DriverlessVehicle v = dataStore.findVehicleById(dvId);
        if (rental != null && v != null) {
            Robot r = dataStore.findRobotById(rental.getRobotId());
            Customer c = dataStore.findCustomerById(rental.getCustId());
            dataStore.getReturns().add(new Returns(rental.getRobotId(), dvId, rDate, rTime));
            System.out.println("Robot pickup scheduled.");
            System.out.println("  Rental ID: " + rentalId + ", Robot: " + (r != null ? r.getModel() : "N/A") +
                    ", Customer: " + (c != null ? c.getFName() + " " + c.getLName() : "N/A") +
                    ", Vehicle: " + v.getLicencePlate() + ", Scheduled: " + rDate + " " + rTime);
        } else {
            System.out.println("Rental or vehicle not found.");
        }
    }

    // ==================== USEFUL REPORTS (Menu only) ====================
    private static void usefulReportsMenu() {
        System.out.println("--- USEFUL REPORTS (Not implemented yet) ---");
        System.out.println("1. Robots in field vs total inventory");
        System.out.println("2. Customers with overdue rentals");
        System.out.println("3. Maintenance due by asset");
        System.out.println("4. Revenue by facility");
        System.out.println("5. Low-rated robots with comments");
        System.out.println("0. Back");
        int choice = readInt("Choice: ");
        if (choice >= 1 && choice <= 5) {
            System.out.println("Report functionality will be implemented in a future checkpoint.");
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
