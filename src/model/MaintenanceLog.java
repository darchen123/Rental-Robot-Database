package model;

/**
 * Tracks maintenance on autonomous assets.
 */
public class MaintenanceLog {
    private final int maintenanceLogId;
    private String staffSSN;
    private int assetId;
    private String type;
    private String dateIn;
    private String dateOut;
    private String description;

    public MaintenanceLog(int maintenanceLogId, String staffSSN, int assetId, String type,
                           String dateIn, String dateOut, String description) {
        this.maintenanceLogId = maintenanceLogId;
        this.staffSSN = staffSSN;
        this.assetId = assetId;
        this.type = type;
        this.dateIn = dateIn;
        this.dateOut = dateOut;
        this.description = description;
    }

    public int getMaintenanceLogId() { return maintenanceLogId; }
    public String getStaffSSN() { return staffSSN; }
    public int getAssetId() { return assetId; }
    public String getType() { return type; }
    public String getDateIn() { return dateIn; }
    public String getDateOut() { return dateOut; }
    public String getDescription() { return description; }

    public void setType(String type) { this.type = type; }
    public void setDateIn(String dateIn) { this.dateIn = dateIn; }
    public void setDateOut(String dateOut) { this.dateOut = dateOut; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Maintenance ID: " + maintenanceLogId + ", Asset: " + assetId + ", Type: " + type + ", Date In: " + dateIn;
    }
}
