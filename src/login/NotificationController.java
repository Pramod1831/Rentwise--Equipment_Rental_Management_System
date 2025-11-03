package login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class NotificationController {

    // --- FXML Table Elements ---
    @FXML private TableView<RequestDisplayModel> requestsTable;
    @FXML private TableColumn<RequestDisplayModel, String> requestDateColumn;
    @FXML private TableColumn<RequestDisplayModel, String> userNameColumn;
    @FXML private TableColumn<RequestDisplayModel, String> equipmentNameColumn;
    @FXML private TableColumn<RequestDisplayModel, Integer> quantityColumn;
    @FXML private TableColumn<RequestDisplayModel, Void> actionsColumn;

    // --- FXML Navigation Containers (HBox) ---
    // ADDED/CORRECTED HBox fields for navigation
    @FXML private HBox homeHBox;
    @FXML private HBox equipmentsHBox;
    @FXML private HBox addequipHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox membersHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;

    // --- FXML Icon/Label Elements (Unchanged) ---
    @FXML private ImageView home_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
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

    @FXML private Label add_equip_label;
    @FXML private Label members_label;
    @FXML private Label issued_label;
    @FXML private Label sideview_noti_label;
    @FXML private Label logout_label;
    @FXML private Label home_label;
    @FXML private Label equipments_side_label;

    private ObservableList<RequestDisplayModel> pendingRequests = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadImages();

        // 1. Set up the cell value factories to link model properties to columns
        // Assuming your RequestDisplayModel has getRequestDate(), getUserName(), getEquipmentName(), and getQuantity()
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // 2. Set up the custom "Actions" column with buttons
        setupActionsColumn();

        // 3. Load the data
        loadPendingRequests();
        requestsTable.setItems(pendingRequests);

        // 4. Set up Navigation Handlers
        if (logout_icon != null) logout_icon.setOnMouseClicked(this::handleLogout);
        if (logout_label != null) logout_label.setOnMouseClicked(this::handleLogout);

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

        if (sideview_noti_label != null) sideview_noti_label.setOnMouseClicked(this::handleNotificationClick);
        if (notification_sideview_icon != null) notification_sideview_icon.setOnMouseClicked(this::handleNotificationClick);
        if (notification_top_icon != null) notification_top_icon.setOnMouseClicked(this::handleNotificationClick);
    }

    //----------------------------------------------------------------------
    //                           NAVIGATION SETUP
    //----------------------------------------------------------------------


    private void navigateTo(String fxmlFile, MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("RentWise - " + fxmlFile.replace(".fxml", "").toUpperCase());
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleMembersClick(MouseEvent event) { navigateTo("members.fxml", event); }
    @FXML private void handleAddEquipClick(MouseEvent event) { navigateTo("add_equip.fxml", event); }
    @FXML private void handleHomeClick(MouseEvent event) { navigateTo("admindashboard.fxml", event); }
    @FXML private void handleIssuedClick(MouseEvent event) { navigateTo("issued.fxml", event); } // Placeholder
    @FXML private void handleEquipmentsClick(MouseEvent event) { navigateTo("equipments.fxml", event); }
    @FXML private void handleNotificationClick(MouseEvent event) { navigateTo("notificatoin_admin.fxml", event); }




    @FXML
    private void handleLogout(MouseEvent event) {
        System.out.println("User is attempting to log out.");
        try {
            // FIX: Use the specific method to get and close the active connection
            Connection conn = DatabaseConnection.getActiveConnection(); // Assuming this exists
            if (conn != null && !conn.isClosed()) {
                conn.close();
                DatabaseConnection.clearActiveConnection(); // Assuming this exists
                System.out.println("Database connection closed on logout.");
            }
            // Assuming SessionManager.clearSession() exists
            // SessionManager.clearSession();

            navigateTo("login.fxml", event);
        } catch (Exception e) {
            System.err.println("Unexpected error during logout: " + e.getMessage());
            e.printStackTrace();
            navigateTo("login.fxml", event);
        }
    }
    //----------------------------------------------------------------------
    //                           TABLE & ACTION LOGIC (Unchanged)
    //----------------------------------------------------------------------

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<RequestDisplayModel, Void>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox pane = new HBox(10, approveButton, rejectButton);

            {
                approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");
                rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");

                approveButton.setOnAction(event -> {
                    RequestDisplayModel item = getTableView().getItems().get(getIndex());
                    handleApprove(item);
                });

                rejectButton.setOnAction(event -> {
                    RequestDisplayModel item = getTableView().getItems().get(getIndex());
                    handleReject(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);

                    RequestDisplayModel displayModel = getTableView().getItems().get(getIndex());
                    String status = displayModel.getRequestObject().getStatus();

                    if ("Pending".equals(status)) {
                        approveButton.setText("Approve Equipment");
                        rejectButton.setText("Reject Equipment");
                        if (!pane.getChildren().contains(rejectButton)) pane.getChildren().add(rejectButton);

                    } else if ("Return Pending".equals(status)) {
                        approveButton.setText("Approve Return");
                        rejectButton.setText("Inspect/Hold");
                        // Recommended: Hide Reject button for returns if not needed
                        pane.getChildren().remove(rejectButton);

                    } else {
                        approveButton.setText("Approve");
                        rejectButton.setText("Reject");
                    }
                }
            }
        });
    }

    private void loadPendingRequests() {
        List<RequestModel> rawRequests = EquipmentDAO.getAdminActionRequests();
        pendingRequests.clear();

        for (RequestModel req : rawRequests) {
            String userName = EquipmentDAO.getUserNameById(req.getUserId());
            String equipName = EquipmentDAO.getEquipmentNameById(req.getEquipmentId());

            pendingRequests.add(new RequestDisplayModel(req, userName, equipName));
        }
        System.out.println("Loaded " + pendingRequests.size() + " pending requests from DB.");
    }

    private void handleApprove(RequestDisplayModel displayModel) {
        RequestModel request = displayModel.getRequestObject();

        boolean success = EquipmentDAO.approveRequest(
                request.getId(),
                request.getEquipmentId(),
                request.getQuantityRequested());

        if (success) {
            pendingRequests.remove(displayModel);
            showAlert("Approved", "Request ID " + request.getId() + " approved successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Failed", "Failed to approve request ID " + request.getId() + ". Check server logs.", Alert.AlertType.ERROR);
        }
    }

    private void handleReject(RequestDisplayModel displayModel) {
        RequestModel request = displayModel.getRequestObject();

        boolean success = EquipmentDAO.rejectRequest(request.getId());

        if (success) {
            pendingRequests.remove(displayModel);
            showAlert("Rejected", "Request ID " + request.getId() + " rejected successfully.", Alert.AlertType.WARNING);
        } else {
            showAlert("Failed", "Failed to reject request ID " + request.getId() + ". Check server logs.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //----------------------------------------------------------------------
    //                           IMAGE UTILITY (Unchanged)
    //----------------------------------------------------------------------

    private void loadImages() {
        String BASE_PATH = "Images/";
        try {
            if (notification_top_icon != null) notification_top_icon.setImage(loadImageFromFile(BASE_PATH + "notification_admin.png"));
            if (profile_icon != null) profile_icon.setImage(loadImageFromFile(BASE_PATH + "account_admin.png"));
            if (home_icon != null) home_icon.setImage(loadImageFromFile(BASE_PATH + "home.png"));
            if (add_equip_icon != null) add_equip_icon.setImage(loadImageFromFile(BASE_PATH + "add_items.png"));
            if (issued_icon != null) issued_icon.setImage(loadImageFromFile(BASE_PATH + "Issued.png"));
            if (members_icon != null) members_icon.setImage(loadImageFromFile(BASE_PATH + "members.png"));
            if (notification_sideview_icon != null) notification_sideview_icon.setImage(loadImageFromFile(BASE_PATH + "notification_admin.png"));
            if (equipments_side_icon != null) equipments_side_icon.setImage(loadImageFromFile(BASE_PATH + "equipments_side_icon.png"));
            if (logout_icon != null) logout_icon.setImage(loadImageFromFile(BASE_PATH + "logout.png"));
            if (equipments_dashboard_icon != null) equipments_dashboard_icon.setImage(loadImageFromFile(BASE_PATH + "sports.png"));
            if (issued_dashboard_icon != null) issued_dashboard_icon.setImage(loadImageFromFile(BASE_PATH + "issued_admin.png"));
            if (remaining_dashboard_icon != null) remaining_dashboard_icon.setImage(loadImageFromFile(BASE_PATH + "remaining_admin.png"));
            if (pending_req_dashboard_icon != null) pending_req_dashboard_icon.setImage(loadImageFromFile(BASE_PATH + "products.png"));
        } catch (Exception e) {
            System.err.println("Failed to load one or more images.");
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