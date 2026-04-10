package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Business transactions for rentals — each method uses a single SQL transaction (BEGIN/COMMIT/ROLLBACK).
 * All parameters bound with PreparedStatement.
 */
public final class RentalService {

    private RentalService() {}

    /** Ensures {@code Rental.C_ID} matches the given customer (rental is for this customer). */
    private static void assertRentalForCustomer(Connection conn, int rentalId, int custId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT C_ID FROM Rental WHERE Rental_ID = ?")) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Rental not found.");
                }
                int cid = rs.getInt("C_ID");
                if (cid != custId) {
                    throw new SQLException("This rental is not for customer Cust_ID=" + custId + ".");
                }
            }
        }
    }

    public static int rentRobot(Connection conn, int custId, int robotId, String startDate, String dueDate,
                                int rentalFee) throws SQLException {
        conn.setAutoCommit(false);
        try {
            if (CustomerDao.findById(conn, custId) == null) {
                throw new SQLException("Customer not found.");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Status FROM Autonomous_Asset1 WHERE Asset_ID = ?")) {
                ps.setInt(1, robotId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Robot asset not found.");
                    }
                    String st = rs.getString("Status");
                    if (st != null && !st.equalsIgnoreCase("available")) {
                        throw new SQLException("Robot is not available (must be status 'available').");
                    }
                }
            }
            int rentalId;
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO Rental (Start_Date, End_Date, Rental_Fee, Due_Date, C_ID)
                    VALUES (?, NULL, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, startDate);
                ps.setInt(2, rentalFee);
                ps.setString(3, dueDate);
                ps.setInt(4, custId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Rental insert failed.");
                    }
                    rentalId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Rents (Robot_ID, Rental_ID) VALUES (?, ?)")) {
                ps.setInt(1, robotId);
                ps.setInt(2, rentalId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Autonomous_Asset1 SET Status = ? WHERE Asset_ID = ?")) {
                ps.setString(1, "in use");
                ps.setInt(2, robotId);
                ps.executeUpdate();
            }
            conn.commit();
            return rentalId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void returnEquipment(Connection conn, int rentalId, int custId, String endDate) throws SQLException {
        conn.setAutoCommit(false);
        try {
            assertRentalForCustomer(conn, rentalId, custId);
            int robotId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Robot_ID FROM Rents WHERE Rental_ID = ?")) {
                ps.setInt(1, rentalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Rental not found.");
                    }
                    robotId = rs.getInt("Robot_ID");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("UPDATE Rental SET End_Date = ? WHERE Rental_ID = ?")) {
                ps.setString(1, endDate);
                ps.setInt(2, rentalId);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Rental update failed.");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Autonomous_Asset1 SET Status = ? WHERE Asset_ID = ?")) {
                ps.setString(1, "available");
                ps.setInt(2, robotId);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void scheduleDelivery(Connection conn, int rentalId, int custId, int dvId, String dDate, String dTime)
            throws SQLException {
        conn.setAutoCommit(false);
        try {
            assertRentalForCustomer(conn, rentalId, custId);
            int robotId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Robot_ID FROM Rents WHERE Rental_ID = ?")) {
                ps.setInt(1, rentalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Rental not found.");
                    }
                    robotId = rs.getInt("Robot_ID");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT OR REPLACE INTO Rental_Delivery (Robot_ID, Rental_ID, DDate, DTime, DV_ID)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {
                ps.setInt(1, robotId);
                ps.setInt(2, rentalId);
                ps.setString(3, dDate);
                ps.setString(4, dTime);
                ps.setInt(5, dvId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Autonomous_Asset1 SET Status = ? WHERE Asset_ID = ?")) {
                ps.setString(1, "in use");
                ps.setInt(2, dvId);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void schedulePickup(Connection conn, int rentalId, int custId, int dvId, String rDate, String rTime)
            throws SQLException {
        conn.setAutoCommit(false);
        try {
            assertRentalForCustomer(conn, rentalId, custId);
            int robotId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT Robot_ID FROM Rents WHERE Rental_ID = ?")) {
                ps.setInt(1, rentalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Rental not found.");
                    }
                    robotId = rs.getInt("Robot_ID");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                    INSERT OR REPLACE INTO Rental_Return (Robot_ID, Rental_ID, RDate, RTime, DV_ID)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {
                ps.setInt(1, robotId);
                ps.setInt(2, rentalId);
                ps.setString(3, rDate);
                ps.setString(4, rTime);
                ps.setInt(5, dvId);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
