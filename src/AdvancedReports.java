import java.sql.*;

public class AdvancedReports {

    // -------------------------------------------------------------
    // 1. Customer RFM Snapshot
    // -------------------------------------------------------------
    public static void customerRFMSnapshot(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT c.CustomerID, c.FirstName, c.LastName, " +
            "       MAX(o.OrderDate) AS LastPurchase, " +
            "       COUNT(o.OrderID) AS PurchaseFrequency, " +
            "       SUM(o.TotalAmount) AS MonetaryValue " +
            "FROM Customer c " +
            "LEFT JOIN Orders o ON c.CustomerID = o.CustomerID " +
            "GROUP BY c.CustomerID, c.FirstName, c.LastName " +
            "ORDER BY MonetaryValue DESC";

        output.append("📊 CUSTOMER RFM SNAPSHOT:\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 2. Order Health Check (Payment mismatches)
    // -------------------------------------------------------------
    public static void orderHealthCheck(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT o.OrderID, o.TotalAmount AS OrderTotal, p.AmountPaid, " +
            "       (p.AmountPaid - o.TotalAmount) AS Difference, " +
            "       p.PaymentStatus " +
            "FROM Orders o " +
            "JOIN Payment p ON o.OrderID = p.OrderID " +
            "WHERE ABS(p.AmountPaid - o.TotalAmount) > 0.01";

        output.append("⚠️ ORDER HEALTH CHECK (Payment Mismatches):\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 3. Product Revenue by Category
    // -------------------------------------------------------------
    public static void productRevenueByCategory(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT p.Category, SUM(oi.Quantity * oi.UnitPrice) AS Revenue " +
            "FROM OrderItems oi " +
            "JOIN Product p ON oi.ProductID = p.ProductID " +
            "GROUP BY p.Category " +
            "ORDER BY Revenue DESC";

        output.append("💰 PRODUCT REVENUE BY CATEGORY:\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 4. Low-Stock Alert
    // -------------------------------------------------------------
    public static void lowStockAlert(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT ProductID, ProductName, StockQuantity " +
            "FROM Product " +
            "WHERE StockQuantity < 10 " +
            "ORDER BY StockQuantity ASC";

        output.append("🚨 LOW-STOCK ALERT:\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 5. Shipping SLA Aging
    // -------------------------------------------------------------
    public static void shippingSLAAging(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT o.OrderID, o.OrderDate, s.ShipDate, " +
            "       (s.ShipDate - o.OrderDate) AS DaysToShip " +
            "FROM Orders o " +
            "JOIN Shipment s ON o.OrderID = s.OrderID " +
            "ORDER BY DaysToShip DESC";

        output.append("📦 SHIPPING SLA AGING:\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // prints any query in a nice table format
    // -------------------------------------------------------------
    private static void executeAndPrint(Connection conn, javax.swing.JTextArea output, String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // Print header
            for (int i = 1; i <= columns; i++) {
                output.append(String.format("%-20s", md.getColumnName(i)));
            }
            output.append("\n");

            // Divider
            output.append("------------------------------------------------------------\n");

            // Print each row
            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    output.append(String.format("%-20s", rs.getString(i)));
                }
                output.append("\n");
            }
            output.append("\n");

        } catch (SQLException e) {
            output.append("❌ SQL Error: " + e.getMessage() + "\n\n");
        }
    }
}
