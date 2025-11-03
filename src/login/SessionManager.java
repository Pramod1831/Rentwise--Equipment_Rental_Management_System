package login;

/**
 * Manages the static session state for the currently logged-in user.
 * This should be cleared upon logout.
 */
public class SessionManager {

    private static int loggedInUserId = -1;
    private static String loggedInUserRole = null;

    /**
     * Sets the session data upon successful login.
     * @param accountId The unique ID of the user.
     * @param role The role (e.g., "admin", "user").
     */
    public static void setLoggedInUser(int accountId, String role) {
        loggedInUserId = accountId;
        loggedInUserRole = role;
        System.out.println("SessionManager: User ID " + accountId + " logged in with role " + role);
    }

    /**
     * Clears all session data upon logout.
     */
    public static void clearSession() {
        if (isLoggedIn()) {
            System.out.println("SessionManager: Clearing session for user ID " + loggedInUserId);
        }
        loggedInUserId = -1;
        loggedInUserRole = null;
    }

    public static boolean isLoggedIn() {
        return loggedInUserId != -1;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static String getLoggedInUserRole() {
        return loggedInUserRole;
    }
}