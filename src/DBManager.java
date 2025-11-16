import java.sql.*;
import java.io.*;

/**
 * DBManager.java
 * Handles all database connections and SQL executions for CPS510 A9
 * Author: Basmulla Atekulla, Rochelle, Michelle
 */

public class DBManager {
    private Connection conn;

    // Connect to Oracle
    public boolean connect(String username, String password) {
        String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl)))";

        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to Oracle DB as " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            return false;
        }
    }

    // Execute SQL file
    public void executeSQLFile(String filePath) {
        if (conn == null) {
            System.err.println("❌ No active database connection.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("--"))
                    continue;

                sb.append(line).append(" ");

                if (line.trim().endsWith(";")) {
                    String sql = sb.toString().replace(";", "");
                    stmt.execute(sql);
                    sb.setLength(0);
                }
            }

            System.out.println("✅ Executed file: " + filePath);
        } catch (Exception e) {
            System.err.println("❌ Error running SQL file: " + e.getMessage());
        }
    }

    // Run SELECT queries
    public ResultSet runQuery(String query) throws SQLException {
        if (conn == null) throw new SQLException("No connection");
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    // Close connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
	public Connection getConnection() {
    return conn;
	}
}

