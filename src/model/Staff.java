package model;

/**
 * Represents facility staff.
 */
public class Staff {
    private final String ssn;
    private int facilityId;
    private String fName;
    private String lName;
    private String phone;
    private String email;
    private String role;

    public Staff(String ssn, int facilityId, String fName, String lName, String phone, String email, String role) {
        this.ssn = ssn;
        this.facilityId = facilityId;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public String getSsn() { return ssn; }
    public int getFacilityId() { return facilityId; }
    public String getFName() { return fName; }
    public String getLName() { return lName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setFacilityId(int facilityId) { this.facilityId = facilityId; }
    public void setFName(String fName) { this.fName = fName; }
    public void setLName(String lName) { this.lName = lName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Staff SSN: " + ssn + ", " + fName + " " + lName + ", Role: " + role;
    }
}
