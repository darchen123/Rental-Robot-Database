package model;

/**
 * Represents a service robot available for rent.
 */
public class Robot extends AutonomousAsset {

    private int batteryAutonomy;
    private String sensors;
    private String training;
    private String function;
    private Integer rentalId;

    public Robot(int assetId, int facilityId, String warrantyExp, String status,
                 String manufacturer, String model, int manufacturingYear, String serialNo,
                 String location, Integer orderId, int batteryAutonomy,
                 String sensors, String training, String function, Integer rentalId) {
        super(assetId, facilityId, warrantyExp, status, manufacturer, model, manufacturingYear, serialNo, location, orderId);
        this.batteryAutonomy = batteryAutonomy;
        this.sensors = sensors;
        this.training = training;
        this.function = function;
        this.rentalId = rentalId;
    }

    public int getRobotId() { return getAssetId(); }
    public int getBatteryAutonomy() { return batteryAutonomy; }
    public String getSensors() { return sensors; }
    public String getTraining() { return training; }
    public String getFunction() { return function; }
    public Integer getRentalId() { return rentalId; }

    public void setBatteryAutonomy(int batteryAutonomy) { this.batteryAutonomy = batteryAutonomy; }
    public void setSensors(String sensors) { this.sensors = sensors; }
    public void setTraining(String training) { this.training = training; }
    public void setFunction(String function) { this.function = function; }
    public void setRentalId(Integer rentalId) { this.rentalId = rentalId; }

    @Override
    public String toString() {
        return "Robot ID: " + getAssetId() + ", Model: " + getModel() +
               ", Serial: " + getSerialNo() + ", Status: " + getStatus() +
               ", Location: " + getLocation() + ", Function: " + function;
    }
}
