package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Creates {@code Internal_Order_Request} rows required by {@code Autonomous_Asset1.Order_ID} (NOT NULL).
 */
public final class InternalOrderDao {

    private InternalOrderDao() {}

    public static int insertOrderRequest(Connection conn, int facId, String elementType) throws SQLException {
        int orderId = nextOrderId(conn);
        final String sql = """
                INSERT INTO Internal_Order_Request (Order_ID, Fac_ID, Element_Type, Quantity, Value, Est_Arrival_Date, Arrival_Date)
                VALUES (?, ?, ?, ?, ?, date('now'), NULL)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, facId);
            ps.setString(3, elementType);
            ps.setInt(4, 1);
            ps.setInt(5, 0);
            ps.executeUpdate();
        }
        return orderId;
    }

    private static int nextOrderId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(MAX(Order_ID), 0) + 1 FROM Internal_Order_Request")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
