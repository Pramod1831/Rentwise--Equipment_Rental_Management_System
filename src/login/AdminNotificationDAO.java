package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminNotificationDAO {

    /**
     * Retrieves the count of all requests requiring admin action.
     * This includes:
     * 1. New Loan Requests (status = 'Pending')
     * 2. Return Requests (status = 'Return Pending')
     * @return The total number of requests the admin must act upon.
     */
    public static int getPendingActionCount() {
        // Query to count requests that need Admin attention
        String query =
                "SELECT COUNT(*) FROM request " +
                        "WHERE status IN ('Pending', 'Return Pending')";

        int count = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Error: Failed to fetch Admin action request count.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Connection Error: Could not get database connection.");
            e.printStackTrace();
        }

        return count;
    }
}