package model;

/**
 * Represents a rental transaction between a customer and a robot.
 */
public class Rental {
    private final int rentalId;
    private int robotId;
    private int custId;
    private String startDate;
    private String endDate;
    private double rentalFee;
    private String dueDate;

    public Rental(int rentalId, int robotId, int custId, String startDate,
                  String endDate, double rentalFee, String dueDate) {
        this.rentalId = rentalId;
        this.robotId = robotId;
        this.custId = custId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalFee = rentalFee;
        this.dueDate = dueDate;
    }

    public int getRentalId() { return rentalId; }
    public int getRobotId() { return robotId; }
    public int getCustId() { return custId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getRentalFee() { return rentalFee; }
    public String getDueDate() { return dueDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setRentalFee(double rentalFee) { this.rentalFee = rentalFee; }

    @Override
    public String toString() {
        return "Rental ID: " + rentalId + ", Customer: " + custId + ", Robot: " + robotId +
               ", Start: " + startDate + ", Due: " + dueDate + ", Fee: $" + rentalFee;
    }
}
