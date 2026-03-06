package model;

/**
 * Tracks robot return/pickup events.
 */
public class Returns {
    private final int robotId;
    private final int deliveryVehicleId;
    private String returnDate;
    private String returnTime;

    public Returns(int robotId, int deliveryVehicleId, String returnDate, String returnTime) {
        this.robotId = robotId;
        this.deliveryVehicleId = deliveryVehicleId;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
    }

    public int getRobotId() { return robotId; }
    public int getDeliveryVehicleId() { return deliveryVehicleId;}
    public String getReturnDate() { return returnDate; }
    public String getReturnTime() { return returnTime; }

    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setReturnTime(String returnTime) { this.returnTime = returnTime; }

    @Override
    public String toString() {
        return "Return: Robot " + robotId + " by Vehicle " + deliveryVehicleId + " on " + returnDate + " " + returnTime;
    }
}
