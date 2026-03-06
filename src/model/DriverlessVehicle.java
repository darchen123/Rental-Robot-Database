package model;

/**
 * Represents a driverless vehicle used for robot delivery and pickup.
 */

public class DriverlessVehicle extends AutonomousAsset {

    private double distanceAutonomy;
    private String licencePlate;
    private double maxSpeed;
    private double payloadCapacity;

    public DriverlessVehicle(int assetId, int facilityId, String warrantyExpDate, String status,
                             String manufacturer, String model, int manufacturingYear, String serialNo,
                             String location, Integer orderId, double distanceAutonomy,
                             String licencePlate, double maxSpeed, double payloadCapacity) {
        super(assetId, facilityId, warrantyExpDate, status, manufacturer, model, manufacturingYear, serialNo, location, orderId);
        this.distanceAutonomy = distanceAutonomy;
        this.licencePlate = licencePlate;
        this.maxSpeed = maxSpeed;
        this.payloadCapacity = payloadCapacity;
    }

    public int getVehicleId() { return getAssetId(); }
    public double getDistanceAutonomy() { return distanceAutonomy; }
    public String getLicencePlate() { return licencePlate; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getPayloadCapacity() { return payloadCapacity; }

    public void setDistanceAutonomy(double distanceAutonomy) { this.distanceAutonomy = distanceAutonomy; }
    public void setLicencePlate(String licencePlate) { this.licencePlate = licencePlate; }
    public void setMaxSpeed(double maxSpeed) { this.maxSpeed = maxSpeed; }
    public void setPayloadCapacity(double payloadCapacity) { this.payloadCapacity = payloadCapacity; }

    @Override
    public String toString() {
        return "Vehicle ID: " + getAssetId() + ", Model: " + getModel() +
               ", Licence: " + licencePlate + ", Status: " + getStatus() +
               ", Location: " + getLocation();
    }
}
