package model;

/**
 * Base class for Robot and DriverlessVehicle.
 */
public abstract class AutonomousAsset {
    private final int assetId;
    private int facilityId;
    private String warrantyExpDate;
    private String status;
    private String manufacturer;
    private String model;
    private int manufacturingYear;
    private String serialNo;
    private String location;
    private Integer orderId;

    public AutonomousAsset(int assetId, int facilityId, String warrantyExpDate, String status,
                            String manufacturer, String model, int manufacturingYear, String serialNo,
                            String location, Integer orderId) {
        this.assetId = assetId;
        this.facilityId = facilityId;
        this.warrantyExpDate = warrantyExpDate;
        this.status = status;
        this.manufacturer = manufacturer;
        this.model = model;
        this.manufacturingYear = manufacturingYear;
        this.serialNo = serialNo;
        this.location = location;
        this.orderId = orderId;
    }

    public int getAssetId() { return assetId; }
    public int getFacilityId() { return facilityId; }
    public String getWarrantyExpDate() { return warrantyExpDate; }
    public String getStatus() { return status; }
    public String getManufacturer() { return manufacturer; }
    public String getModel() { return model; }
    public int getManufacturingYear() { return manufacturingYear; }
    public String getSerialNo() { return serialNo; }
    public String getLocation() { return location; }
    public Integer getOrderId() { return orderId; }

    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }
    public void setWarrantyExpDate(String warrantyExpDate) { this.warrantyExpDate = warrantyExpDate; }
    public void setStatus(String status) { this.status = status; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setModel(String model) { this.model = model; }
    public void setManufacturingYear(int manufacturingYear) { this.manufacturingYear = manufacturingYear; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }
    public void setLocation(String location) { this.location = location; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
}
