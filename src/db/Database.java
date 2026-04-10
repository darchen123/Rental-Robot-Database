package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite connection and schema bootstrap. All user input is passed via {@link java.sql.PreparedStatement}
 * in DAO classes (never concatenated into SQL strings) to mitigate SQL injection.
 */
public final class Database {

    private static final Path DB_FILE = Path.of("data", "RentalHomeRobot.db");

    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE.toString().replace('\\', '/');

    private Database() {}

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(JDBC_URL);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    /**
     * Ensures {@code data/} exists. If {@code data/RentalHomeRobot.db} does not exist yet, creates it and
     * applies {@code sql/schema.sql} and {@code sql/seed.sql}. If you place your own
     * {@code RentalHomeRobot.db} in {@code data/} first (import/copy), schema/seed are skipped so your
     * tables, views, and data are left unchanged.
     */
    public static void initializeIfNeeded() throws SQLException, IOException {
        Path dataDir = DB_FILE.getParent();
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        boolean bootstrap = !Files.exists(DB_FILE);
        Path schema = Path.of("sql", "schema.sql");
        Path seed = Path.of("sql", "seed.sql");
        try (Connection conn = getConnection()) {
            if (!bootstrap) {
                return;
            }
            if (!Files.exists(schema)) {
                throw new IOException("Missing sql/schema.sql (run from project root).");
            }
            String ddl = Files.readString(schema);
            try (Statement st = conn.createStatement()) {
                for (String stmt : splitSqlStatements(ddl)) {
                    if (!stmt.isBlank()) {
                        st.execute(stmt);
                    }
                }
            }
            if (Files.exists(seed)) {
                String seedSql = Files.readString(seed);
                try (Statement st = conn.createStatement()) {
                    for (String stmt : splitSqlStatements(seedSql)) {
                        if (!stmt.isBlank()) {
                            st.execute(stmt);
                        }
                    }
                }
            }
        }
    }

    private static String[] splitSqlStatements(String sql) {
        return sql.split(";");
    }
}
