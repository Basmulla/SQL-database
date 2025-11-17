import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UI_Main.java E-Commerce Database Swing Interface for Oracle 11g 
 * Authors: Basmulla Atekulla, Rochelle, Michelle
 * Fixed: Added proper credential validation and error handling
 */
public class UI_Main extends JFrame {

    private Connection conn;
    private JTextArea outputArea;

    public UI_Main() {
        super("CPS510 E-Commerce Database UI");

        setLayout(new BorderLayout());

        // --- Left Panel (Buttons)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(11, 1, 5, 5));

        JButton btnDrop = new JButton("Drop Tables");
        JButton btnCreate = new JButton("Create Tables");
        JButton btnPopulate = new JButton("Populate Tables");
        JButton btnViewProducts = new JButton("View Products");
        JButton btnAnalytics = new JButton("Run Analytics");
        JButton btnExit = new JButton("Exit");

        /* Advanced Report buttons */
        JButton btnRFM = new JButton("Customer RFM Snapshot");
        JButton btnOrderHealth = new JButton("Order Health Check");
        JButton btnRevenue = new JButton("Revenue by Brand");
        JButton btnLowStock = new JButton("Low Stock Alert");
        JButton btnSLA = new JButton("Shipping SLA Aging");

        buttonPanel.add(btnDrop);
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnPopulate);
        buttonPanel.add(btnViewProducts);
        buttonPanel.add(btnAnalytics);
        buttonPanel.add(btnRFM);
        buttonPanel.add(btnOrderHealth);
        buttonPanel.add(btnRevenue);
        buttonPanel.add(btnLowStock);
        buttonPanel.add(btnSLA);
        buttonPanel.add(btnExit);

        add(buttonPanel, BorderLayout.WEST);

        // --- Center Panel (Output)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // --- IMPROVED CREDENTIAL HANDLING ---
        DBManager db = new DBManager();
        String username = null;
        String password = null;
        boolean validCredentials = false;

