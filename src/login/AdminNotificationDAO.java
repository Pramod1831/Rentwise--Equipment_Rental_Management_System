package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminNotificationDAO {

    public static int getPendingActionCount() {
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