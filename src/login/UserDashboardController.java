package login;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    @FXML public Label notificationCountLabel;


    @FXML private HBox equipmentsHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;
    @FXML private HBox homeHBox;
    @FXML private HBox issuedHBox;

    @FXML private ImageView home_icon;
    @FXML private ImageView received_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;

    @FXML private Label totalEquipmentsLabel;
    @FXML private Label totalIssuedLabel;
    @FXML private Label totalRemainingLabel;
    @FXML private Label totalPendingLabel;

    @FXML private ImageView equipments_icon;
    @FXML private ImageView received_card_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;
    @FXML private ImageView logout_icon;

    @FXML private VBox equipmentListContainer;

    @FXML private Label home_label;
    @FXML private Label received_label;
    @FXML private Label equipments_side_label;
    @FXML private Label sideview_noti_label;
    @FXML private Label logout_label;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (totalEquipmentsLabel == null) {
            System.err.println("FATAL ERROR: FXML loading failed. totalEquipmentsLabel is null. Check fx:id in FXML.");
            return;
        }

        setupNavigationHandlers();
        loadIcons();
        refreshDashboardData();
        updateNotificationCount();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    refreshDashboardData();
                    updateNotificationCount(); // Keep badge updated
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupNavigationHandlers() {
        if (logoutHBox != null) logoutHBox.setOnMouseClicked(this::handleLogout);
        if (homeHBox != null) homeHBox.setOnMouseClicked(this::handleHomeClick);
        if (issuedHBox != null) issuedHBox.setOnMouseClicked(this::handleIssuedClick);
        if (notificationHBox != null) notificationHBox.setOnMouseClicked(this::handleNotificationClick);
        if (equipmentsHBox != null) equipmentsHBox.setOnMouseClicked(this::handleEquipmentsClick);
    }

    private void handleHomeClick(MouseEvent event) { navigateTo("userdashboard.fxml", event); }
    private void handleIssuedClick(MouseEvent event) { navigateTo("received.fxml", event); }

    private void handleNotificationClick(MouseEvent event) {
        int currentUserId = SessionManager.getLoggedInUserId();
        NotificationDAO.acknowledgeRequestStatus(currentUserId);
        updateNotificationCount();
        navigateTo("notification_user.fxml", event);
    }

    private void handleEquipmentsClick(MouseEvent event) { navigateTo("user_equipments.fxml", event); }

    private void navigateTo(String fxmlFile, MouseEvent event) {
        String resourcePath = "/login/" + fxmlFile;
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(resourcePath)));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("RentWise");
            stage.show();
        } catch (IOException e) {
            System.err.println("Navigation Failed: Could not load FXML file: " + resourcePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected navigation error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void refreshDashboardData() {
        loadCardTotals();
        loadDynamicEquipmentCards();
    }

    private void updateNotificationCount() {
        if (notificationCountLabel == null) {
            System.err.println("Notification badge label not found in FXML (fx:id=notificationCountLabel).");
            return;
        }

        try {
            int currentUserId = SessionManager.getLoggedInUserId();
            int count = NotificationDAO.getUnseenNotificationCount(currentUserId);

            if (count > 0) {
                notificationCountLabel.setText(String.valueOf(count));
                notificationCountLabel.setVisible(true);
            } else {
                notificationCountLabel.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Error updating notification count: " + e.getMessage());
            notificationCountLabel.setVisible(false);
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
            System.err.println("Failed to load image: " + fileName + ". Error: " + e.getMessage());
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
        if (home_icon != null) home_icon.setImage(loadImage("home.png"));
        if (received_icon != null) received_icon.setImage(loadImage("Issued.png"));
        if (equipments_side_icon != null) equipments_side_icon.setImage(loadImage("equipments_side_icon.png"));
        if (notification_sideview_icon != null) notification_sideview_icon.setImage(loadImage("notification_admin.png"));
        if (notification_top_icon != null) notification_top_icon.setImage(loadImage("notification_admin.png"));
        if (profile_icon != null) profile_icon.setImage(loadImage("account_admin.png"));
        if (equipments_icon != null) equipments_icon.setImage(loadImage("sports.png"));
        if (received_card_icon != null) received_card_icon.setImage(loadImage("invoice.png"));
        if (remaining_dashboard_icon != null) remaining_dashboard_icon.setImage(loadImage("remaining_admin.png"));
        if (pending_req_dashboard_icon != null) pending_req_dashboard_icon.setImage(loadImage("products.png"));
        if (logout_icon != null) logout_icon.setImage(loadImage("logout.png"));
    }

    private void loadCardTotals() {
        int totalEquipments = 0;
        int totalIssued = 0;
        int totalRemaining = 0;
        int pendingRequests = 0;

        try {
            List<EquipmentModel> equipments = EquipmentDAO.getAllEquipments();

            for (EquipmentModel equipment : equipments) {
                totalEquipments += equipment.getQuantity();
                totalIssued += equipment.getIssued();
                totalRemaining += equipment.getRemaining();
            }

            if (totalEquipmentsLabel != null) totalEquipmentsLabel.setText(String.valueOf(totalEquipments));
            if (totalIssuedLabel != null) totalIssuedLabel.setText(String.valueOf(totalIssued));
            if (totalRemainingLabel != null) totalRemainingLabel.setText(String.valueOf(totalRemaining));

            if (totalPendingLabel != null) totalPendingLabel.setText(String.valueOf(pendingRequests));

        } catch (Exception e) {
            System.err.println("Error loading dashboard card data: " + e.getMessage());
            if (totalEquipmentsLabel != null) totalEquipmentsLabel.setText("N/A");
            if (totalIssuedLabel != null) totalIssuedLabel.setText("N/A");
            if (totalRemainingLabel != null) totalRemainingLabel.setText("N/A");
            if (totalPendingLabel != null) totalPendingLabel.setText("N/A");
        }
    }

    private void loadDynamicEquipmentCards() {
        if (equipmentListContainer == null) {
            System.err.println("Error: equipmentListContainer is null. Check FXML fx:id.");
            return;
        }
        equipmentListContainer.getChildren().clear();

        try {
            List<EquipmentModel> equipments = EquipmentDAO.getAllEquipments();

            if (equipments.isEmpty()) {
                equipmentListContainer.getChildren().add(new Label("No equipment records found in the database."));
                return;
            }

            for (EquipmentModel equipment : equipments) {
                HBox card = createEquipmentCard(equipment);
                equipmentListContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            System.err.println("Error loading dynamic equipment data: " + e.getMessage());
            equipmentListContainer.getChildren().add(new Label("Error: Could not load equipment data from database."));
        }
    }

    private HBox createEquipmentCard(EquipmentModel equipment) {
        HBox cardHBox = new HBox();
        cardHBox.setPrefHeight(100.0);
        cardHBox.setAlignment(Pos.CENTER_LEFT);
        cardHBox.setSpacing(20.0);
        cardHBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        cardHBox.setPadding(new Insets(10));

        ImageView icon = new ImageView();
        icon.setFitHeight(70.0);
        icon.setFitWidth(70.0);
        icon.setPickOnBounds(true);
        icon.setPreserveRatio(true);

        Image equipmentImage = loadImageFromBytes(equipment.getImageData());
        icon.setImage(equipmentImage);

        VBox detailsVBox = new VBox();
        detailsVBox.setSpacing(2.0);

        Label nameLabel = new Label(equipment.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #143a7d;");

        Label totalLabel = new Label("Total: " + equipment.getQuantity());
        Label issuedLabel = new Label("Issued: " + equipment.getIssued());
        Label remainingLabel = new Label("Remaining: " + equipment.getRemaining());
        remainingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: green;");

        detailsVBox.getChildren().addAll(nameLabel, totalLabel, issuedLabel, remainingLabel);

        cardHBox.getChildren().addAll(icon, detailsVBox);

        return cardHBox;
    }

    private void handleLogout(MouseEvent event) {
        System.out.println("User is attempting to log out.");
        try {
            SessionManager.clearSession();

            if (DatabaseConnection.getActiveConnection() != null) {
                DatabaseConnection.getActiveConnection().close();
                System.out.println("Database: Active connection closed on logout.");
            }

            DatabaseConnection.clearActiveConnection();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("login.fxml")));
            Parent root = loader.load();

            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load the login screen.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("An error occurred while closing the database connection.");
        }
    }
}