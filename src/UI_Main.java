import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * UI_Main.java
 * Java Swing Interface for Oracle DB Automation (CPS510 A9)
 */

public class UI_Main extends JFrame {
    private DBManager db;
    private JTextArea output;

    public UI_Main() {
        super("CPS510 Oracle Database UI");
        db = new DBManager();

        // Prompt for login
        String username = JOptionPane.showInputDialog("Enter Oracle Username:");
        String password = JOptionPane.showInputDialog("Enter Oracle Password:");

        if (!db.connect(username, password)) {
            JOptionPane.showMessageDialog(null, "Failed to connect to Oracle. Exiting.");
            System.exit(1);
        }

        // UI setup
        setLayout(new BorderLayout());
        output = new JTextArea(20, 60);
        output.setEditable(false);

        JPanel menu = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton dropBtn = new JButton("Drop Tables");
        JButton createBtn = new JButton("Create Tables");
        JButton populateBtn = new JButton("Populate Tables");
        JButton queryBtn = new JButton("Run Queries");
        JButton exitBtn = new JButton("Exit");

        menu.add(dropBtn);
        menu.add(createBtn);
        menu.add(populateBtn);
        menu.add(queryBtn);
        menu.add(exitBtn);

        add(menu, BorderLayout.WEST);
        add(new JScrollPane(output), BorderLayout.CENTER);

        // Button actions
        dropBtn.addActionListener(e -> runSQL("sql/drop_tables.sql"));
        createBtn.addActionListener(e -> runSQL("sql/create_tables.sql"));
        populateBtn.addActionListener(e -> runSQL("sql/populate_tables.sql"));
        queryBtn.addActionListener(e -> runQuery());
        exitBtn.addActionListener(e -> {
            db.close();
            System.exit(0);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void runSQL(String filePath) {
        db.executeSQLFile(filePath);
        output.append("Executed: " + filePath + "\n");
    }

    private void runQuery() {
        try {
            ResultSet rs = db.runQuery("SELECT * FROM Customer");
            output.setText("Customer Table:\n----------------------\n");
            while (rs.next()) {
                output.append(rs.getInt("CustomerID") + " | " +
                              rs.getString("Name") + " | " +
                              rs.getString("Email") + "\n");
            }
        } catch (Exception e) {
            output.append("Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UI_Main());
    }
}
