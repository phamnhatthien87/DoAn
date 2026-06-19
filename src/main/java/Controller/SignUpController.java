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

public class SignUpController {

    private final UserDAO userDAO = new UserDAO();

    @FXML private TextField txtNewUser;

    @FXML private PasswordField txtNewPass;

    @FXML private PasswordField txtConfirmPass;


    @FXML
    private void handleSignUp() {

        String username =
                txtNewUser.getText().trim();

        String password =
                txtNewPass.getText().trim();

        String confirmPassword =
                txtConfirmPass.getText().trim();


        if (username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Vui lòng nhập đầy đủ thông tin!"
            );

            return;
        }


        if (!password.equals(confirmPassword)) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Mật khẩu xác nhận không khớp!"
            );

            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]{4,30}$")) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Tên đăng nhập chỉ được chứa chữ, số, dấu _ và từ 4-30 ký tự!"
            );

            return;
        }

        if (password.length() < 8) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Mật khẩu phải có ít nhất 8 ký tự!"
            );

            return;
        }


        boolean success =
                userDAO.register(
                        username,
                        password
                );

        if (success) {

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Thành công",
                    "Đăng ký thành công!"
            );

            try {

                backToLogin();

            } catch (Exception e) {

                e.printStackTrace();
            }

        } else {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Thất bại",
                    "Tên đăng nhập đã tồn tại!"
            );
        }
    }


    @FXML
    private void backToLogin() throws Exception {

        Parent root =
                FXMLLoader.load(
                        getClass().getResource(
                                "/View/login.fxml"
                        )
                );

        Stage stage =
                (Stage) txtNewUser
                        .getScene()
                        .getWindow();

        stage.setScene(new Scene(root));

        stage.setTitle("Đăng nhập");

        stage.show();
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
