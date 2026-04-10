package model;

/**
 * Represents a driverless vehicle used for robot delivery and pickup.
 */
public class DriverlessVehicle extends AutonomousAsset {

    private int distanceAutonomy;
    private String licensePlate;
    private int maxSpeed;
    private int payloadCapacity;

    public DriverlessVehicle(int assetId, int facilityId, String warrantyExpDate, String status,
                             String manufacturer, String model, int manufacturingYear, String serialNo,
                             String location, Integer orderId, int distanceAutonomy,
                             String licensePlate, int maxSpeed, int payloadCapacity) {
        super(assetId, facilityId, warrantyExpDate, status, manufacturer, model, manufacturingYear, serialNo, location, orderId);
        this.distanceAutonomy = distanceAutonomy;
        this.licensePlate = licensePlate;
        this.maxSpeed = maxSpeed;
        this.payloadCapacity = payloadCapacity;
    }

    public int getVehicleId() { return getAssetId(); }
    public int getDistanceAutonomy() { return distanceAutonomy; }
    public String getLicensePlate() { return licensePlate; }
    public int getMaxSpeed() { return maxSpeed; }
    public int getPayloadCapacity() { return payloadCapacity; }

    public void setDistanceAutonomy(int distanceAutonomy) { this.distanceAutonomy = distanceAutonomy; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public void setMaxSpeed(int maxSpeed) { this.maxSpeed = maxSpeed; }
    public void setPayloadCapacity(int payloadCapacity) { this.payloadCapacity = payloadCapacity; }

    @Override
    public String toString() {
        return "Vehicle ID: " + getAssetId() + ", Model: " + getModel() +
               ", License: " + licensePlate + ", Status: " + getStatus() +
               ", Location: " + getLocation();
    }
}
