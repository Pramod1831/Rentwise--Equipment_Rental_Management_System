package login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Button cancelButton;
    @FXML private Button loginButton;
    @FXML private Button signUpButton;
    @FXML private Label myLabel;
    @FXML private ImageView brandingImageView;
    @FXML private ImageView lockImageView;
    @FXML private TextField usernameTextField;
    @FXML private TextField enterPasswordField; 

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ✅ Load images directly from classpath (works in JAR)
        brandingImageView.setImage(new Image(
                Objects.requireNonNull(getClass().getResource("/Images/sideview.jpg")).toExternalForm()
        ));
        lockImageView.setImage(new Image(
                Objects.requireNonNull(getClass().getResource("/Images/logo.png")).toExternalForm()
        ));
    }

    @FXML
    private void loginButtonOnAction(javafx.event.ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !enterPasswordField.getText().isBlank()) {
            validateLogin();
        } else {
            myLabel.setText("Please enter username and password");
        }
    }

    private void validateLogin() {
        DatabaseConnection connectNow = new DatabaseConnection();
        String verifyLogin = "SELECT account_id, password, role FROM user_account WHERE username = ?";

        String username = usernameTextField.getText();
        String enteredPassword = enterPasswordField.getText();

        try (Connection connectDB = connectNow.getConnection()) {

            if (connectDB == null) {
                myLabel.setText("Database connection error. Please try again.");
                return;
            }

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, username);

                try (ResultSet queryResult = preparedStatement.executeQuery()) {
                    if (queryResult.next()) {
                        String storedHashedPassword = queryResult.getString("password");

                        if (PasswordHasher.checkPassword(enteredPassword, storedHashedPassword)) {
                            int accountId = queryResult.getInt("account_id");
                            String role = queryResult.getString("role");
                            myLabel.setText("Login Successful!");

                            SessionManager.setLoggedInUser(accountId, role);

                            String fxmlFile;
                            String windowTitle;

                            if ("admin".equalsIgnoreCase(role)) {
                                fxmlFile = "admindashboard.fxml";
                                windowTitle = "Admin Dashboard";
                            } else if ("user".equalsIgnoreCase(role)) {
                                fxmlFile = "userdashboard.fxml";
                                windowTitle = "User Dashboard";
                            } else {
                                fxmlFile = "userdashboard.fxml";
                                windowTitle = "Dashboard";
                            }

                            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                            Parent root = loader.load();
                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.setTitle(windowTitle);
                            stage.show();

                        } else {
                            myLabel.setText("Invalid login. Please try again.");
                        }
                    } else {
                        myLabel.setText("Invalid login. Please try again.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            myLabel.setText("An error occurred during login verification.");
        }
    }

    @FXML
    private void setCancelButtonAction(javafx.event.ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void signUpButtonAction(javafx.event.ActionEvent event) {
        createAccountForm();
    }

    public void createAccountForm() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("register.fxml")));
            Stage registerStage = new Stage();
            registerStage.initStyle(StageStyle.UNDECORATED);
            registerStage.setScene(new Scene(root, 520, 539));
            registerStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