        // Try up to 3 times to get valid credentials
        for (int attempt = 1; attempt <= 3; attempt++) {
            // Get username
            username = JOptionPane.showInputDialog(this, 
                "Enter Oracle username" + (attempt > 1 ? " (Attempt " + attempt + "/3)" : "") + ":",
                "Database Login",
                JOptionPane.QUESTION_MESSAGE);

            // Check if user clicked Cancel
            if (username == null) {
                outputArea.append("⚠️ Login cancelled by user.\n");
                JOptionPane.showMessageDialog(this, 
                    "Login cancelled. Application will exit.",
                    "Login Cancelled", 
                    JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            username = username.trim();
            
            // Validate username is not empty
            if (username.isEmpty()) {
                outputArea.append("❌ Username cannot be empty (Attempt " + attempt + "/3).\n");
                JOptionPane.showMessageDialog(this, 
                    "Username cannot be empty. Please try again.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Get password
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(this, pf, 
                "Enter Oracle password for user: " + username, 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);

            if (okCxl != JOptionPane.OK_OPTION) {
                outputArea.append("⚠️ Login cancelled by user.\n");
                JOptionPane.showMessageDialog(this, 
                    "Login cancelled. Application will exit.",
                    "Login Cancelled", 
                    JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            password = new String(pf.getPassword()).trim();

            // Validate password is not empty
            if (password.isEmpty()) {
                outputArea.append("❌ Password cannot be empty (Attempt " + attempt + "/3).\n");
                JOptionPane.showMessageDialog(this, 
                    "Password cannot be empty. Please try again.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Credentials are valid (not empty), break the loop
            validCredentials = true;
            break;
        }

        // If after 3 attempts we don't have valid credentials, exit
        if (!validCredentials) {
            outputArea.append("❌ Maximum login attempts exceeded. Exiting.\n");
            JOptionPane.showMessageDialog(this, 
                "Maximum login attempts exceeded.",
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        // --- Try to Connect ---
        outputArea.append("🔄 Attempting to connect to Oracle DB...\n");
        outputArea.append("    Host: oracle.scs.ryerson.ca:1521\n");
        outputArea.append("    User: " + username + "\n\n");
        
        if (db.connect(username, password)) {
            conn = db.getConnection();
            outputArea.append("✅ Connected to Oracle DB as " + username + "\n");
            outputArea.append("    Connection successful!\n\n");
            
            JOptionPane.showMessageDialog(this, 
                "Successfully connected to database!",
                "Connection Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            outputArea.append("❌ Failed to connect to Oracle DB.\n");
            outputArea.append("   Possible reasons:\n");
            outputArea.append("   - Incorrect username or password\n");
            outputArea.append("   - Not connected to TMU VPN (required for off-campus access)\n");
            outputArea.append("   - Database server is down\n");
            outputArea.append("   - JDBC driver (ojdbc6.jar) not found\n\n");
            
            int retry = JOptionPane.showConfirmDialog(this,
                "Failed to connect to database.\n\n" +
                "Common solutions:\n" +
                "• Verify username and password\n" +
                "• Connect to TMU VPN if off-campus\n" +
                "• Check that ojdbc6.jar is in /lib folder\n\n" +
                "Would you like to try again?",
                "Connection Failed",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
            
            if (retry == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(UI_Main::new);
                return;
            } else {
                System.exit(0);
            }
        }

        // --- Button Actions ---
        btnDrop.addActionListener(e -> runSQL("sql/01_drop_tables.sql"));
        btnCreate.addActionListener(e -> runSQL("sql/02_create_tables.sql"));
        btnPopulate.addActionListener(e -> runSQL("sql/03_populate_tables.sql"));
        btnViewProducts.addActionListener(e -> displayQuery("SELECT * FROM Product"));
        
        // Fixed: Product table doesn't have Category column, use Brand instead
        btnAnalytics.addActionListener(e -> 
            displayQuery("SELECT Brand, COUNT(*) AS TotalProducts, SUM(StockQuantity) AS TotalStock " +
                        "FROM Product GROUP BY Brand ORDER BY TotalProducts DESC"));
        
        // Advanced Reports - using fixed version
        btnRFM.addActionListener(e -> AdvancedReports.customerRFMSnapshot(conn, outputArea));
        btnOrderHealth.addActionListener(e -> AdvancedReports.orderHealthCheck(conn, outputArea));
        btnRevenue.addActionListener(e -> AdvancedReports.productRevenueByCategory(conn, outputArea));
        btnLowStock.addActionListener(e -> AdvancedReports.lowStockAlert(conn, outputArea));
        btnSLA.addActionListener(e -> AdvancedReports.shippingSLAAging(conn, outputArea));

        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                closeConnection();
                System.exit(0);
            }
        });

        // --- Frame Settings ---
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    // --- Run SQL Script ---
    private void runSQL(String path) {
        if (conn == null) {
            outputArea.append("⚠️ No active connection.\n");
            JOptionPane.showMessageDialog(this,
                "No database connection available.\nPlease restart and login.",
                "Connection Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        outputArea.append("🔄 Running " + path + "...\n");
        try {
            JdbcOracleConnectionTemplate.runSQLFile(conn, path);
            outputArea.append("✅ Completed: " + path + "\n\n");
        } catch (Exception ex) {
            outputArea.append("❌ Error executing " + path + ": " + ex.getMessage() + "\n\n");
            JOptionPane.showMessageDialog(this,
                "Error executing SQL file:\n" + ex.getMessage(),
                "SQL Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Execute SELECT Query ---
    private void displayQuery(String query) {
        if (conn == null) {
            outputArea.append("⚠️ No active connection.\n");
            JOptionPane.showMessageDialog(this,
                "No database connection available.\nPlease restart and login.",
                "Connection Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        outputArea.append("🔄 Executing query...\n");
        outputArea.append("Query: " + query + "\n\n");

        try (Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            // Print column headers
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                header.append(String.format("%-20s", md.getColumnName(i)));
            }
            outputArea.append(header.toString() + "\n");
            outputArea.append("-".repeat(colCount * 20) + "\n");

            // Print rows
            int rowCount = 0;
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    String value = rs.getString(i);
                    row.append(String.format("%-20s", value != null ? value : "NULL"));
                }
                outputArea.append(row.toString() + "\n");
                rowCount++;
            }
            
            outputArea.append("\n✅ Query completed. " + rowCount + " row(s) returned.\n\n");

        } catch (SQLException ex) {
            outputArea.append("❌ SQL Error: " + ex.getMessage() + "\n");
            outputArea.append("   Error Code: " + ex.getErrorCode() + "\n");
            outputArea.append("   SQL State: " + ex.getSQLState() + "\n\n");
            
            JOptionPane.showMessageDialog(this,
                "SQL Error:\n" + ex.getMessage() + "\n\n" +
                "Error Code: " + ex.getErrorCode(),
                "Query Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Close DB Connection ---
    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                outputArea.append("🔒 Connection closed successfully.\n");
            }
        } catch (SQLException e) {
            outputArea.append("⚠️ Error closing connection: " + e.getMessage() + "\n");
        }
    }

    // --- Launch Application ---
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default if system look and feel fails
        }
        
        SwingUtilities.invokeLater(UI_Main::new);
    }
}