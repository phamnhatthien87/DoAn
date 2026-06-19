package Controller;

import DAO.BookDAO;
import DAO.BorrowingDAO;
import DAO.CategoryDAO;
import DAO.PublisherDAO;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import model.Book;
import model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibrarianController {


    @FXML private TextField txtBookId;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtQuantity;

    @FXML private ComboBox<String> cbCategory;
    @FXML private ComboBox<String> cbPublisher;

    @FXML private TableView<Book> tblBooks;

    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, Integer> colQuantity;

    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colCat;
    @FXML private TableColumn<Book, String> colPub;
    @FXML private TextField txtSearch;


    @FXML private TableView<String[]> tblHistory;

    @FXML private TableColumn<String[], String> colHistoryUser;
    @FXML private TableColumn<String[], String> colHistoryBook;
    @FXML private TableColumn<String[], String> colHistoryDate;
    @FXML private TableColumn<String[], String> colHistoryStatus;

    @FXML private Label lblTotalLoaned;


    private User librarian;


    private final BookDAO bookDAO = new BookDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PublisherDAO publisherDAO = new PublisherDAO();


    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> publisherMap = new HashMap<>();


    public void setLibrarian(User user) {

        this.librarian = user;

        System.out.println("Thủ thư: " + user.getUsername());
    }


    @FXML
    public void initialize() {


        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colPub.setCellValueFactory(new PropertyValueFactory<>("publisherName"));


        colHistoryUser.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue()[1]));

        colHistoryBook.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue()[2]));

        colHistoryDate.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue()[3]));

        colHistoryStatus.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue()[4]));

        loadStaticData();
        loadBooks();
        loadHistory();
    }


    private void loadStaticData() {


        cbCategory.getItems().clear();

        categoryMap = categoryDAO.getAllToMap();

        cbCategory.getItems().addAll(categoryMap.keySet());


        cbPublisher.getItems().clear();

        publisherMap = publisherDAO.getAllPublishers();

        cbPublisher.getItems().addAll(publisherMap.keySet());
    }


    @FXML
    public void addBookAction() {

        try {

            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String qtyText = txtQuantity.getText().trim();

            if (title.isEmpty()
                    || author.isEmpty()
                    || qtyText.isEmpty()
                    || cbCategory.getValue() == null
                    || cbPublisher.getValue() == null) {

                showAlert("Thông báo", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            int qty = Integer.parseInt(qtyText);

            Book b = new Book(0, title, author, qty, "", "");

            b.setCategoryId(categoryMap.get(cbCategory.getValue()));
            b.setPublisherId(publisherMap.get(cbPublisher.getValue()));

            if (bookDAO.insert(b)) {

                showAlert("Thành công", "Đã thêm sách!");

                loadBooks();

                clearForm();

            } else {

                showAlert("Lỗi", "Không thể thêm sách!");
            }

        } catch (NumberFormatException e) {

            showAlert("Lỗi", "Số lượng phải là số!");
        }
    }


    @FXML
    public void deleteBookAction() {

        Book selected = tblBooks.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showAlert("Thông báo", "Vui lòng chọn sách!");
            return;
        }

        if (borrowingDAO.isBookBorrowed(selected.getId())) {

            showAlert("Lỗi", "Sách đang được mượn, không thể xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa sách này?");

        if (confirm.showAndWait().get() == ButtonType.OK) {

            if (bookDAO.delete(selected.getId())) {

                showAlert("Thành công", "Đã xóa sách!");

                loadBooks();

                clearForm();

            } else {

                showAlert("Lỗi", "Xóa thất bại!");
            }
        }
    }


    private void loadBooks() {

        tblBooks.setItems(
                FXCollections.observableArrayList(
                        bookDAO.getAllBooks()
                )
        );
    }


    @FXML
    public void loadHistory() {

        List<String[]> history = borrowingDAO.getHistory();

        tblHistory.setItems(
                FXCollections.observableArrayList(history)
        );

        long count = history.stream()
                .filter(row -> "borrowing".equalsIgnoreCase(row[4]))
                .count();

        lblTotalLoaned.setText(
                "Sách đang được mượn: " + count
        );
    }


    @FXML
    public void handleReturnBook() {

        String[] selected =
                tblHistory.getSelectionModel().getSelectedItem();

        if (selected == null) {

            showAlert("Thông báo", "Vui lòng chọn một lượt mượn!");
            return;
        }

        System.out.println("===== DEBUG RETURN =====");

        for (int i = 0; i < selected.length; i++) {

            System.out.println(
                    "selected[" + i + "] = " + selected[i]
            );
        }

        if ("returned".equalsIgnoreCase(selected[4])) {

            showAlert("Thông báo", "Sách đã được trả trước đó!");
            return;
        }

        try {

            int borrowId = Integer.parseInt(selected[0]);
            int bookId = Integer.parseInt(selected[5]);

            System.out.println("borrowId = " + borrowId);
            System.out.println("bookId = " + bookId);

            Alert confirm =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Xác nhận");
            confirm.setHeaderText(null);
            confirm.setContentText("Xác nhận trả sách?");

            if (confirm.showAndWait().get() == ButtonType.OK) {

                boolean success =
                        borrowingDAO.returnBookTransaction(
                                borrowId,
                                bookId
                        );

                System.out.println("return result = " + success);

                if (success) {

                    showAlert("Thành công", "Đã trả sách!");

                    loadHistory();
                    loadBooks();

                } else {

                    showAlert("Lỗi", "Trả sách thất bại!");
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            showAlert("Lỗi", "Dữ liệu không hợp lệ!");
        }
    }


    @FXML
    public void resetFormAction() {

        clearForm();
    }


    private void clearForm() {

        txtBookId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtQuantity.clear();

        cbCategory.getSelectionModel().clearSelection();
        cbPublisher.getSelectionModel().clearSelection();
    }


    private void showAlert(String title, String msg) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait();
    }


}
