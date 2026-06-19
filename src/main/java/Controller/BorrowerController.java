package Controller;

import DAO.BookDAO;
import DAO.BorrowingDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.TableRow;

import model.Book;
import model.User;

public class BorrowerController {

    private User borrower;

    private final BookDAO bookDAO = new BookDAO();
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    @FXML private TableView<Book> tableBooks;

    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colAvailable;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, String> colPublisher;

    @FXML private TextField txtSearch;


    public void setBorrower(User user) {

        this.borrower = user;

        System.out.println(
                "Borrower Login: " + user.getUsername()
        );
    }


    @FXML
    public void initialize() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colTitle.setCellValueFactory(
                new PropertyValueFactory<>("title"));

        colAuthor.setCellValueFactory(
                new PropertyValueFactory<>("author"));

        colAvailable.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        colCategory.setCellValueFactory(
                new PropertyValueFactory<>("categoryName"));

        colPublisher.setCellValueFactory(
                new PropertyValueFactory<>("publisherName"));

        loadBooksData();


        txtSearch.textProperty().addListener(
                (obs, oldVal, newVal) -> handleSearch()
        );


        tableBooks.setRowFactory(tv -> new TableRow<Book>() {

            @Override
            protected void updateItem(Book item, boolean empty) {

                super.updateItem(item, empty);

                if (item == null || empty) {

                    setStyle("");

                } else if (item.getQuantity() <= 0) {

                    setStyle(
                            "-fx-background-color: #ffcccc;"
                    );

                } else {

                    setStyle("");
                }
            }
        });
    }


    public void loadBooksData() {

        ObservableList<Book> list =
                FXCollections.observableArrayList(
                        bookDAO.getAllBooks()
                );

        tableBooks.setItems(list);

        tableBooks.refresh();
    }


    @FXML
    private void handleSearch() {

        String keyword =
                txtSearch.getText()
                        .toLowerCase()
                        .trim();

        if (keyword.isEmpty()) {

            loadBooksData();

            return;
        }

        ObservableList<Book> allBooks =
                FXCollections.observableArrayList(
                        bookDAO.getAllBooks()
                );

        ObservableList<Book> filteredList =
                FXCollections.observableArrayList();

        for (Book b : allBooks) {

            boolean matchTitle =
                    b.getTitle()
                            .toLowerCase()
                            .contains(keyword);

            boolean matchCategory =
                    b.getCategoryName() != null
                            &&
                            b.getCategoryName()
                                    .toLowerCase()
                                    .contains(keyword);

            if (matchTitle || matchCategory) {

                filteredList.add(b);
            }
        }

        tableBooks.setItems(filteredList);
    }


    @FXML
    public void borrowBook() {

        if (borrower == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Chưa xác định người dùng!"
            );

            return;
        }

        Book selectedBook =
                tableBooks.getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Thông báo",
                    "Vui lòng chọn sách!"
            );

            return;
        }

        if (selectedBook.getQuantity() <= 0) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Sách đã hết!"
            );

            return;
        }

        boolean success =
                borrowingDAO.borrowBookTransaction(
                        borrower.getId(),
                        selectedBook.getId()
                );

        if (success) {

            loadBooksData();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Bạn đã mượn: "
                            + selectedBook.getTitle()
            );

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Thất bại",
                    "Không thể mượn sách!"
            );
        }
    }


    private void showAlert(
            Alert.AlertType type,
            String title,
            String content
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
