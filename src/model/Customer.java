package model;

/**
 * Represents a community member who can rent robots.
 */
public class Customer {
    private final int custId;
    private int facilityId;
    private String fName;
    private String lName;
    private String address;
    private String phone;
    private String email;
    private String startDate;
    private int facilityDistance;
    private String status;

    public Customer(int custId, int facilityId, String fName, String lName, String address,
                    String phone, String email, String startDate, int facilityDistance, String status) {
        this.custId = custId;
        this.facilityId = facilityId;
        this.fName = fName;
        this.lName = lName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.startDate = startDate;
        this.facilityDistance = facilityDistance;
        this.status = status;
    }

    public int getCustId() { return custId; }
    public int getFacilityId() { return facilityId; }
    public String getFName() { return fName; }
    public String getLName() { return lName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStartDate() { return startDate; }
    public int getFacilityDistance() { return facilityDistance; }
    public String getStatus() { return status; }

    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }
    public void setFName(String fName) { this.fName = fName; }
    public void setLName(String lName) { this.lName = lName; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setFacilityDistance(int facilityDistance) { this.facilityDistance = facilityDistance; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Customer ID: " + custId + ", " + fName + " " + lName +
               ", Email: " + email + ", Status: " + status;
    }
}
