package DAO;

import Database.ConnectDB;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO {

    public Map<String, Integer> getAllToMap() {

        Map<String, Integer> map = new HashMap<>();

        String sql = "SELECT id, categoryName FROM Categories";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("categoryName");
                int id = rs.getInt("id");

                map.put(name, id);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi load Category!");
            e.printStackTrace();
        }

        return map;
    }
}