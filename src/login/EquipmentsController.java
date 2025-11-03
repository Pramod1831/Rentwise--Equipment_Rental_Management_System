package login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class EquipmentsController implements Initializable {

    // ==========================================================
    // 1. FXML Fields (Sidebar/Navigation)
    // ==========================================================

    // Labels & Icons (Existing)
    @FXML private ImageView home_icon;
    @FXML private ImageView add_equip_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView members_icon;
    @FXML private ImageView logout_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView equipments_side_icon; // Represents the current page icon

    // NEW: HBox Navigation Containers
    @FXML private HBox homeHBox;
    @FXML private HBox addequipHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox membersHBox;
    @FXML private HBox equipmentsHBox; // For the current page's HBox
    @FXML private HBox notificationHBox; // MISSING: For Notifications
    @FXML private HBox logoutHBox;

    @FXML private Label add_equip_label;
    @FXML private Label members_label;
    @FXML private Label issued_label;
    @FXML private Label sideview_noti_label;
    @FXML private Label logout_label;
    @FXML private Label home_label;
    @FXML private Label equipments_side_label;

    // Core content container
    @FXML private FlowPane equipmentFlowPane;

    // Other ImageViews (Dashboard counters - not used on this page, but included)
    @FXML private ImageView equipments_dashboard_icon;
    @FXML private ImageView issued_dashboard_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEquipments();
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
    // 3. EQUIPMENT LOADING AND CARD GENERATION (Unchanged)
    // ==========================================================

    private void loadEquipments() {
        if (equipmentFlowPane == null) return;

        equipmentFlowPane.getChildren().clear();
        List<EquipmentModel> equipments = EquipmentDAO.getAllEquipments();

        for (EquipmentModel equipment : equipments) {
            VBox card = createEquipmentCard(equipment);
            equipmentFlowPane.getChildren().add(card);
        }
    }

    private VBox createEquipmentCard(EquipmentModel equipment) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.getStyleClass().addAll("equipment-card");
        card.setPadding(new Insets(10));

        // 1. Image View
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("equipment-image");

        if (equipment.getImageData() != null) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(equipment.getImageData())) {
                Image image = new Image(bis);
                imageView.setImage(image);
            } catch (IOException e) {
                System.err.println("Error loading image for ID " + equipment.getId() + ": " + e.getMessage());
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }

        // 2. Name Label
        Label nameLabel = new Label(equipment.getName());
        nameLabel.getStyleClass().add("equipment-name");

        // 3. Quantity Label
        Label quantityLabel = new Label(String.format("Total: %d | Remaining: %d",
                equipment.getQuantity(),
                equipment.getRemaining()));
        quantityLabel.getStyleClass().add("equipment-quantity");

        // 4. Buttons (Edit and Delete)
        Button editBtn = new Button("Edit Quantity");
        editBtn.getStyleClass().addAll("card-button", "edit-button");
        editBtn.setOnAction(e -> handleEditEquipment(equipment));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().addAll("card-button", "delete-button");
        deleteBtn.setOnAction(e -> handleDeleteEquipment(equipment));

        HBox buttonBox = new HBox(10, editBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("card-buttons");

        card.getChildren().addAll(imageView, nameLabel, quantityLabel, buttonBox);
        return card;
    }

    // ==========================================================
    // 4. ACTION HANDLERS: DELETE AND EDIT (Unchanged)
    // ==========================================================

    private void handleDeleteEquipment(EquipmentModel equipment) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Equipment: " + equipment.getName());
        confirmation.setContentText("Are you sure you want to delete this equipment? This will remove all stock, including the " + equipment.getIssued() + " currently issued items.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (EquipmentDAO.deleteEquipment(equipment.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment Deleted", equipment.getName() + " was successfully removed.");
                loadEquipments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Deletion Failed", "Could not delete the equipment from the database.");
            }
        }
    }

    private void handleEditEquipment(EquipmentModel equipment) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Editing Total Quantity for: " + equipment.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField quantityField = new TextField(String.valueOf(equipment.getQuantity()));

        Label issuedWarning = new Label("Currently Issued: " + equipment.getIssued() + ". New quantity must be " + equipment.getIssued() + " or higher.");
        issuedWarning.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");

        grid.add(new Label("Current Total Quantity:"), 0, 0);
        grid.add(quantityField, 1, 0);
        grid.add(issuedWarning, 0, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int newQuantity = Integer.parseInt(quantityField.getText().trim());
                    if (newQuantity < equipment.getIssued()) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Quantity", "Quantity Too Low", "The new quantity cannot be less than the currently issued quantity (" + equipment.getIssued() + ").");
                        return null;
                    }
                    return newQuantity;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Quantity Error", "Quantity must be a valid whole number.");
                    return null;
                }
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(newQuantity -> {
            if (EquipmentDAO.updateEquipmentQuantity(equipment.getId(), newQuantity)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Quantity Updated", equipment.getName() + " stock updated to " + newQuantity + ".");
                loadEquipments();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "Could not update the quantity in the database.");
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ==========================================================
    // 5. IMAGE LOADING UTILITY (Unchanged)
    // ==========================================================

    private void loadImages() {
        // ... (Image loading code is correct and unchanged)
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
            System.err.println("Failed to load one or more images.");
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