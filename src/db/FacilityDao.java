package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Facility lookup — used to validate FK targets before inserting assets. */
public final class FacilityDao {

    private FacilityDao() {}

    public static boolean exists(Connection conn, int facId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Facility WHERE Fac_ID = ? LIMIT 1")) {
            ps.setInt(1, facId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
