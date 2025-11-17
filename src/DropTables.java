import java.sql.Connection;

public class DropTables {
    public static void run(Connection conn, javax.swing.JTextArea output) {
        String path = "sql/01_drop_tables.sql";
        output.append("Dropping existing tables...\n");
        JdbcOracleConnectionTemplate.runSQLFile(conn, path);
        output.append(" Tables dropped successfully.\n\n");
    }
}
