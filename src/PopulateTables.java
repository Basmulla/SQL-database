import java.sql.Connection;

public class PopulateTables {
    public static void run(Connection conn, javax.swing.JTextArea output) {
        String path = "sql/03_populate_tables.sql";
        output.append(" Populating tables with sample data...\n");
        JdbcOracleConnectionTemplate.runSQLFile(conn, path);
        output.append(" Tables populated successfully.\n\n");
    }
}
