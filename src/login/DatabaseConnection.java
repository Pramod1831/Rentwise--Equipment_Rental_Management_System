package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ⭐ Static field to hold the single active connection for the session
    private static Connection databaseLink;

    public static Connection getConnection() throws SQLException {
        // NOTE: In a real app, you should use separate methods for getting a fresh
        // connection and setting/getting the active session connection.
        // For this login/logout flow, we'll connect and store in one go.

        String databaseName = "rentwise";
        String databaseUser = "user_name";
        String databasePassword = "user_password"; // Replace with your password in production!
        String url = "jdbc:mysql://localhost:3306/" + databaseName; // explicitly add port

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection newConnection = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("✅ Database connected successfully!");

            // ⭐ CRITICAL: Store the new connection as the active session connection
            databaseLink = newConnection;
            return databaseLink;

        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Failed to establish database connection.", e);
        }
    }

    /**
     * Retrieves the single active database connection for the session.
     */
    public static Connection getActiveConnection() {
        return databaseLink;
    }

    /**
     * Clears the static connection link after the connection has been closed
     * (Called during logout to destroy the session connection reference).
     */
    public static void clearActiveConnection() {
        databaseLink = null;
    }
}