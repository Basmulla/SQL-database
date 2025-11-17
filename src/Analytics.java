import java.sql.*;

public class Analytics {
    public static void run(Connection conn, javax.swing.JTextArea output) {
        String query = "SELECT Category, COUNT(*) AS TotalProducts FROM Product GROUP BY Category";
        output.append(" Running analytics query...\n");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            output.append(String.format("%-20s | %-10s\n", "Category", "TotalProducts"));
            output.append("---------------------------------\n");
            while (rs.next()) {
                output.append(String.format("%-20s | %-10s\n", rs.getString("Category"), rs.getInt("TotalProducts")));
            }
            output.append("\n Analytics complete.\n\n");
        } catch (SQLException e) {
            output.append("❌ SQL Error: " + e.getMessage() + "\n\n");
        }
    }
}
