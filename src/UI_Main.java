
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UI_Main.java E-Commerce Database Swing Interface for Oracle 11g Authors:
 * Basmulla Atekulla, Rochelle, Michelle
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

        /*Advanced Report buttons */
        JButton btnRFM = new JButton("Customer RFM Snapshot");
        JButton btnOrderHealth = new JButton("Order Health Check");
        JButton btnRevenue = new JButton("Revenue by Category");
        JButton btnLowStock = new JButton("Low Stock Alert");
        JButton btnSLA = new JButton("Shipping SLA Aging");

        buttonPanel.add(btnDrop);
        buttonPanel.add(btnCreate);
        buttonPanel.add(btnPopulate);
        buttonPanel.add(btnViewProducts);
        buttonPanel.add(btnAnalytics);
        // --- Advanced Report Buttons
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
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        DBManager db = new DBManager();

        // Ask user for credentials
        String username = JOptionPane.showInputDialog(this, "Enter Oracle username:");
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(this, pf, "Enter Oracle password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String password = "";
        if (okCxl == JOptionPane.OK_OPTION) {
            password = new String(pf.getPassword());
        }

        // Try to connect
        if (db.connect(username, password)) {
            conn = db.getConnection();
            outputArea.append("✅ Connected to Oracle DB as " + username + "\n\n");
        } else {
            outputArea.append("❌ Failed to connect to Oracle DB.\nCheck username/password.\n\n");
        }

        // --- Button Actions
        btnDrop.addActionListener(e -> runSQL("sql/01_drop_tables.sql"));
        btnCreate.addActionListener(e -> runSQL("sql/02_create_tables.sql"));
        btnPopulate.addActionListener(e -> runSQL("sql/03_populate_tables.sql"));
        btnViewProducts.addActionListener(e -> displayQuery("SELECT * FROM Product"));
        btnAnalytics.addActionListener(e
                -> displayQuery("SELECT Category, COUNT(*) AS TotalProducts FROM Product GROUP BY Category"));
        btnExit.addActionListener(e -> {
            closeConnection();
            System.exit(0);
        });

        // --- Frame Settings
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // --- Run SQL Script
    private void runSQL(String path) {
        if (conn == null) {
            outputArea.append("⚠️ No active connection.\n");
            return;
        }

        outputArea.append("Running " + path + "...\n");
        try {
            JdbcOracleConnectionTemplate.runSQLFile(conn, path);
            outputArea.append("✅ Completed: " + path + "\n\n");
        } catch (Exception ex) {
            outputArea.append("❌ Error executing " + path + ": " + ex.getMessage() + "\n\n");
        }
    }

    // --- Execute SELECT Query
    private void displayQuery(String query) {
        if (conn == null) {
            outputArea.append("⚠️ No active connection.\n");
            return;
        }

        outputArea.append("Executing query: " + query + "\n");

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            // Print column headers
            for (int i = 1; i <= colCount; i++) {
                outputArea.append(md.getColumnName(i) + "\t");
            }
            outputArea.append("\n----------------------------------------\n");

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    outputArea.append(rs.getString(i) + "\t");
                }
                outputArea.append("\n");
            }
            outputArea.append("\n✅ Query completed.\n\n");

        } catch (SQLException ex) {
            outputArea.append("❌ SQL Error: " + ex.getMessage() + "\n\n");
        }
    }

    // --- Close DB Connection
    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                outputArea.append("🔒 Connection closed.\n");
            }
        } catch (SQLException e) {
            outputArea.append("Error closing connection: " + e.getMessage() + "\n");
        }
    }

    // --- Launch Application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UI_Main::new);
    }
}
