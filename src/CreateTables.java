import java.sql.Connection;

public class CreateTables {
    public static void run(Connection conn, javax.swing.JTextArea output) {
        String path = "sql/02_create_tables.sql";
        output.append("🏗️ Creating database tables...\n");
        JdbcOracleConnectionTemplate.runSQLFile(conn, path);
        output.append("✅ Tables created successfully.\n\n");
    }
}
