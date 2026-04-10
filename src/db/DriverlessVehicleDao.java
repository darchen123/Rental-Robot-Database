package db;

import model.DriverlessVehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DriverlessVehicleDao {

    private DriverlessVehicleDao() {}

    private static void ensureModel(Connection conn, String model, String manufacturer) throws SQLException {
        final String sql = "INSERT OR IGNORE INTO Autonomous_Asset2 (Model, Manufacturer) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model);
            ps.setString(2, manufacturer);
            ps.executeUpdate();
        }
    }

    private static void upsertFacilityLocation(Connection conn, int facId, String location) throws SQLException {
        final String sql = "INSERT OR REPLACE INTO Autonomous_Asset3 (Fac_ID, Location) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facId);
            ps.setString(2, location);
            ps.executeUpdate();
        }
    }

    public static int insert(Connection conn, int facId, String warrantyExp, String status, String manufacturer,
                             String model, int year, int serialNo, String location, int distanceAutonomy,
                             String licensePlate, int maxSpeed, int payloadCapacity) throws SQLException {
        if (!FacilityDao.exists(conn, facId)) {
            throw new SQLException(
                    "No row in Facility for Fac_ID=" + facId + ". Use an existing facility id (e.g. from your seed data).");
        }
        conn.setAutoCommit(false);
        try {
            ensureModel(conn, model, manufacturer);
            int orderId = InternalOrderDao.insertOrderRequest(conn, facId, "driverless vehicle");
            int assetId = DbIds.nextAutonomousAssetId(conn);
            final String sqlAa = """
                    INSERT INTO Autonomous_Asset1 (Asset_ID, Fac_ID, Warrenty_Exp, Status, Model, Year, Serial_no, Order_ID)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlAa)) {
                ps.setInt(1, assetId);
                ps.setInt(2, facId);
                ps.setString(3, warrantyExp);
                ps.setString(4, status);
                ps.setString(5, model);
                ps.setInt(6, year);
                ps.setInt(7, serialNo);
                ps.setInt(8, orderId);
                ps.executeUpdate();
            }
            final String sqlDv = """
                    INSERT INTO Driverless_Vehicle (AA_ID, Distance_Autonomy, License_Plate, Max_Speed, Payload_Capacity)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlDv)) {
                ps.setInt(1, assetId);
                ps.setInt(2, distanceAutonomy);
                ps.setString(3, licensePlate);
                ps.setInt(4, maxSpeed);
                ps.setInt(5, payloadCapacity);
                ps.executeUpdate();
            }
            upsertFacilityLocation(conn, facId, location);
            conn.commit();
            return assetId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static boolean update(Connection conn, int aaId, String manufacturer, String model, String status,
                                 String location) throws SQLException {
        ensureModel(conn, model, manufacturer);
        final String sql = "UPDATE Autonomous_Asset1 SET Model = ?, Status = ? WHERE Asset_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model);
            ps.setString(2, status);
            ps.setInt(3, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("SELECT Fac_ID FROM Autonomous_Asset1 WHERE Asset_ID = ?")) {
            ps.setInt(1, aaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    upsertFacilityLocation(conn, rs.getInt("Fac_ID"), location);
                }
            }
        }
        return true;
    }

    public static boolean delete(Connection conn, int aaId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Maintains WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Maintenance_Log WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental_Delivery WHERE DV_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental_Return WHERE DV_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Driverless_Vehicle WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Autonomous_Asset1 WHERE Asset_ID = ?")) {
            ps.setInt(1, aaId);
            return ps.executeUpdate() > 0;
        }
    }

    public static DriverlessVehicle findById(Connection conn, int aaId) throws SQLException {
        final String sql = """
                SELECT a.Asset_ID, a.Fac_ID, a.Warrenty_Exp, a.Status, a.Model, a.Year, a.Serial_no, a.Order_ID,
                       m.Manufacturer, d.Distance_Autonomy, d.License_Plate, d.Max_Speed, d.Payload_Capacity,
                       loc.Location AS Fac_Location
                FROM Autonomous_Asset1 a
                JOIN Autonomous_Asset2 m ON a.Model = m.Model
                JOIN Driverless_Vehicle d ON d.AA_ID = a.Asset_ID
                LEFT JOIN Autonomous_Asset3 loc ON loc.Fac_ID = a.Fac_ID
                WHERE a.Asset_ID = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public static List<DriverlessVehicle> searchByPlateLike(Connection conn, String fragment) throws SQLException {
        return queryList(conn, "WHERE lower(d.License_Plate) LIKE lower(?)", "%" + fragment + "%");
    }

    public static List<DriverlessVehicle> searchByStatus(Connection conn, String status) throws SQLException {
        return queryList(conn, "WHERE a.Status = ?", status);
    }

    private static List<DriverlessVehicle> queryList(Connection conn, String whereClause, String param)
            throws SQLException {
        final String sql = """
                SELECT a.Asset_ID, a.Fac_ID, a.Warrenty_Exp, a.Status, a.Model, a.Year, a.Serial_no, a.Order_ID,
                       m.Manufacturer, d.Distance_Autonomy, d.License_Plate, d.Max_Speed, d.Payload_Capacity,
                       loc.Location AS Fac_Location
                FROM Autonomous_Asset1 a
                JOIN Autonomous_Asset2 m ON a.Model = m.Model
                JOIN Driverless_Vehicle d ON d.AA_ID = a.Asset_ID
                LEFT JOIN Autonomous_Asset3 loc ON loc.Fac_ID = a.Fac_ID
                """ + whereClause;
        List<DriverlessVehicle> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private static DriverlessVehicle map(ResultSet rs) throws SQLException {
        String loc = rs.getString("Fac_Location");
        if (loc == null) {
            loc = "";
        }
        return new DriverlessVehicle(
                rs.getInt("Asset_ID"),
                rs.getInt("Fac_ID"),
                rs.getString("Warrenty_Exp"),
                rs.getString("Status"),
                rs.getString("Manufacturer"),
                rs.getString("Model"),
                rs.getInt("Year"),
                String.valueOf(rs.getInt("Serial_no")),
                loc,
                rs.getInt("Order_ID"),
                rs.getInt("Distance_Autonomy"),
                rs.getString("License_Plate"),
                rs.getInt("Max_Speed"),
                rs.getInt("Payload_Capacity")
        );
    }
}
