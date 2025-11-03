package login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserEquipmentsController implements Initializable {

    @FXML private HBox homeHBox;
    @FXML private HBox logoutHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox notificationHBox;

    @FXML private FlowPane equipmentFlowPane;
    @FXML private ScrollPane equipmentScrollPane;

    @FXML private ImageView home_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView logout_icon;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadIcons();
        loadEquipmentCards();
        setupNavigationHandlers();
    }

    private void setupNavigationHandlers() {
        if (logoutHBox != null) {
            logoutHBox.setOnMouseClicked(event -> handleLogout());
        }

        if (homeHBox != null) {
            homeHBox.setOnMouseClicked(event -> navigateTo("userdashboard.fxml", homeHBox));
        }

        if (issuedHBox != null) {
            issuedHBox.setOnMouseClicked(event -> navigateTo("received.fxml", issuedHBox));
        }
        if (notificationHBox != null) {
            notificationHBox.setOnMouseClicked(event -> navigateTo("notification_user.fxml", notificationHBox));
        }
    }

    private void navigateTo(String fxmlFile, HBox sourceHBox) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Parent root = loader.load();

            Stage stage = (Stage) sourceHBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RentWise");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("NULL POINTER: FXML file not found at resource path: " + fxmlFile);
            e.printStackTrace();
        }
    }


    private Image loadImage(String fileName) {
        try {
            File file = new File("Images/" + fileName);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            }
            return new Image(Objects.requireNonNull(getClass().getResource("/Images/" + fileName)).toExternalForm());
        } catch (Exception e) {
            return null;
        }
    }

    private Image loadImageFromBytes(byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
                return new Image(bis);
            } catch (Exception e) {
                System.err.println("Failed to load image from byte array: " + e.getMessage());
            }
        }
        return loadImage("default_equipment.png");
    }

    private void loadIcons() {
        if (notification_top_icon != null) notification_top_icon.setImage(loadImage("notification_admin.png"));
        if (profile_icon != null) profile_icon.setImage(loadImage("account_admin.png"));
        if (home_icon != null) home_icon.setImage(loadImage("home.png"));
        if (issued_icon != null) issued_icon.setImage(loadImage("Issued.png"));
        if (equipments_side_icon != null) equipments_side_icon.setImage(loadImage("equipments_side_icon.png"));
        if (notification_sideview_icon != null) notification_sideview_icon.setImage(loadImage("notification_admin.png"));
        if (logout_icon != null) logout_icon.setImage(loadImage("logout.png"));
    }

    private void loadEquipmentCards() {
        if (equipmentFlowPane == null) {
            return;
        }
        equipmentFlowPane.getChildren().clear();

        try {
            List<EquipmentModel> equipments = EquipmentDAO.getAllEquipments();

            if (equipments.isEmpty()) {
                equipmentFlowPane.getChildren().add(new Label("No equipment records found in the database."));
                return;
            }

            equipmentFlowPane.setHgap(25);
            equipmentFlowPane.setVgap(25);

            for (EquipmentModel equipment : equipments) {
                VBox card = createEquipmentCard(equipment);
                equipmentFlowPane.getChildren().add(card);
            }

        } catch (Exception e) {
            System.err.println("Error loading equipment data into FlowPane: " + e.getMessage());
            equipmentFlowPane.getChildren().add(new Label("Error: Could not load data."));
        }
    }

    private VBox createEquipmentCard(EquipmentModel equipment) {

        VBox cardVBox = new VBox();
        cardVBox.setAlignment(Pos.TOP_CENTER);
        cardVBox.setSpacing(10);
        cardVBox.setPrefWidth(200.0);
        cardVBox.setPrefHeight(250.0);
        cardVBox.getStyleClass().add("equipment-card");
        cardVBox.setPadding(new Insets(15));

        ImageView icon = new ImageView(loadImageFromBytes(equipment.getImageData()));
        icon.setFitHeight(120.0);
        icon.setFitWidth(150.0);
        icon.setPreserveRatio(true);
        icon.getStyleClass().add("equipment-image");

        Label nameLabel = new Label(equipment.getName());
        nameLabel.getStyleClass().add("equipment-name");

        Label quantityLabel = new Label("Available: " + equipment.getRemaining());
        quantityLabel.getStyleClass().add("equipment-quantity");

        Button requestButton = new Button("Request");
        requestButton.getStyleClass().addAll("card-button", "edit-button");
        requestButton.setPrefWidth(120);

        requestButton.setOnAction(event -> handleRequestAction(equipment));

        cardVBox.getChildren().addAll(icon, nameLabel, quantityLabel, requestButton);

        return cardVBox;
    }


    private void handleRequestAction(EquipmentModel equipment) {
        System.out.println("Request action initiated for: " + equipment.getName());

        int availableQuantity = equipment.getRemaining();

        if (availableQuantity <= 0) {
            JOptionPane.showMessageDialog(null,
                    "The requested equipment is out of stock.",
                    "Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity = 0;
        boolean validInput = false;

        while (!validInput) {
            String quantityStr = JOptionPane.showInputDialog(
                    null,
                    "How many " + equipment.getName() + " do you want to request? (Available: " + availableQuantity + ")",
                    "Request Quantity",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (quantityStr == null) {
                System.out.println("Request cancelled by user.");
                return;
            }

            try {
                quantity = Integer.parseInt(quantityStr);

                if (quantity > 0 && quantity <= availableQuantity) {
                    validInput = true;
                } else if (quantity <= 0) {
                    JOptionPane.showMessageDialog(null,
                            "Please enter a number greater than 0.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(null,
                            "The quantity requested (" + quantity + ") exceeds the available stock of " + availableQuantity + ".",
                            "Invalid Quantity",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid input. Please enter a valid number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }


        int currentUserId = SessionManager.getLoggedInUserId();

        if (currentUserId == -1) {
            System.err.println("Error: User ID not found. Session may be invalid.");
            JOptionPane.showMessageDialog(null, "User session error. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Attempting to submit request for User ID " + currentUserId + ".");

        boolean success = EquipmentDAO.createNewRequest(
                currentUserId,
                equipment.getId(),
                quantity
        );

        if (success) {
            System.out.println("User requested " + quantity + " of " + equipment.getName() + ". Request sent successfully.");
            JOptionPane.showMessageDialog(null,
                    "Your request for " + quantity + " " + equipment.getName() + " has been sent. It is now pending admin approval.",
                    "Request Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            System.err.println("Database failed to save the request.");
            JOptionPane.showMessageDialog(null,
                    "Failed to send your request due to a database error. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleUserReturn(int requestId) {
        boolean success = EquipmentDAO.requestReturn(requestId);

        if (success) {
            System.out.println("Return initiated successfully. Waiting for admin approval.");
        } else {
            System.err.println("Failed to initiate return request.");
        }
    }

    private void handleLogout() {
        System.out.println("User is attempting to log out.");
        if (logoutHBox != null) {
            navigateTo("login.fxml", logoutHBox);
        }
    }
}