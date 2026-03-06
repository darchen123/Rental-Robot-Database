package model;

/**
 * Tracks robot delivery events.
 */

public class Delivers {
    private final int robotId;
    private final int deliveryVehicleId;
    private String dDate;
    private String dTime;

    public Delivers(int robotId, int deliveryVehicleId, String dDate, String dTime) {
        this.robotId = robotId;
        this.deliveryVehicleId = deliveryVehicleId;
        this.dDate = dDate;
        this.dTime = dTime;
    }

    public int getRobotId() { return robotId; }
    public int getDeliveryVehicleId() { return deliveryVehicleId; }
    public String getDDate() { return dDate; }
    public String getDTime() { return dTime; }

    public void setDDate(String dDate) { this.dDate = dDate; }
    public void setDTime(String dTime) { this.dTime = dTime; }

    @Override
    public String toString() {
        return "Delivery: Robot " + robotId + " by Vehicle " + deliveryVehicleId + " on " + dDate + " " + dTime;
    }
}
