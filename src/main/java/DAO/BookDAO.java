package DAO;

import Database.ConnectDB;

import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class BookDAO {


    public List<Book> getAll() {

        return getAllBooks();
    }


    public List<Book> getAllBooks() {

        List<Book> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "b.id, " +
                        "b.title, " +
                        "b.author, " +
                        "b.quantity, " +
                        "c.categoryName, " +
                        "p.publisherName " +
                        "FROM Books b " +
                        "LEFT JOIN Categories c " +
                        "ON b.categoryId = c.id " +
                        "LEFT JOIN Publishers p " +
                        "ON b.publisherId = p.id";

        try (

                Connection con =
                        ConnectDB.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()

        ) {

            while (rs.next()) {

                Book b = new Book(

                        rs.getInt("id"),

                        rs.getString("title"),

                        rs.getString("author"),

                        rs.getInt("quantity"),

                        rs.getString("categoryName"),

                        rs.getString("publisherName")
                );

                list.add(b);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }


    public boolean insert(Book b) {

        String sql =
                "INSERT INTO Books " +
                        "(title, author, quantity, categoryId, publisherId) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (

                Connection con =
                        ConnectDB.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)

        ) {

            ps.setString(1, b.getTitle());

            ps.setString(2, b.getAuthor());

            ps.setInt(3, b.getQuantity());

            ps.setInt(4, b.getCategoryId());

            ps.setInt(5, b.getPublisherId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    public boolean delete(int id) {

        String sql =
                "DELETE FROM Books WHERE id = ?";

        try (

                Connection con =
                        ConnectDB.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)

        ) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }


    public List<Book> searchBooks(String keyword) {

        List<Book> list = new ArrayList<>();

        String sql =
                "SELECT " +
                        "b.id, " +
                        "b.title, " +
                        "b.author, " +
                        "b.quantity, " +
                        "c.categoryName, " +
                        "p.publisherName " +
                        "FROM Books b " +
                        "LEFT JOIN Categories c " +
                        "ON b.categoryId = c.id " +
                        "LEFT JOIN Publishers p " +
                        "ON b.publisherId = p.id " +
                        "WHERE " +
                        "LOWER(b.title) LIKE ? " +
                        "OR LOWER(c.categoryName) LIKE ?";

        try (

                Connection con =
                        ConnectDB.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)

        ) {

            String search =
                    "%" + keyword.toLowerCase() + "%";

            ps.setString(1, search);

            ps.setString(2, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Book b = new Book(

                        rs.getInt("id"),

                        rs.getString("title"),

                        rs.getString("author"),

                        rs.getInt("quantity"),

                        rs.getString("categoryName"),

                        rs.getString("publisherName")
                );

                list.add(b);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }
}
