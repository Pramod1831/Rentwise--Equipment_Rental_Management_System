package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {

    private static final String INSERT_EQUIPMENT_SQL =
            "INSERT INTO equipments (name, quantity, issued, image_data) VALUES (?, ?, ?, ?)";

    private static Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public static String getRequestStatusById(int requestId) {
        String status = null;
        String query = "SELECT status FROM request WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("status");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching request status: " + e.getMessage());
        }
        return status;
    }

    public static boolean approveRequest(int requestId, int equipmentId, int requestedQuantity) {
        String currentStatus = getRequestStatusById(requestId);

        if ("Pending".equals(currentStatus)) {
            System.out.println("Processing Loan Approval for Request ID: " + requestId);
            return approveNewLoan(requestId, equipmentId, requestedQuantity);

        } else if ("Return Pending".equals(currentStatus)) {
            System.out.println("Processing Return Approval for Request ID: " + requestId);
            return acceptReturn(requestId, equipmentId, requestedQuantity);

        } else {
            System.err.println("Approval skipped: Request ID " + requestId + " has status: " + currentStatus);
            return false;
        }
    }

    private static boolean approveNewLoan(int requestId, int equipmentId, int requestedQuantity) {
        Connection conn = null;
        String updateRequestSql = "UPDATE request SET status = 'Approved', action_date = NOW() WHERE id = ?";
        String updateEquipmentSql = "UPDATE equipments SET issued = issued + ? WHERE id = ?";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement reqStmt = conn.prepareStatement(updateRequestSql)) {
                reqStmt.setInt(1, requestId);
                reqStmt.executeUpdate();
            }

            try (PreparedStatement equipStmt = conn.prepareStatement(updateEquipmentSql)) {
                equipStmt.setInt(1, requestedQuantity);
                equipStmt.setInt(2, equipmentId);
                equipStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed while approving new loan request " + requestId + ": " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back.");
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }


    public static boolean acceptReturn(int requestId, int equipmentId, int quantity) {
        Connection conn = null;

        String updateRequestSql = "UPDATE request SET status = 'Returned', action_date = NOW() WHERE id = ?";
        String updateEquipmentSql = "UPDATE equipments SET issued = issued - ? WHERE id = ?";

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement updateRequestStmt = conn.prepareStatement(updateRequestSql)) {
                updateRequestStmt.setInt(1, requestId);
                updateRequestStmt.executeUpdate();
            }

            try (PreparedStatement updateEquipmentStmt = conn.prepareStatement(updateEquipmentSql)) {
                updateEquipmentStmt.setInt(1, quantity);
                updateEquipmentStmt.setInt(2, equipmentId);
                updateEquipmentStmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Return accepted and processed: Request " + requestId + " completed.");
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed while processing return for request " + requestId + ": " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back.");
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    // conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public static void addEquipment(String name, int quantity, int i, byte[] imageData) throws SQLException {
        int issued = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_EQUIPMENT_SQL)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, issued);

            if (imageData != null) {
                pstmt.setBytes(4, imageData);
            } else {
                pstmt.setNull(4, java.sql.Types.BLOB);
            }

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error adding equipment to database: " + e.getMessage());
            throw e;
        }
    }

    public static List<EquipmentModel> getAllEquipments() {
        List<EquipmentModel> equipments = new ArrayList<>();

        String query = "SELECT id, name, quantity, issued, image_data FROM equipments";

        try (Connection connectDB = getConnection();
             Statement statement = connectDB.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                byte[] imageData = rs.getBytes("image_data");
                int quantity = rs.getInt("quantity");
                int issued = rs.getInt("issued");

                int remaining = quantity - issued;

                equipments.add(new EquipmentModel(
                        rs.getInt("id"),
                        rs.getString("name"),
                        quantity,
                        issued,
                        remaining,
                        imageData
                ));
            }

        } catch (Exception e) {
            System.err.println("Error fetching all equipments: " + e.getMessage());
            e.printStackTrace();
        }

        return equipments;
    }

    public static boolean deleteEquipment(int equipmentId) {
        String query = "DELETE FROM equipments WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, equipmentId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error deleting equipment " + equipmentId + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean updateEquipmentQuantity(int equipmentId, int newQuantity) {
        // Step 1: Check the currently issued quantity to prevent quantity < issued
        int issuedQuantity = 0;
        String checkQuery = "SELECT issued FROM equipments WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, equipmentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                issuedQuantity = rs.getInt("issued");
            } else {
                System.err.println("Equipment ID not found for update check: " + equipmentId);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Database error during update check: " + e.getMessage());
            return false;
        }

        if (newQuantity < issuedQuantity) {
            System.err.println("Update failed: New quantity (" + newQuantity + ") cannot be less than issued quantity (" + issuedQuantity + ").");
            return false;
        }


        String updateQuery = "UPDATE equipments SET quantity = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            updateStmt.setInt(1, newQuantity);
            updateStmt.setInt(2, equipmentId);

            int rowsAffected = updateStmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error updating equipment quantity: " + e.getMessage());
            return false;
        }
    }


    public static int getPendingRequestCount() {
        System.out.println("Note: getPendingRequestCount is deprecated. Use getRequestsByStatus().size()");
        return 0;
    }

    public static EquipmentModel getEquipmentById(int equipmentId) {
        String query = "SELECT id, name, quantity, issued, image_data FROM equipments WHERE id = ?";

        try (Connection connectDB = getConnection();
             PreparedStatement pstmt = connectDB.prepareStatement(query)) {

            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] imageData = rs.getBytes("image_data");
                    int quantity = rs.getInt("quantity");
                    int issued = rs.getInt("issued");
                    int remaining = quantity - issued;

                    return new EquipmentModel(
                            rs.getInt("id"),
                            rs.getString("name"),
                            quantity,
                            issued,
                            remaining,
                            imageData
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching equipment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<RequestModel> getPendingRequests() {
        List<RequestModel> requests = new ArrayList<>();
        String query = "SELECT id, user_id, equipment_id, quantity_requested, status, request_date, action_date FROM request WHERE status = 'Pending'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                requests.add(new RequestModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("quantity_requested"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getTimestamp("action_date")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending requests: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public static String getEquipmentNameById(int equipmentId) {
        String name = "Unknown Equipment";
        String query = "SELECT name FROM equipments WHERE id = ?";

        try (Connection connectDB = getConnection();
             PreparedStatement pstmt = connectDB.prepareStatement(query)) {

            pstmt.setInt(1, equipmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching equipment name by ID: " + e.getMessage());
        }
        return name;
    }

    public static String getUserNameById(int userId) {
        String name = "Unknown User";
        String query = "SELECT username FROM user_account WHERE account_id = ?";

        try (Connection connectDB = getConnection();
             PreparedStatement pstmt = connectDB.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user name by ID: " + e.getMessage());
        }
        return name;
    }

    public static boolean rejectRequest(int requestId) {
        String updateRequestSql = "UPDATE request SET status = 'Rejected', action_date = NOW() WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateRequestSql)) {

            pstmt.setInt(1, requestId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error rejecting request " + requestId + ": " + e.getMessage());
            return false;
        }
    }

    public static boolean createNewRequest(int userId, int equipmentId, int quantity) {
        String sql = "INSERT INTO request (user_id, equipment_id, quantity_requested, status, request_date) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, equipmentId);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, "Pending"); // Default status for a new request

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error creating new equipment request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<RequestModel> getUserRequests(int userId) {
        List<RequestModel> requests = new ArrayList<>();
        String query = "SELECT id, user_id, equipment_id, quantity_requested, status, request_date, action_date " +
                "FROM request WHERE user_id = ? ORDER BY request_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(new RequestModel(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("equipment_id"),
                            rs.getInt("quantity_requested"),
                            rs.getString("status"),
                            rs.getTimestamp("request_date"),
                            rs.getTimestamp("action_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user requests: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public static boolean requestReturn(int requestId) {
        String sql = "UPDATE request SET status = 'Return Pending' WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, requestId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error requesting return for ID " + requestId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<RequestModel> getRequestsByStatus(String status) {
        List<RequestModel> requests = new ArrayList<>();
        String query = "SELECT id, user_id, equipment_id, quantity_requested, status, request_date, action_date FROM request WHERE status = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(new RequestModel(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("equipment_id"),
                            rs.getInt("quantity_requested"),
                            rs.getString("status"),
                            rs.getTimestamp("request_date"),
                            rs.getTimestamp("action_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching requests by status: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }

    public static List<RequestModel> getAdminActionRequests() {
        List<RequestModel> requests = new ArrayList<>();
        String query = "SELECT id, user_id, equipment_id, quantity_requested, status, request_date, action_date FROM request WHERE status IN ('Pending', 'Return Pending') ORDER BY request_date ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                requests.add(new RequestModel(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("quantity_requested"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date"),
                        rs.getTimestamp("action_date")));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching Admin action requests: " + e.getMessage());
            e.printStackTrace();
        }
        return requests;
    }
}