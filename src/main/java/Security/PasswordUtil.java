package Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Khong the ma hoa mat khau", e);
        }
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null || !isSha256Hash(storedPassword)) {
            return false;
        }

        return hashPassword(password).equalsIgnoreCase(storedPassword);
    }

    public static boolean isHashed(String storedPassword) {
        return isSha256Hash(storedPassword);
    }

    public static boolean isSha256Hash(String storedPassword) {
        return storedPassword != null && storedPassword.matches("[0-9a-fA-F]{64}");
    }
}
