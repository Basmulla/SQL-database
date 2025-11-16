import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * CPS510 Assignment 9 -  Oracle Connection Demo
 * Authors: Basmulla Atekulla, Michelle, Rochelle
 *
 * Allows any group member to log in with their TMU Oracle credentials
 * and connect to the TMU Oracle 11g database.
 */
public class JdbcOracleConnectionTemplate {

    public static void main(String[] args) {

        Connection conn = null;
        Properties props = new Properties();
        Scanner sc = new Scanner(System.in);
        Console console = System.console();

        String host = "oracle.scs.ryerson.ca";
        String port = "1521";
        String sid  = "orcl";
        String url  = "jdbc:oracle:thin:@"+host+":"+port+":"+sid;

        String username = "";
        String password = "";

        File credFile = new File(".db_credentials.properties");

        try {
            // ===================== LOAD SAVED CREDENTIALS =====================
            if (credFile.exists()) {
                FileInputStream fis = new FileInputStream(credFile);
                props.load(fis);
                fis.close();
                username = props.getProperty("username", "");
                password = props.getProperty("password", "");
                if (!username.isEmpty()) {
                    System.out.println("Found saved credentials for user: " + username);
                    System.out.print("Use saved credentials? (Y/N): ");
                    String choice = sc.nextLine().trim().toUpperCase();
                    if (!choice.equals("Y")) {
                        username = "";
                        password = "";
                    }
                }
            }

            // ===================== PROMPT USER IF NEEDED =====================
            if (username.isEmpty()) {
                System.out.print("Enter Oracle username: ");
                username = sc.nextLine().trim();

                if (console != null) {
                    char[] passArray = console.readPassword("Enter Oracle password: ");
                    password = new String(passArray);
                } else {
                    System.out.print("Enter Oracle password: ");
                    password = sc.nextLine().trim();
                }

                System.out.print("Save credentials for next time? (Y/N): ");
                String save = sc.nextLine().trim().toUpperCase();
                if (save.equals("Y")) {
                    props.setProperty("username", username);
                    props.setProperty("password", password);
                    FileOutputStream fos = new FileOutputStream(credFile);
                    props.store(fos, "Saved Oracle credentials");
                    fos.close();
                    System.out.println("Credentials saved locally (in .db_credentials.properties).");
                }
            }

            // ===================== CONNECT TO DATABASE =====================
            System.out.println("\nAttempting connection to: " + url);
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(url, username, password);

            if (conn != null) {
                System.out.println("? Connected successfully as user: " + username);
            }

            // ===================== RUN SAMPLE QUERY =====================
            String query = "SELECT NAME, NUM FROM TESTJDBC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                System.out.println("\n--- Results from TESTJDBC ---");
                while (rs.next()) {
                    String name = rs.getString("NAME");
                    int num = rs.getInt("NUM");
                    System.out.println(name + ", " + num);
                }
                System.out.println("------------------------------\n");
            } catch (SQLException e) {
                System.err.println("? SQL Error: " + e.getMessage());
            }

        } catch (ClassNotFoundException ex) {
            System.err.println("? JDBC Driver not found. Make sure ojdbc6.jar is in lib/");
            ex.printStackTrace();

        } catch (SQLException ex) {
            System.err.println("? Database connection failed:");
            System.err.println("Error Code: " + ex.getErrorCode());
            System.err.println("Message: " + ex.getMessage());
            System.err.println("Hint: Connect to TMU VPN if you are off-campus.");

        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());

        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("?? Connection closed successfully.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

	    // ===================== HELPER METHODS FOR UI =====================
    public static Connection connect(String username, String password) {
        Connection conn = null;
        String url = "jdbc:oracle:thin:@oracle.scs.ryerson.ca:1521:orcl";
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to Oracle DB as " + username);
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
        return conn;
    }

    public static void runSQLFile(Connection conn, String filePath) {
        if (conn == null) {
            System.err.println("❌ No connection to run SQL file.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new java.io.FileReader(filePath));
             Statement stmt = conn.createStatement()) {

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

            System.out.println("✅ Successfully executed: " + filePath);

        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ File Error: " + e.getMessage());
        }
    }

}
