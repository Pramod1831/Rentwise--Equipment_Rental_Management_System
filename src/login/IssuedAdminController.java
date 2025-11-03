package login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File; // Needed for image loading
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class IssuedAdminController {

    // ==========================================================
    // 1. TableView FXML Fields
    // ==========================================================
    @FXML private TableView<RequestDisplayModel> issuedTable;
    @FXML private TableColumn<RequestDisplayModel, String> requestDateColumn;
    @FXML private TableColumn<RequestDisplayModel, String> userNameColumn;
    @FXML private TableColumn<RequestDisplayModel, String> equipmentNameColumn;
    @FXML private TableColumn<RequestDisplayModel, Integer> quantityColumn;
    @FXML private TableColumn<RequestDisplayModel, String> statusColumn;

    // ==========================================================
    // 2. Navigation FXML Fields (Sidebar/Top Bar)
    // ==========================================================
    // Labels
    @FXML private Label add_equip_label;
    @FXML private Label members_label;
    @FXML private Label issued_label;
    @FXML private Label sideview_noti_label;
    @FXML private Label logout_label;
    @FXML private Label home_label;
    @FXML private Label equipments_side_label;


    // Icons
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView home_icon;
    @FXML private ImageView add_equip_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView members_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView logout_icon;

    // Dashboard Icons (for image loading completeness)
    @FXML private ImageView equipments_dashboard_icon;
    @FXML private ImageView issued_dashboard_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;

    // HBoxes (CRITICAL for robust navigation)
    @FXML private HBox homeHBox;
    @FXML private HBox addequipHBox;
    @FXML private HBox membersHBox;
    @FXML private HBox equipmentsHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;
    @FXML private HBox issuedHBox; // Current page

    private ObservableList<RequestDisplayModel> issuedRequests = FXCollections.observableArrayList();

    /**
     * Initializes the controller, sets up the table columns, loads data, and sets up navigation.
     */
    @FXML
    public void initialize() {
        // 1. Setup Table Columns
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Load Data
        loadIssuedRequests();
        issuedTable.setItems(issuedRequests);

        // 3. Load Images
        loadImages();


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
    // ==========================================================
    // 2. Navigation Setup
    // ==========================================================

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

    // ==========================================================
    // 4. DATA LOADING
    // ==========================================================

    private void loadIssuedRequests() {
        // Assuming EquipmentDAO.getRequestsByStatus and helper methods exist
        List<RequestModel> rawRequests = EquipmentDAO.getRequestsByStatus("Approved");

        issuedRequests.clear();

        for (RequestModel req : rawRequests) {
            // Fetch User and Equipment Names using the IDs (Crucial step)
            String userName = EquipmentDAO.getUserNameById(req.getUserId());
            String equipName = EquipmentDAO.getEquipmentNameById(req.getEquipmentId());

            // Create display model and add to list
            issuedRequests.add(new RequestDisplayModel(req, userName, equipName));
        }
        System.out.println("Loaded " + issuedRequests.size() + " currently issued requests.");
    }

    // ==========================================================
    // 5. IMAGE LOADING UTILITY
    // ==========================================================

    private void loadImages() {
        String BASE_PATH = "Images/";

        try {
            // Load all necessary images
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

            // Assign images to ImageView components
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
            System.err.println("Failed to load one or more images using File method.");
            e.printStackTrace();
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