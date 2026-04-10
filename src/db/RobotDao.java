package db;

import model.Robot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Robot + Autonomous_Asset1 + Autonomous_Asset2 + Autonomous_Asset3 (facility location).
 * PreparedStatement only.
 */
public final class RobotDao {

    private RobotDao() {}

    private static void ensureModel(Connection conn, String model, String manufacturer) throws SQLException {
        final String sql = "INSERT OR IGNORE INTO Autonomous_Asset2 (Model, Manufacturer) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model);
            ps.setString(2, manufacturer);
            ps.executeUpdate();
        }
    }

    /** Persists facility-level location in {@code Autonomous_Asset3} (one row per facility per your schema). */
    private static void upsertFacilityLocation(Connection conn, int facId, String location) throws SQLException {
        final String sql = "INSERT OR REPLACE INTO Autonomous_Asset3 (Fac_ID, Location) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facId);
            ps.setString(2, location);
            ps.executeUpdate();
        }
    }

    public static int insert(Connection conn, int facId, String warrantyExp, String status, String manufacturer,
                             String model, int year, int serialNo, String location, int batteryAutonomy,
                             String sensors, String training, String function) throws SQLException {
        if (!FacilityDao.exists(conn, facId)) {
            throw new SQLException(
                    "No row in Facility for Fac_ID=" + facId + ". Use an existing facility id (e.g. from your seed data).");
        }
        conn.setAutoCommit(false);
        try {
            ensureModel(conn, model, manufacturer);
            int orderId = InternalOrderDao.insertOrderRequest(conn, facId, "robot");
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
            final String sqlR = """
                    INSERT INTO Robot (AA_ID, Battery_Autonomy, Sensors, Training, Function)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sqlR)) {
                ps.setInt(1, assetId);
                ps.setInt(2, batteryAutonomy);
                ps.setString(3, sensors);
                ps.setString(4, training.isEmpty() ? null : training);
                ps.setString(5, function);
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
                                 String location, String training) throws SQLException {
        ensureModel(conn, model, manufacturer);
        final String sqlAa = "UPDATE Autonomous_Asset1 SET Model = ?, Status = ? WHERE Asset_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlAa)) {
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
        final String sqlR = "UPDATE Robot SET Training = ? WHERE AA_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlR)) {
            ps.setString(1, training.isEmpty() ? null : training);
            ps.setInt(2, aaId);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(Connection conn, int aaId) throws SQLException {
        List<Integer> rentalIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT Rental_ID FROM Rents WHERE Robot_ID = ?")) {
            ps.setInt(1, aaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rentalIds.add(rs.getInt(1));
                }
            }
        }
        for (int rid : rentalIds) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Payment WHERE Rental_ID = ?")) {
                ps.setInt(1, rid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental_Delivery WHERE Rental_ID = ?")) {
                ps.setInt(1, rid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental_Return WHERE Rental_ID = ?")) {
                ps.setInt(1, rid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rents WHERE Rental_ID = ?")) {
                ps.setInt(1, rid);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Rental WHERE Rental_ID = ?")) {
                ps.setInt(1, rid);
                ps.executeUpdate();
            }
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Maintains WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Maintenance_Log WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Robot WHERE AA_ID = ?")) {
            ps.setInt(1, aaId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Autonomous_Asset1 WHERE Asset_ID = ?")) {
            ps.setInt(1, aaId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Robot findById(Connection conn, int aaId) throws SQLException {
        final String sql = """
                SELECT a.Asset_ID, a.Fac_ID, a.Warrenty_Exp, a.Status, a.Model, a.Year, a.Serial_no, a.Order_ID,
                       m.Manufacturer, r.Battery_Autonomy, r.Sensors, r.Training, r.Function,
                       loc.Location AS Fac_Location
                FROM Autonomous_Asset1 a
                JOIN Autonomous_Asset2 m ON a.Model = m.Model
                JOIN Robot r ON r.AA_ID = a.Asset_ID
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

    public static List<Robot> searchByModelLike(Connection conn, String fragment) throws SQLException {
        return queryList(conn, """
                WHERE lower(a.Model) LIKE lower(?)
                """, "%" + fragment + "%");
    }

    public static List<Robot> searchByStatus(Connection conn, String status) throws SQLException {
        return queryList(conn, "WHERE a.Status = ?", status);
    }

    public static List<Robot> searchByFunctionLike(Connection conn, String fragment) throws SQLException {
        return queryList(conn, """
                WHERE lower(r.Function) LIKE lower(?)
                """, "%" + fragment + "%");
    }

    private static List<Robot> queryList(Connection conn, String whereClause, String param) throws SQLException {
        final String sql = """
                SELECT a.Asset_ID, a.Fac_ID, a.Warrenty_Exp, a.Status, a.Model, a.Year, a.Serial_no, a.Order_ID,
                       m.Manufacturer, r.Battery_Autonomy, r.Sensors, r.Training, r.Function,
                       loc.Location AS Fac_Location
                FROM Autonomous_Asset1 a
                JOIN Autonomous_Asset2 m ON a.Model = m.Model
                JOIN Robot r ON r.AA_ID = a.Asset_ID
                LEFT JOIN Autonomous_Asset3 loc ON loc.Fac_ID = a.Fac_ID
                """ + whereClause;
        List<Robot> list = new ArrayList<>();
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

    private static Robot map(ResultSet rs) throws SQLException {
        String loc = rs.getString("Fac_Location");
        if (loc == null) {
            loc = "";
        }
        return new Robot(
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
                rs.getInt("Battery_Autonomy"),
                rs.getString("Sensors"),
                rs.getString("Training"),
                rs.getString("Function"),
                null
        );
    }
}
