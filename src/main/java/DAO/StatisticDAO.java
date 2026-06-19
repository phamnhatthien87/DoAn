package DAO;

import Database.ConnectDB;
import java.sql.*;
import java.time.LocalDate;

public class StatisticDAO {

    public int byDate(LocalDate d) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE borrowDate=?";
        try (Connection c = ConnectDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(d));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
    public int byMonth(int m, int y) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE MONTH(borrowDate)=? AND YEAR(borrowDate)=?";
        try (Connection c = ConnectDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m);
            ps.setInt(2, y);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
}
