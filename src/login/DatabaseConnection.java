package login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection databaseLink;

    public static Connection getConnection() throws SQLException {


        String databaseName = "rentwise";
        String databaseUser = "username";
        String databasePassword = "User_password";
        String url = "jdbc:mysql://localhost:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection newConnection = DriverManager.getConnection(url, databaseUser, databasePassword);
            System.out.println("Database connected successfully!");

            databaseLink = newConnection;
            return databaseLink;

        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Failed to establish database connection.", e);
        }
    }


    public static Connection getActiveConnection() {
        return databaseLink;
    }

    public static void clearActiveConnection() {
        databaseLink = null;
    }
}