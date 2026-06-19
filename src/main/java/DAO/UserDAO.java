package DAO;

import Database.ConnectDB;
import Security.PasswordUtil;
import model.Borrower;
import model.Librarian;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User login(String username, String password) {

        String sql = "SELECT id, username, password, role FROM Users WHERE username = ?";

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String dbUsername = rs.getString("username");
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                if (!isPasswordValid(password, storedPassword)) {
                    return null;
                }

                if (!PasswordUtil.isHashed(storedPassword)) {
                    updatePasswordHash(id, PasswordUtil.hashPassword(password));
                }

                if ("LIBRARIAN".equalsIgnoreCase(role)) {
                    return new Librarian(id, dbUsername, null, role);
                }

                return new Borrower(id, dbUsername, null, role);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean register(String username, String password) {

        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'borrower')";
        String hashedPassword = PasswordUtil.hashPassword(password);

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Loi dang ky: username co the da ton tai hoac cot password qua ngan.");
            return false;
        }
    }

    private boolean isPasswordValid(String rawPassword, String storedPassword) {

        if (PasswordUtil.isHashed(storedPassword)) {
            return PasswordUtil.verifyPassword(rawPassword, storedPassword);
        }

        return storedPassword != null && storedPassword.equals(rawPassword);
    }

    private void updatePasswordHash(int userId, String passwordHash) {

        String sql = "UPDATE Users SET password = ? WHERE id = ?";

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
