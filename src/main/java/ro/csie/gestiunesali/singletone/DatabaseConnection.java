package ro.csie.gestiunesali.singletone;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private Connection connection;
    private static final Dotenv DOTENV = Dotenv.load();

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = DOTENV.get("DB_URL");
            String user = DOTENV.get("DB_USER");
            String password = DOTENV.get("DB_PASSWORD");
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL nu a fost gasit.", e);
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = DOTENV.get("DB_URL");
            String user = DOTENV.get("DB_USER");
            String password = DOTENV.get("DB_PASSWORD");
            this.connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
