package db;

import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Customer CRUD — uses PreparedStatement for all parameters (SQL injection safe). */
public final class CustomerDao {

    private CustomerDao() {}

    public static int insert(Connection conn, int facId, String fName, String lName, String address,
                             String phone, String email, String startDate, int facDist, String status)
            throws SQLException {
        final String sql = """
                INSERT INTO Customer (Fac_ID, FName, LName, Address, Phone, Email, Start_Date, Fac_Dist, Status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, facId);
            ps.setString(2, fName);
            ps.setString(3, lName);
            ps.setString(4, address);
            ps.setString(5, phone);
            ps.setString(6, email);
            ps.setString(7, startDate);
            ps.setInt(8, facDist);
            ps.setString(9, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Insert customer failed.");
    }

    public static boolean update(Connection conn, int custId, String status, String address, String phone)
            throws SQLException {
        final String sql = "UPDATE Customer SET Status = ?, Address = ?, Phone = ? WHERE Cust_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, address);
            ps.setString(3, phone);
            ps.setInt(4, custId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(Connection conn, int custId) throws SQLException {
        final String sql = "DELETE FROM Customer WHERE Cust_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Customer findById(Connection conn, int custId) throws SQLException {
        final String sql = "SELECT * FROM Customer WHERE Cust_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, custId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public static List<Customer> searchByNameLike(Connection conn, String nameFragment) throws SQLException {
        final String sql = """
                SELECT * FROM Customer
                WHERE lower(FName || ' ' || LName) LIKE lower(?)
                """;
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nameFragment + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public static List<Customer> searchByEmailLike(Connection conn, String emailFragment) throws SQLException {
        final String sql = "SELECT * FROM Customer WHERE Email IS NOT NULL AND lower(Email) LIKE lower(?)";
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + emailFragment + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public static List<Customer> searchByStatus(Connection conn, String status) throws SQLException {
        final String sql = "SELECT * FROM Customer WHERE Status = ?";
        List<Customer> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private static Customer map(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("Cust_ID"),
                rs.getInt("Fac_ID"),
                rs.getString("FName"),
                rs.getString("LName"),
                rs.getString("Address"),
                rs.getString("Phone"),
                rs.getString("Email"),
                rs.getString("Start_Date"),
                rs.getInt("Fac_Dist"),
                rs.getString("Status")
        );
    }
}
