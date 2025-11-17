import java.sql.*;

public class ViewReports {
    public static void showProducts(Connection conn, javax.swing.JTextArea output) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Product")) {

            output.append(" Product List:\n");
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    output.append(md.getColumnName(i) + ": " + rs.getString(i) + "\t");
                }
                output.append("\n");
            }
            output.append("\n Displayed all products.\n\n");
        } catch (SQLException e) {
            output.append(" SQL Error: " + e.getMessage() + "\n\n");
        }
    }

    public static void showCustomers(Connection conn, javax.swing.JTextArea output) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Customer")) {

            output.append(" Customer List:\n");
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    output.append(md.getColumnName(i) + ": " + rs.getString(i) + "\t");
                }
                output.append("\n");
            }
            output.append("\n Displayed all customers.\n\n");
        } catch (SQLException e) {
            output.append(" SQL Error: " + e.getMessage() + "\n\n");
        }
    }
}
