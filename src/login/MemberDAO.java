package login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public static List<MemberModel> getAllMembers() {
        List<MemberModel> members = new ArrayList<>();

        String query = "SELECT account_id, firstname, lastname, username FROM user_account";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("account_id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String username = rs.getString("username");

                members.add(new MemberModel(id, firstName, lastName, username));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database error fetching members: " + e.getMessage());
        }

        return members;
    }

    /**
     * Deletes a member from the database using their ID.
     */
    public static boolean deleteMember(String memberId) {
        String query = "DELETE FROM user_account WHERE account_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, memberId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database error deleting member " + memberId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates a member's information.
     */
    public static boolean updateMember(MemberModel member) {
        String query = "UPDATE user_account SET firstname = ?, lastname = ?, username = ? WHERE account_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, member.getFirstName());
            stmt.setString(2, member.getLastName());
            stmt.setString(3, member.getUsername());
            stmt.setString(4, member.getMemberId()); // Use the ID for the WHERE clause

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Database error updating member " + member.getMemberId() + ": " + e.getMessage());
            return false;
        }
    }
}