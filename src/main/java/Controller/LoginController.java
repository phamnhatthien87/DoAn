package Controller;

import DAO.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import model.User;

public class LoginController {

    private final UserDAO userDAO = new UserDAO();

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;


    @FXML
    private void handleLogin() {

        String username =
                txtUser.getText().trim();

        String password =
                txtPass.getText().trim();

        if (username.isEmpty()
                || password.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Thông báo",
                    "Vui lòng nhập đầy đủ tài khoản và mật khẩu!"
            );

            return;
        }

        User user =
                userDAO.login(username, password);

        if (user == null) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Đăng nhập thất bại",
                    "Sai tài khoản hoặc mật khẩu!"
            );

            return;
        }

        System.out.println(
                "Login success: " + user.getRole()
        );

        switch (user.getRole().toLowerCase()) {

            case "borrower":

                openBorrowerUI(user);
                break;

            case "librarian":

                openLibrarianUI(user);
                break;

            default:

                showAlert(
                        Alert.AlertType.ERROR,
                        "Lỗi",
                        "Role không hợp lệ!"
                );
        }
    }


    private void openBorrowerUI(User user) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/View/borrower.fxml"
                            )
                    );

            Parent root = loader.load();

            BorrowerController controller =
                    loader.getController();

            controller.setBorrower(user);

            changeScene(
                    root,
                    "Hệ thống - Người mượn"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể mở giao diện Borrower!"
            );
        }
    }


    private void openLibrarianUI(User user) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/View/librarian.fxml"
                            )
                    );

            Parent root = loader.load();

            LibrarianController controller =
                    loader.getController();

            controller.setLibrarian(user);

            changeScene(
                    root,
                    "Hệ thống - Thủ thư"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể mở giao diện Librarian!"
            );
        }
    }


    @FXML
    private void openSignUp() {

        try {

            Parent root =
                    FXMLLoader.load(
                            getClass().getResource(
                                    "/View/signup.fxml"
                            )
                    );

            changeScene(
                    root,
                    "Đăng ký tài khoản"
            );

        } catch (Exception e) {

            e.printStackTrace();

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể mở màn hình đăng ký!"
            );
        }
    }


    private void changeScene(
            Parent root,
            String title
    ) {

        Stage stage =
                (Stage) txtUser.getScene().getWindow();

        stage.setScene(new Scene(root));

        stage.setTitle(title);

        stage.show();
    }


    private void showAlert(
            Alert.AlertType type,
            String title,
            String msg
    ) {

        Alert alert = new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(msg);

        alert.showAndWait();
    }
}
