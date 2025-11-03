package login;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML private ImageView myImageView;
    @FXML private Button closeButton;
    @FXML private Label registrationLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Button registerButton;
    @FXML private TextField firstnameTextField;
    @FXML private TextField lastnameTextField;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField setPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myImageView.setImage(new Image(
                Objects.requireNonNull(getClass().getResource("/Images/rentlogo.png")).toExternalForm()
        ));
    }

    @FXML
    public void registerButtonOnAction(javafx.event.ActionEvent event) throws SQLException {
        if (setPasswordField.getText().equals(confirmPasswordField.getText())) {
            registerUser();
            confirmPasswordLabel.setText("");
        } else {
            confirmPasswordLabel.setText("Password does not match!!!");
        }
    }

    @FXML
    public void closeButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void registerUser() throws SQLException {
        DatabaseConnection connectNow = new DatabaseConnection();

        try (Connection connectDB = connectNow.getConnection()) {

            if (connectDB == null) {
                registrationLabel.setText("Database connection error.");
                return;
            }

            String firstName = firstnameTextField.getText();
            String lastName = lastnameTextField.getText();
            String userName = usernameTextField.getText();
            String plainPassword = setPasswordField.getText();

            String hashedPassword = PasswordHasher.hashPassword(plainPassword);

            String insertToRegister = "INSERT INTO user_account (firstname, lastname, username, password) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(insertToRegister)) {
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, userName);
                preparedStatement.setString(4, hashedPassword);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    registrationLabel.setText("User has been Registered Successfully!");
                } else {
                    registrationLabel.setText("Registration Failed!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            registrationLabel.setText("Registration Failed! (Check if username exists)");
        }
    }
}
