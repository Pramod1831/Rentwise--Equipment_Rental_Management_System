package login;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordHasher {

    public static String hashPassword(String plainPassword) {
        // BCrypt.gensalt() generates a random salt and includes the default cost factor (10).
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // BCrypt.checkpw securely hashes the plainPassword and compares it to the storedHash.
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}