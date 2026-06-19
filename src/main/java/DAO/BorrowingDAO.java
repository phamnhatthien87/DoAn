package DAO;

import Database.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {

    /**
     * Lấy lịch sử mượn trả
     */
    public List<String[]> getHistory() {

        List<String[]> list = new ArrayList<>();

        String sql =
                "SELECT * " +
                        "FROM View_BorrowingDetails " +
                        "ORDER BY BorrowDate DESC";

        try (
                Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(new String[]{

                        rs.getString("BorrowID"),
                        rs.getString("BorrowerName"),
                        rs.getString("BookTitle"),
                        rs.getString("BorrowDate"),
                        rs.getString("Status"),
                        rs.getString("BookID")
                });
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }

    /**
     * TRẢ SÁCH
     */
    public boolean returnBookTransaction(int borrowId, int bookId) {

        String updateBorrow =
                "UPDATE Borrowings " +
                        "SET status = 'returned', " +
                        "returnDate = GETDATE() " +
                        "WHERE id = ?";

        String updateBook =
                "UPDATE Books " +
                        "SET quantity = quantity + 1, " +
                        "loaned_out = CASE " +
                        "WHEN loaned_out > 0 THEN loaned_out - 1 " +
                        "ELSE 0 END " +
                        "WHERE id = ?";

        try (Connection con = ConnectDB.getConnection()) {

            con.setAutoCommit(false);

            System.out.println("===== DATABASE DEBUG =====");
            System.out.println("Connection = " + con.getCatalog());

            try (
                    PreparedStatement ps1 =
                            con.prepareStatement(updateBorrow);

                    PreparedStatement ps2 =
                            con.prepareStatement(updateBook)
            ) {

                System.out.println("borrowId = " + borrowId);
                System.out.println("bookId = " + bookId);

                ps1.setInt(1, borrowId);
                ps2.setInt(1, bookId);

                String checkSql =
                        "SELECT * FROM Books WHERE id = ?";

                try (
                        PreparedStatement checkPs =
                                con.prepareStatement(checkSql)
                ) {

                    checkPs.setInt(1, bookId);

                    ResultSet rs = checkPs.executeQuery();

                    if (rs.next()) {

                        System.out.println("===== BOOK FOUND =====");

                        System.out.println(
                                "Book ID = " + rs.getInt("id"));

                        System.out.println(
                                "Title = " + rs.getString("title"));

                        System.out.println(
                                "Quantity = " + rs.getInt("quantity"));

                        System.out.println(
                                "Loaned Out = " + rs.getInt("loaned_out"));

                    } else {

                        System.out.println(
                                "===== BOOK NOT FOUND =====");
                    }
                }

                int r1 = ps1.executeUpdate();
                int r2 = ps2.executeUpdate();

                System.out.println("Update Borrowings = " + r1);
                System.out.println("Update Books = " + r2);

                if (r1 > 0 && r2 > 0) {

                    con.commit();

                    System.out.println(
                            "===== RETURN SUCCESS =====");

                    return true;

                } else {

                    con.rollback();

                    System.out.println(
                            "===== ROLLBACK =====");
                }

            } catch (Exception e) {

                con.rollback();

                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * MƯỢN SÁCH
     */
    public boolean borrowBookTransaction(int userId, int bookId) {

        String updateBook =
                "UPDATE Books " +
                        "SET quantity = quantity - 1, " +
                        "loaned_out = loaned_out + 1 " +
                        "WHERE id = ? AND quantity > 0";

        String insertBorrow =
                "INSERT INTO Borrowings " +
                        "(userId, bookId, borrowDate, status) " +
                        "VALUES (?, ?, GETDATE(), 'borrowing')";

        try (Connection con = ConnectDB.getConnection()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement ps1 =
                            con.prepareStatement(updateBook);

                    PreparedStatement ps2 =
                            con.prepareStatement(insertBorrow)
            ) {

                ps1.setInt(1, bookId);

                int r1 = ps1.executeUpdate();

                ps2.setInt(1, userId);
                ps2.setInt(2, bookId);

                int r2 = ps2.executeUpdate();

                System.out.println("Update Books = " + r1);
                System.out.println("Insert Borrowings = " + r2);

                if (r1 > 0 && r2 > 0) {

                    con.commit();

                    System.out.println(
                            "===== BORROW SUCCESS =====");

                    return true;

                } else {

                    con.rollback();

                    System.out.println(
                            "===== BORROW ROLLBACK =====");
                }

            } catch (Exception e) {

                con.rollback();

                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * KIỂM TRA SÁCH ĐANG ĐƯỢC MƯỢN
     */
    public boolean isBookBorrowed(int bookId) {

        String sql =
                "SELECT COUNT(*) " +
                        "FROM Borrowings " +
                        "WHERE bookId = ? " +
                        "AND status = 'borrowing'";

        try (
                Connection con = ConnectDB.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}
