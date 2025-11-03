package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationDAO {

    /**
     * Retrieves the count of *unacknowledged* requests for the user.
     * An unacknowledged request is one where:
     * 1. The status is not 'Pending' (meaning an admin acted on it).
     * 2. The user has not yet acknowledged the status update (is_acknowledged = FALSE).
     * @param userId The ID of the user whose requests are being checked.
     * @return The number of unacknowledged request updates.
     */
    public static int getUnseenNotificationCount(int userId) {
        String query =
                "SELECT COUNT(*) FROM request " +
                        "WHERE user_id = ? " +
                        "AND status != 'Pending' " +
                        "AND is_acknowledged = FALSE";

        int count = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Error: Failed to fetch unacknowledged request count for user " + userId);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Connection Error: Could not get database connection.");
            e.printStackTrace();
        }

        return count;
    }


    /**
     * Utility method to mark a request status change as seen by the user.
     * This is called when the user clicks the Notification menu item.
     */
    public static void acknowledgeRequestStatus(int userId) {
        String updateQuery =
                "UPDATE request SET is_acknowledged = TRUE " +
                        "WHERE user_id = ? AND status != 'Pending' AND is_acknowledged = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setInt(1, userId);
            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("✅ Acknowledged " + rowsUpdated + " request status updates for user " + userId);

        } catch (SQLException e) {
            System.err.println("❌ Database Error: Failed to acknowledge requests for user " + userId);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Connection Error: Could not get database connection for acknowledgment.");
        }
    }
}