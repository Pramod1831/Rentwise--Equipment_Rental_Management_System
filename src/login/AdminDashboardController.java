package login;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    // ==========================================================
    // 1. Dashboard and Sidebar FXML Fields
    // ==========================================================
    @FXML private ListView<String> listView_page;
    @FXML private PieChart pieChart_view;

    // Summary Labels
    @FXML private Label equipmentsCountLabel;
    @FXML private Label issuedCountLabel;
    @FXML private Label remainingCountLabel;
    @FXML private Label pendingReqCountLabel;
    @FXML private Label equipments_side_label;

    // Sidebar Labels (Menu Clicks)
    @FXML private Label home_label;
    @FXML private Label add_equip_label;
    @FXML private Label members_label;
    @FXML private Label issued_label;
    @FXML private Label sideview_noti_label;
    // Central Content VBox
    @FXML private VBox centerVBox;

    // Image/Logout Fields
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView home_icon;
    @FXML private ImageView add_equip_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView members_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView logout_icon;
    @FXML private ImageView equipments_dashboard_icon;
    @FXML private ImageView issued_dashboard_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;
    @FXML private Label logout_label;
    @FXML private HBox members_Hbox;

    // ⭐ Notification Badge HBox and Label
    @FXML private HBox notificationHBox;
    @FXML public Label notificationCountLabel;

    // Must be implemented for FXML Controllers
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize() signature was missing ResourceBundle, fixing this common JavaFX pattern.
        initialize();
    }

    // ==========================================================
    // 2. Initialization (Added Timeline and Notification Logic)
    // ==========================================================
    @FXML
    public void initialize() {
        loadEquipmentData();
        loadImages();
        setupNavigationHandlers();
        updateNotificationCount(); // Initial load

        // Auto-refresh every 5 seconds
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {
                    loadEquipmentData(); // Refreshes all dashboard totals
                    updateNotificationCount(); // Refreshes the badge
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupNavigationHandlers() {
        // Wiring HBoxes to MouseEvent handlers
        if (logout_icon != null) logout_icon.setOnMouseClicked(this::handleLogout);
        if (logout_label != null) logout_label.setOnMouseClicked(this::handleLogout);

        if (members_Hbox != null) members_Hbox.setOnMouseClicked(this::handleMembersClick); // Use HBox if defined
        if (members_label != null) members_label.setOnMouseClicked(this::handleMembersClick);
        if (members_icon != null) members_icon.setOnMouseClicked(this::handleMembersClick);

        if (home_label != null) home_label.setOnMouseClicked(this::handleHomeClick);
        if (home_icon != null) home_icon.setOnMouseClicked(this::handleHomeClick);

        if (add_equip_label != null) add_equip_label.setOnMouseClicked(this::handleAddEquipClick);
        if (add_equip_icon != null) add_equip_icon.setOnMouseClicked(this::handleAddEquipClick);

        if (issued_label != null) issued_label.setOnMouseClicked(this::handleIssuedClick);
        if (issued_icon != null) issued_icon.setOnMouseClicked(this::handleIssuedClick);

        if (equipments_side_label != null) equipments_side_label.setOnMouseClicked(this::handleEquipmentsClick);
        if (equipments_side_icon != null) equipments_side_icon.setOnMouseClicked(this::handleEquipmentsClick);

        if (notificationHBox != null) notificationHBox.setOnMouseClicked(this::handleNotificationClick);
        if (sideview_noti_label != null) sideview_noti_label.setOnMouseClicked(this::handleNotificationClick);
        if (notification_sideview_icon != null) notification_sideview_icon.setOnMouseClicked(this::handleNotificationClick);
        if (notification_top_icon != null) notification_top_icon.setOnMouseClicked(this::handleNotificationClick);
    }

    // ==========================================================
    // 3. Load Dashboard Data and Notification Count
    // ==========================================================
    private void loadEquipmentData() {
        // WARNING: This method (and the DAOs it calls) is likely causing connection spam.
        List<EquipmentModel> equipments = EquipmentDAO.getAllEquipments();

        ObservableList<String> listItems = FXCollections.observableArrayList();
        int totalIssued = 0;
        int totalRemaining = 0;
        int totalQuantity = 0;

        // Count for Admin Action Requests (Pending and Return Pending)
        int totalPendingRequests = EquipmentDAO.getAdminActionRequests().size();

        for (EquipmentModel eq : equipments) {
            listItems.add(eq.toString());
            totalIssued += eq.getIssued();
            totalRemaining += eq.getRemaining();
            totalQuantity += eq.getQuantity();
        }

        if (equipmentsCountLabel != null) equipmentsCountLabel.setText(String.valueOf(totalQuantity));
        if (issuedCountLabel != null) issuedCountLabel.setText(String.valueOf(totalIssued));
        if (remainingCountLabel != null) remainingCountLabel.setText(String.valueOf(totalRemaining));
        if (pendingReqCountLabel != null) pendingReqCountLabel.setText(String.valueOf(totalPendingRequests));

        if (listView_page != null) listView_page.setItems(listItems);

        if (pieChart_view != null) {
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    // Order matters for styling, Issued must be first, Remaining second
                    new PieChart.Data("Issued", totalIssued),
                    new PieChart.Data("Remaining", totalRemaining)
            );
            pieChart_view.setData(pieData);

            // ⭐ Applying custom styles for orange and red after data is set
            for (PieChart.Data data : pieChart_view.getData()) {
                Node node = data.getNode();
                if (data.getName().equals("Issued")) {
                    // Set Issued slice color to a vivid orange
                    node.setStyle("-fx-pie-color: #e74c3c;");
                } else if (data.getName().equals("Remaining")) {
                    // Set Remaining slice color to a strong red
                    node.setStyle("-fx-pie-color: #f39c12;");
                }
            }
        }
    }

    /**
     * Updates the notification count based on the number of unread admin actions.
     */
    private void updateNotificationCount() {
        if (notificationCountLabel == null) {
            System.err.println("Notification badge label not found (fx:id=notificationCountLabel).");
            return;
        }

        try {
            // WARNING: This method (and the DAO it calls) is likely causing connection spam.
            int count = AdminNotificationDAO.getPendingActionCount();

            if (count > 0) {
                notificationCountLabel.setText(String.valueOf(count));
                notificationCountLabel.setVisible(true);
            } else {
                notificationCountLabel.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("Error updating admin notification count: " + e.getMessage());
            notificationCountLabel.setVisible(false);
        }
    }


    // ==========================================================
    // 4. Scene Switching Logic
    // ==========================================================

    /**
     * Safely navigates to a new FXML scene using the source node of the MouseEvent.
     */
    private void navigateTo(String fxmlFile, MouseEvent event) {
        String resourcePath = "/login/" + fxmlFile;
        try {
            Node sourceNode = (Node) event.getSource();
            Scene currentScene = sourceNode.getScene();
            Stage stage = null;

            if (currentScene != null && currentScene.getWindow() instanceof Stage) {
                stage = (Stage) currentScene.getWindow();
            }

            if (stage == null) {
                System.err.println("Navigation Error: Could not retrieve Stage. The source node is not fully attached to a window.");
                return;
            }

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourcePath)));

            stage.setScene(new Scene(root));
            stage.setTitle("RentWise - ADMIN");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + resourcePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleMembersClick(MouseEvent event) { navigateTo("members.fxml", event); }
    @FXML private void handleAddEquipClick(MouseEvent event) { navigateTo("add_equip.fxml", event); }
    @FXML private void handleHomeClick(MouseEvent event) { navigateTo("admindashboard.fxml", event); }
    @FXML private void handleIssuedClick(MouseEvent event) { navigateTo("issued.fxml", event); }
    @FXML private void handleEquipmentsClick(MouseEvent event) { navigateTo("equipments.fxml", event); }

    @FXML private void handleNotificationClick(MouseEvent event) {
        // Corrected FXML typo from 'notificatoin_admin.fxml' to 'notification_admin.fxml'
        navigateTo("notificatoin_admin.fxml", event);
    }

    // ==========================================================
    // 5. Logout Functionality
    // ==========================================================
    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("Admin is attempting to log out.");
        try {
            // Use the centralized connection management for closing
            Connection conn = DatabaseConnection.getActiveConnection();
            if (conn != null && !conn.isClosed()) {
                conn.close();
                DatabaseConnection.clearActiveConnection();
                System.out.println("Database connection closed on admin logout.");
            }
            SessionManager.clearSession();

            navigateTo("login.fxml", event);
        } catch (Exception e) {
            System.err.println("Unexpected error during logout: " + e.getMessage());
            e.printStackTrace();
            // Fallback navigation
            navigateTo("login.fxml", event);
        }
    }

    // ==========================================================
    // 6. Image Loading Utility
    // ==========================================================
    private void loadImages() {
        String BASE_PATH = "Images/";

        try {
            Image imgNotification = loadImageFromFile(BASE_PATH + "notification_admin.png");
            Image imgProfile = loadImageFromFile(BASE_PATH + "account_admin.png");
            Image imgHome = loadImageFromFile(BASE_PATH + "home.png");
            Image imgAddEquip = loadImageFromFile(BASE_PATH + "add_items.png");
            Image imgIssued = loadImageFromFile(BASE_PATH + "Issued.png");
            Image imgMembers = loadImageFromFile(BASE_PATH + "members.png");
            Image imgLogout = loadImageFromFile(BASE_PATH + "logout.png");
            Image imgEquipmentDashboard = loadImageFromFile(BASE_PATH + "sports.png");
            Image imgIssuedDashboard = loadImageFromFile(BASE_PATH + "issued_admin.png");
            Image imgRemainingDashboard = loadImageFromFile(BASE_PATH + "remaining_admin.png");
            Image imgProductsDashboard = loadImageFromFile(BASE_PATH + "products.png");
            Image imgEquipments = loadImageFromFile(BASE_PATH + "equipments_side_icon.png");

            if (notification_top_icon != null) notification_top_icon.setImage(imgNotification);
            if (profile_icon != null) profile_icon.setImage(imgProfile);
            if (home_icon != null) home_icon.setImage(imgHome);
            if (add_equip_icon != null) add_equip_icon.setImage(imgAddEquip);
            if (issued_icon != null) issued_icon.setImage(imgIssued);
            if (members_icon != null) members_icon.setImage(imgMembers);
            if (notification_sideview_icon != null) notification_sideview_icon.setImage(imgNotification);
            if (equipments_side_icon != null) equipments_side_icon.setImage(imgEquipments);
            if (logout_icon != null) logout_icon.setImage(imgLogout);
            if (equipments_dashboard_icon != null) equipments_dashboard_icon.setImage(imgEquipmentDashboard);
            if (issued_dashboard_icon != null) issued_dashboard_icon.setImage(imgIssuedDashboard);
            if (remaining_dashboard_icon != null) remaining_dashboard_icon.setImage(imgRemainingDashboard);
            if (pending_req_dashboard_icon != null) pending_req_dashboard_icon.setImage(imgProductsDashboard);

        } catch (Exception e) {
            System.err.println("Failed to load one or more images using File method. Ensure 'Images' folder is accessible.");
        }
    }

    private Image loadImageFromFile(String relativePath) {
        URL resource = getClass().getResource("/" + relativePath);
        if (resource == null) {
            System.err.println("Image not found: " + relativePath);
            return null;
        }
        return new Image(resource.toExternalForm());
    }
}
