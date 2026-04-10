package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Explicit ID allocation for databases where {@code Autonomous_Asset1.Asset_ID} is not an SQLite
 * {@code INTEGER PRIMARY KEY} rowid alias (e.g. {@code INT PRIMARY KEY} DDL), so JDBC generated keys are empty.
 */
public final class DbIds {

    private DbIds() {}

    public static int nextAutonomousAssetId(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COALESCE(MAX(Asset_ID), 0) + 1 FROM Autonomous_Asset1")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
