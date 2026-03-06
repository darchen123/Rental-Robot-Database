package model;

/**
 * Represents a local community robot rental facility.
 */

public class Facility {
    private final int facilityId;
    private String address;
    private String city;
    private String phone;
    private String manager;
    private int deliveryVehicleCapacity;
    private int robotCapacity;

    public Facility(int facilityId, String address, String city, String phone, String manager,
                    int deliveryVehicleCapacity, int robotCapacity) {
        this.facilityId = facilityId;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.manager = manager;
        this.deliveryVehicleCapacity = deliveryVehicleCapacity;
        this.robotCapacity = robotCapacity;
    }

    public int getFacilityId() { return facilityId; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public String getManager() { return manager; }
    public int getDeliveryVehicleCapacity() { return deliveryVehicleCapacity; }
    public int getRobotCapacity() { return robotCapacity; }

    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setManager(String manager) { this.manager = manager; }
    public void setDeliveryVehicleCapacity(int deliveryVehicleCapacity) { this.deliveryVehicleCapacity = deliveryVehicleCapacity; }
    public void setRobotCapacity(int robotCapacity) { this.robotCapacity = robotCapacity; }

    @Override
    public String toString() {
        return "Facility ID: " + facilityId + ", " + city + ", " + address +
               ", Manager: " + manager + ", Delivery Vehicle Capacity: " + deliveryVehicleCapacity +
               ", Robot Capacity: " + robotCapacity;
    }
}
