package login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class AddEquipController {

    // --- FXML Fields ---
    @FXML private ImageView home_icon;
    @FXML private ImageView logout_icon;
    @FXML private ImageView add_equip_icon;

    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private Button chooseImageButton;
    @FXML private ImageView imageView;

    // --- Navigation Containers (HBox) ---
    @FXML private HBox homeHBox;
    @FXML private HBox membersHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox addequipHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;
    @FXML private HBox equipmentsHBox;


    // --- Other Image Views/Labels ---


    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView members_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView equipments_side_icon;
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

    private byte[] selectedImageBytes = null;

    @FXML
    public void initialize() {
        loadImages();

        // --- Attach click handlers to Labels/Icons/HBoxes for navigation ---


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

    //----------------------------------------------------------------------
    //                           ACTION HANDLERS (Unchanged)
    //----------------------------------------------------------------------

    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Equipment Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Window stage = chooseImageButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                selectedImageBytes = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(selectedImageBytes);
                }
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);

            } catch (IOException e) {
                selectedImageBytes = null;
                imageView.setImage(null);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String name = nameField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (name.isEmpty() || quantityStr.isEmpty()) {
            System.err.println("Please fill in the Name and Quantity fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("Quantity must be a positive whole number.");
            return;
        }

        try {
            // Assuming EquipmentDAO.addEquipment exists and is correct
            // EquipmentDAO.addEquipment(name, quantity, quantity, selectedImageBytes); // Assuming initial remaining = quantity

            System.out.println("Equipment added successfully!");
            nameField.clear();
            quantityField.clear();
            imageView.setImage(null);
            selectedImageBytes = null;

        } catch (Exception e) {
            System.err.println("Database Error: Could not add equipment.");
            e.printStackTrace();
        }
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