import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UI_Main.java - Enhanced E-Commerce Database UI with Better Display
 * Authors: Basmulla Atekulla, Rochelle, Michelle
 * Enhanced: Improved formatting, tables, colors, and visual appeal
 * Fixed: Table headers now properly visible
 */
public class UI_Main extends JFrame {

    private Connection conn;
    private JTextArea outputArea;
    private JTabbedPane tabbedPane;
    private JPanel tablePanel;
    private Color primaryColor = new Color(102, 126, 234);
    private Color accentColor = new Color(118, 75, 162);

    public UI_Main() {
        super("CPS510 E-Commerce Database - Enhanced UI");

        setLayout(new BorderLayout(10, 10));
        
        // Add padding to main frame
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP PANEL: Title and Connection Status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(primaryColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("🏪 E-Commerce Database Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);

        // --- LEFT PANEL: Buttons with Categories
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.setBackground(new Color(245, 245, 245));

        // Database Operations Section
        addSectionLabel(leftPanel, "📊 Database Operations");
        JButton btnDrop = createStyledButton("Drop Tables", new Color(231, 76, 60));
        JButton btnCreate = createStyledButton("Create Tables", new Color(46, 204, 113));
        JButton btnPopulate = createStyledButton("Populate Tables", new Color(52, 152, 219));
        
        leftPanel.add(btnDrop);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnCreate);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnPopulate);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // View Data Section
        addSectionLabel(leftPanel, "👁️ View Data");
        JButton btnViewProducts = createStyledButton("View Products", new Color(155, 89, 182));
        JButton btnViewCustomers = createStyledButton("View Customers", new Color(155, 89, 182));
        JButton btnViewOrders = createStyledButton("View Orders", new Color(155, 89, 182));
        
        leftPanel.add(btnViewProducts);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnViewCustomers);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnViewOrders);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Analytics Section
        addSectionLabel(leftPanel, "📈 Analytics & Reports");
        JButton btnRFM = createStyledButton("Customer RFM", new Color(230, 126, 34));
        JButton btnRevenue = createStyledButton("Revenue by Brand", new Color(230, 126, 34));
        JButton btnLowStock = createStyledButton("Low Stock Alert", new Color(192, 57, 43));
        JButton btnShipping = createStyledButton("Shipping Status", new Color(230, 126, 34));
        
        leftPanel.add(btnRFM);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnRevenue);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnLowStock);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnShipping);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // System Section
        addSectionLabel(leftPanel, "⚙️ System");
        JButton btnClear = createStyledButton("Clear Output", new Color(149, 165, 166));
        JButton btnExit = createStyledButton("Exit", new Color(52, 73, 94));
        
        leftPanel.add(btnClear);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(btnExit);
        
        JScrollPane leftScrollPane = new JScrollPane(leftPanel);
        leftScrollPane.setPreferredSize(new Dimension(220, 0));
        leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(leftScrollPane, BorderLayout.WEST);

        // --- CENTER PANEL: Tabbed View
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Text Output Tab
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        tabbedPane.addTab("📝 Console Output", outputScrollPane);
        
        // Table View Tab
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        JLabel placeholderLabel = new JLabel("Select a view option to display data in table format", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        placeholderLabel.setForeground(Color.GRAY);
        tablePanel.add(placeholderLabel, BorderLayout.CENTER);
        tabbedPane.addTab("📊 Table View", tablePanel);
        
        add(tabbedPane, BorderLayout.CENTER);

        // --- LOGIN AND CONNECTION
        DBManager db = new DBManager();
        String username = null;
        String password = null;
        boolean validCredentials = false;

        for (int attempt = 1; attempt <= 3; attempt++) {
            username = JOptionPane.showInputDialog(this, 
                "Enter Oracle username" + (attempt > 1 ? " (Attempt " + attempt + "/3)" : "") + ":",
                "Database Login",
                JOptionPane.QUESTION_MESSAGE);

            if (username == null) {
                System.exit(0);
            }

            username = username.trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Username cannot be empty. Please try again.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }

            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(this, pf, 
                "Enter Oracle password for user: " + username, 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);

            if (okCxl != JOptionPane.OK_OPTION) {
                System.exit(0);
            }

            password = new String(pf.getPassword()).trim();
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Password cannot be empty. Please try again.",
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                continue;
            }

            validCredentials = true;
            break;
        }

        if (!validCredentials) {
            System.exit(0);
        }

        // Update title with connection status
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        
        if (db.connect(username, password)) {
            conn = db.getConnection();
            statusLabel.setText("✅ Connected as: " + username + " | oracle.scs.ryerson.ca:1521");
            appendToOutput("╔════════════════════════════════════════════════════════════╗\n");
            appendToOutput("║          CONNECTION ESTABLISHED SUCCESSFULLY              ║\n");
            appendToOutput("╚════════════════════════════════════════════════════════════╝\n");
            appendToOutput("User: " + username + "\n");
            appendToOutput("Host: oracle.scs.ryerson.ca:1521\n");
            appendToOutput("Database: orcl\n\n");
        } else {
            statusLabel.setText("❌ Connection Failed");
            statusLabel.setForeground(new Color(231, 76, 60));
            appendToOutput("❌ Failed to connect to Oracle DB.\n");
            appendToOutput("Please check credentials and VPN connection.\n\n");
        }
        
        topPanel.add(statusLabel, BorderLayout.EAST);

        // --- BUTTON ACTIONS
        btnDrop.addActionListener(e -> runSQL("sql/01_drop_tables.sql"));
        btnCreate.addActionListener(e -> runSQL("sql/02_create_tables.sql"));
        btnPopulate.addActionListener(e -> runSQL("sql/03_populate_tables.sql"));
        
        btnViewProducts.addActionListener(e -> displayQueryInTable("Products", 
            "SELECT ProductID, Name, Brand, Price, StockQuantity, IsActive FROM Product ORDER BY ProductID"));
        
        btnViewCustomers.addActionListener(e -> displayQueryInTable("Customers",
            "SELECT CustomerID, Name, Email, Phone, Address FROM Customer ORDER BY CustomerID"));
        
        btnViewOrders.addActionListener(e -> displayQueryInTable("Orders",
            "SELECT o.OrderID, c.Name AS Customer, o.OrderDate, o.Status, o.OrderTotal, s.Name AS Staff " +
            "FROM Orders o JOIN Customer c ON o.CustomerID = c.CustomerID " +
            "LEFT JOIN Staff s ON o.StaffID = s.StaffID ORDER BY o.OrderDate DESC"));
        
        btnRFM.addActionListener(e -> displayQueryInTable("Customer RFM Analysis",
            "SELECT c.CustomerID, c.Name, MAX(o.OrderDate) AS LastPurchase, " +
            "ROUND(SYSDATE - MAX(o.OrderDate)) AS RecencyDays, COUNT(o.OrderID) AS Frequency, " +
            "NVL(SUM(o.OrderTotal), 0) AS MonetaryValue FROM Customer c " +
            "LEFT JOIN Orders o ON c.CustomerID = o.CustomerID GROUP BY c.CustomerID, c.Name " +
            "ORDER BY MonetaryValue DESC"));
        
        btnRevenue.addActionListener(e -> displayQueryInTable("Revenue by Brand",
            "SELECT p.Brand, COUNT(DISTINCT p.ProductID) AS Products, SUM(od.Quantity) AS UnitsSold, " +
            "SUM(od.Quantity * od.PurchasePrice) AS Revenue FROM Product p " +
            "JOIN OrderDetails od ON p.ProductID = od.ProductID GROUP BY p.Brand ORDER BY Revenue DESC"));
        
        btnLowStock.addActionListener(e -> displayQueryInTable("Low Stock Alert",
            "SELECT ProductID, Name, Brand, StockQuantity, Price, " +
            "CASE WHEN StockQuantity = 0 THEN 'OUT OF STOCK' " +
            "WHEN StockQuantity <= 5 THEN 'LOW STOCK' " +
            "WHEN StockQuantity <= 15 THEN 'MODERATE' ELSE 'SUFFICIENT' END AS Status " +
            "FROM Product WHERE IsActive = 'Y' ORDER BY StockQuantity ASC"));
        
        btnShipping.addActionListener(e -> displayQueryInTable("Shipping Status",
            "SELECT s.ShippingID, s.OrderID, c.Name AS Customer, s.Courier, s.TrackingNum, " +
            "s.Status, ROUND(SYSDATE - o.OrderDate) AS DaysAgo FROM Shipping s " +
            "JOIN Orders o ON s.OrderID = o.OrderID JOIN Customer c ON o.CustomerID = c.CustomerID " +
            "ORDER BY o.OrderDate DESC"));
        
        btnClear.addActionListener(e -> {
            outputArea.setText("");
            tablePanel.removeAll();
            JLabel placeholder = new JLabel("Output cleared. Select a view option.", SwingConstants.CENTER);
            placeholder.setFont(new Font("Arial", Font.ITALIC, 14));
            placeholder.setForeground(Color.GRAY);
            tablePanel.add(placeholder, BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
        });

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

        // --- FRAME SETTINGS
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- HELPER METHOD: Add Section Label
    private void addSectionLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(new Color(52, 73, 94));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    // --- HELPER METHOD: Create Styled Button
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    // --- HELPER METHOD: Append to Output
    private void appendToOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    // --- RUN SQL SCRIPT
    private void runSQL(String path) {
        if (conn == null) {
            appendToOutput("⚠️ No active connection.\n\n");
            return;
        }

        appendToOutput("\n▶ Running: " + path + "\n");
        appendToOutput("─".repeat(60) + "\n");
        
        try {
            JdbcOracleConnectionTemplate.runSQLFile(conn, path);
            appendToOutput("✅ Successfully completed: " + path + "\n");
            appendToOutput("─".repeat(60) + "\n\n");
        } catch (Exception ex) {
            appendToOutput("❌ Error: " + ex.getMessage() + "\n\n");
        }
    }

    // --- DISPLAY QUERY IN TABLE FORMAT (FIXED HEADERS)
    private void displayQueryInTable(String title, String query) {
        if (conn == null) {
            appendToOutput("⚠️ No active connection.\n\n");
            return;
        }

        appendToOutput("\n▶ Executing: " + title + "\n");
        appendToOutput("─".repeat(60) + "\n");

        try (Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();

            // Get column names
            String[] columnNames = new String[colCount];
            for (int i = 0; i < colCount; i++) {
                columnNames[i] = md.getColumnName(i + 1);
            }

            // Get data
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            int rowCount = 0;
            
            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 0; i < colCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
                rowCount++;
            }

            // Create table with styling
            JTable table = new JTable(model);
            table.setFont(new Font("Arial", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.setGridColor(new Color(230, 230, 230));
            table.setSelectionBackground(new Color(232, 240, 254));
            table.setSelectionForeground(Color.BLACK);
            table.setShowGrid(true);
            table.setIntercellSpacing(new Dimension(1, 1));
            
            // *** FIXED: Make header visible with proper styling ***
            JTableHeader header = table.getTableHeader();
            header.setOpaque(true);  // Make sure it's opaque
            header.setBackground(primaryColor);
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Arial", Font.BOLD, 13));
            header.setPreferredSize(new Dimension(0, 40));  // Fixed height
            header.setBorder(BorderFactory.createLineBorder(primaryColor, 2));
            header.setReorderingAllowed(false);  // Prevent column reordering
            
            // Make header text more visible
            DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
            headerRenderer.setHorizontalAlignment(JLabel.CENTER);
            headerRenderer.setBackground(primaryColor);
            headerRenderer.setForeground(Color.WHITE);
            headerRenderer.setFont(new Font("Arial", Font.BOLD, 13));
            
            // Center align numeric columns and apply formatting
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
            
            for (int i = 0; i < colCount; i++) {
                String colName = columnNames[i].toUpperCase();
                if (colName.contains("ID") || colName.contains("QUANTITY") || 
                    colName.contains("DAYS") || colName.contains("COUNT") || colName.contains("FREQUENCY")) {
                    table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                } else if (colName.contains("PRICE") || colName.contains("TOTAL") || 
                           colName.contains("REVENUE") || colName.contains("MONETARY")) {
                    table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
                }
            }

            // Create scroll pane with visible header
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            
            // Update table panel
            tablePanel.removeAll();
            
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(primaryColor);
            
            JLabel countLabel = new JLabel(rowCount + " row(s)");
            countLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            countLabel.setForeground(Color.GRAY);
            
            headerPanel.add(titleLabel, BorderLayout.WEST);
            headerPanel.add(countLabel, BorderLayout.EAST);
            
            tablePanel.add(headerPanel, BorderLayout.NORTH);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
            
            // Switch to table tab
            tabbedPane.setSelectedIndex(1);

            appendToOutput("✅ Query completed. " + rowCount + " row(s) displayed in Table View.\n");
            appendToOutput("─".repeat(60) + "\n\n");

        } catch (SQLException ex) {
            appendToOutput("❌ SQL Error: " + ex.getMessage() + "\n");
            appendToOutput("   Error Code: " + ex.getErrorCode() + "\n\n");
        }
    }

    // --- CLOSE CONNECTION
    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                appendToOutput("🔒 Connection closed successfully.\n");
            }
        } catch (SQLException e) {
            appendToOutput("⚠️ Error closing connection: " + e.getMessage() + "\n");
        }
    }

    // --- MAIN
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        SwingUtilities.invokeLater(UI_Main::new);
    }
}