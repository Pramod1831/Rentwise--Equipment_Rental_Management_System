package login;

public class SessionManager {

    private static int loggedInUserId = -1;
    private static String loggedInUserRole = null;

    public static void setLoggedInUser(int accountId, String role) {
        loggedInUserId = accountId;
        loggedInUserRole = role;
        System.out.println("SessionManager: User ID " + accountId + " logged in with role " + role);
    }


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