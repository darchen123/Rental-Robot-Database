package model;

/**
 * Represents a payment for a rental.
 */
public class Payment {
    private final int paymentId;
    private int rentalId;
    private int custId;
    private double amount;
    private String paymentMethod;
    private String status;

    public Payment(int paymentId, int rentalId, int custId, double amount, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.rentalId = rentalId;
        this.custId = custId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getPaymentId() { return paymentId; }
    public int getRentalId() { return rentalId; }
    public int getCustId() { return custId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Payment ID: " + paymentId + ", Rental: " + rentalId + ", Amount: $" + amount + ", Status: " + status;
    }
}
