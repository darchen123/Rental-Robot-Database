package model;

/**
 * Internal order request for new assets.
 */

public class InternalOrderRequest {
    private final int orderId;
    private int facilityId;
    private String assetType;
    private int quantity;
    private double value;
    private String estArrivalDate;
    private String arrivalDate;

    public InternalOrderRequest(int orderId, int facilityId, String assetType, int quantity,
                                 double value, String estArrivalDate, String arrivalDate) {
        this.orderId = orderId;
        this.facilityId = facilityId;
        this.assetType = assetType;
        this.quantity = quantity;
        this.value = value;
        this.estArrivalDate = estArrivalDate;
        this.arrivalDate = arrivalDate;
    }

    public int getOrderId() { return orderId; }
    public int getFacilityId() { return facilityId; }
    public String getAssetType() { return assetType; }
    public int getQuantity() { return quantity; }
    public double getValue() { return value; }
    public String getEstArrivalDate() { return estArrivalDate; }
    public String getArrivalDate() { return arrivalDate; }

    public void setAssetType(String assetType) { this.assetType = assetType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setValue(double value) { this.value = value; }
    public void setEstArrivalDate(String estArrivalDate) { this.estArrivalDate = estArrivalDate; }
    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }

    @Override
    public String toString() {
        return "Order ID: " + orderId + ", Type: " + assetType + ", Qty: " + quantity + ", Value: $" + value;
    }
}
