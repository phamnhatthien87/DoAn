package Security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Khong the ma hoa mat khau", e);
        }
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null || !isHashed(storedPassword)) {
            return false;
        }

        return MessageDigest.isEqual(
                hashPassword(password).getBytes(StandardCharsets.UTF_8),
                storedPassword.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static boolean isHashed(String storedPassword) {
        return storedPassword != null && storedPassword.matches("[0-9a-fA-F]{64}");
    }
}
