package dk.sdu.Core.db;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final Dotenv dotenv = Dotenv.load();

    // Husk at tilføje DB_URL, DB_USER og DB_PASSWORD i .env
    private static final String URL = dotenv.get("DB_URL", "Add to env");
    private static final String USER = dotenv.get("DB_USER", "Add to env");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "Add to env");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
