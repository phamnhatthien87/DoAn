package DAO;

import Database.ConnectDB;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PublisherDAO {
    public Map<String, Integer> getAllPublishers() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT id, publisherName FROM Publishers";
        try (Connection conn = ConnectDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("publisherName"), rs.getInt("id"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}