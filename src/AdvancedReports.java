import java.sql.*;

/**
 * AdvancedReports.java
 * Fixed to match the actual database schema in 02_create_tables.sql
 */
public class AdvancedReports {

    // -------------------------------------------------------------
    // 1. Customer RFM Snapshot (FIXED)
    // -------------------------------------------------------------
    public static void customerRFMSnapshot(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT c.CustomerID, " +
            "       c.Name AS CustomerName, " +
            "       MAX(o.OrderDate) AS LastPurchase, " +
            "       ROUND(SYSDATE - MAX(o.OrderDate)) AS RecencyDays, " +
            "       COUNT(o.OrderID) AS PurchaseFrequency, " +
            "       NVL(SUM(o.OrderTotal), 0) AS MonetaryValue " +
            "FROM Customer c " +
            "LEFT JOIN Orders o ON c.CustomerID = o.CustomerID " +
            "GROUP BY c.CustomerID, c.Name " +
            "ORDER BY MonetaryValue DESC";

        output.append("📊 CUSTOMER RFM SNAPSHOT:\n");
        output.append("(Recency, Frequency, Monetary Value Analysis)\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 2. Order Health Check (Payment vs Order Total)
    // -------------------------------------------------------------
    public static void orderHealthCheck(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT o.OrderID, " +
            "       o.OrderTotal, " +
            "       NVL(p.Amount, 0) AS AmountPaid, " +
            "       (o.OrderTotal - NVL(p.Amount, 0)) AS Outstanding, " +
            "       o.Status AS OrderStatus, " +
            "       p.PaymentMethod " +
            "FROM Orders o " +
            "LEFT JOIN Payment p ON o.OrderID = p.OrderID " +
            "ORDER BY Outstanding DESC";

        output.append("⚠️ ORDER HEALTH CHECK (Payment Status):\n");
        output.append("Shows orders with outstanding balances\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 3. Product Revenue by Brand (FIXED - no Category column exists)
    // -------------------------------------------------------------
    public static void productRevenueByCategory(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT p.Brand, " +
            "       COUNT(DISTINCT p.ProductID) AS ProductCount, " +
            "       SUM(od.Quantity) AS TotalUnitsSold, " +
            "       SUM(od.Quantity * od.PurchasePrice) AS TotalRevenue " +
            "FROM Product p " +
            "JOIN OrderDetails od ON p.ProductID = od.ProductID " +
            "GROUP BY p.Brand " +
            "ORDER BY TotalRevenue DESC";

        output.append("💰 PRODUCT REVENUE BY BRAND:\n");
        output.append("Showing sales performance by product brand\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 4. Low-Stock Alert (FIXED)
    // -------------------------------------------------------------
    public static void lowStockAlert(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT ProductID, " +
            "       Name AS ProductName, " +
            "       Brand, " +
            "       StockQuantity, " +
            "       Price, " +
            "       CASE " +
            "           WHEN StockQuantity = 0 THEN 'OUT OF STOCK' " +
            "           WHEN StockQuantity <= 5 THEN 'LOW STOCK' " +
            "           WHEN StockQuantity <= 15 THEN 'MODERATE' " +
            "           ELSE 'SUFFICIENT' " +
            "       END AS StockStatus " +
            "FROM Product " +
            "WHERE IsActive = 'Y' " +
            "ORDER BY StockQuantity ASC";

        output.append("🚨 LOW-STOCK ALERT:\n");
        output.append("Products that need restocking\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 5. Shipping SLA Aging (FIXED)
    // -------------------------------------------------------------
    public static void shippingSLAAging(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT s.ShippingID, " +
            "       s.OrderID, " +
            "       c.Name AS CustomerName, " +
            "       o.OrderDate, " +
            "       s.Courier, " +
            "       s.Status AS ShippingStatus, " +
            "       s.TrackingNum, " +
            "       s.DeliveryDate, " +
            "       ROUND(SYSDATE - o.OrderDate) AS DaysSinceOrder, " +
            "       CASE " +
            "           WHEN s.Status = 'DELIVERED' THEN 'Completed' " +
            "           WHEN s.Status = 'SHIPPING' AND (SYSDATE - o.OrderDate) > 7 THEN 'DELAYED' " +
            "           WHEN s.Status = 'SHIPPING' THEN 'In Transit' " +
            "           ELSE 'Pending' " +
            "       END AS SLA_Status " +
            "FROM Shipping s " +
            "JOIN Orders o ON s.OrderID = o.OrderID " +
            "JOIN Customer c ON o.CustomerID = c.CustomerID " +
            "ORDER BY DaysSinceOrder DESC";

        output.append("📦 SHIPPING SLA AGING:\n");
        output.append("Tracking delivery performance and delays\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 6. BONUS: Staff Performance Report
    // -------------------------------------------------------------
    public static void staffPerformanceReport(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT s.StaffID, " +
            "       s.Name AS StaffName, " +
            "       s.Role, " +
            "       COUNT(DISTINCT o.OrderID) AS OrdersProcessed, " +
            "       NVL(SUM(o.OrderTotal), 0) AS TotalValueProcessed, " +
            "       ROUND(AVG(o.OrderTotal), 2) AS AvgOrderValue " +
            "FROM Staff s " +
            "LEFT JOIN Orders o ON s.StaffID = o.StaffID " +
            "GROUP BY s.StaffID, s.Name, s.Role " +
            "ORDER BY TotalValueProcessed DESC";

        output.append("👥 STAFF PERFORMANCE REPORT:\n");
        output.append("Orders processed and revenue generated by staff\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // 7. BONUS: Product Category Breakdown (Books, Clothing, Electronics)
    // -------------------------------------------------------------
    public static void productCategoryBreakdown(Connection conn, javax.swing.JTextArea output) {
        String query =
            "SELECT 'Books' AS Category, COUNT(*) AS ProductCount, " +
            "       SUM(p.StockQuantity) AS TotalStock " +
            "FROM Books b " +
            "JOIN Product p ON b.ProductID = p.ProductID " +
            "UNION ALL " +
            "SELECT 'Clothing', COUNT(*), SUM(p.StockQuantity) " +
            "FROM Clothing c " +
            "JOIN Product p ON c.ProductID = p.ProductID " +
            "UNION ALL " +
            "SELECT 'Electronics', COUNT(*), SUM(p.StockQuantity) " +
            "FROM Electronics e " +
            "JOIN Product p ON e.ProductID = p.ProductID " +
            "ORDER BY ProductCount DESC";

        output.append("📚📱👕 PRODUCT CATEGORY BREAKDOWN:\n");
        output.append("Inventory distribution across product types\n\n");
        executeAndPrint(conn, output, query);
    }


    // -------------------------------------------------------------
    // Helper method: Execute query and print results in table format
    // -------------------------------------------------------------
    private static void executeAndPrint(Connection conn, javax.swing.JTextArea output, String query) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            // Print header
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= columns; i++) {
                header.append(String.format("%-25s", md.getColumnName(i)));
            }
            output.append(header.toString() + "\n");

            // Print divider
            output.append("=".repeat(columns * 25) + "\n");

            // Print rows
            int rowCount = 0;
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columns; i++) {
                    String value = rs.getString(i);
                    row.append(String.format("%-25s", value != null ? value : "NULL"));
                }
                output.append(row.toString() + "\n");
                rowCount++;
            }

            // Print summary
            if (rowCount == 0) {
                output.append("(No data found)\n");
            } else {
                output.append("\n✅ " + rowCount + " row(s) returned.\n");
            }
            output.append("\n");

        } catch (SQLException e) {
            output.append("❌ SQL Error: " + e.getMessage() + "\n");
            output.append("   Error Code: " + e.getErrorCode() + "\n");
            output.append("   SQL State: " + e.getSQLState() + "\n\n");
        }
    }
}