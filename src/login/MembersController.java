package login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MembersController {

    // ==========================================================
    // 1. TableView FXML Fields
    // ==========================================================
    @FXML private TableView<MemberModel> membersTable;
    @FXML private TableColumn<MemberModel, String> idColumn;
    @FXML private TableColumn<MemberModel, String> firstNameColumn;
    @FXML private TableColumn<MemberModel, String> lastNameColumn;
    @FXML private TableColumn<MemberModel, String> usernameColumn;
    @FXML private TableColumn<MemberModel, Void> actionsColumn;


    // ==========================================================
    // 2. Navigation FXML Fields (Labels/Icons)
    // ==========================================================
    @FXML private Label home_label;
    @FXML private Label add_equip_label;
    @FXML private Label members_label;
    @FXML private Label issued_label;
    @FXML private Label equipments_side_label;
    @FXML private Label logout_label;
    @FXML private Label sideview_noti_label;

    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView home_icon;
    @FXML private ImageView add_equip_icon;
    @FXML private ImageView issued_icon;
    @FXML private ImageView members_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView logout_icon;

    // Dashboard Icons (for consistency, even if not used here)
    @FXML private ImageView equipments_dashboard_icon;
    @FXML private ImageView issued_dashboard_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;

    // NEW: HBox Navigation Containers
    @FXML private HBox homeHBox;
    @FXML private HBox addequipHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox equipmentsHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;
    @FXML private HBox members_Hbox; // Already present


    @FXML
    public void initialize() {
        setupTableColumns();
        loadMemberData();
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
    // 4. DATA LOADING AND TABLE SETUP
    // ==========================================================

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        if (actionsColumn != null) {
            Callback<TableColumn<MemberModel, Void>, TableCell<MemberModel, Void>> cellFactory = param -> new TableCell<>() {

                private final Button deleteButton = new Button("Delete");
                private final Button editButton = new Button("Edit");

                {
                    deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 3 8 3 8;");
                    editButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 10px; -fx-padding: 3 8 3 8;");

                    deleteButton.setOnAction(event -> {
                        MemberModel member = getTableView().getItems().get(getIndex());
                        handleDeleteMember(member);
                    });

                    editButton.setOnAction(event -> {
                        MemberModel member = getTableView().getItems().get(getIndex());
                        handleEditMember(member);
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox pane = new HBox(5, editButton, deleteButton);
                        pane.setAlignment(Pos.CENTER_LEFT);
                        setGraphic(pane);
                    }
                }
            };
            actionsColumn.setCellFactory(cellFactory);
        } else {
            System.err.println("CRITICAL: actionsColumn is null. Check fx:id in Members.fxml.");
        }
    }

    private void loadMemberData() {
        try {
            List<MemberModel> memberList = MemberDAO.getAllMembers();
            ObservableList<MemberModel> observableMembers = FXCollections.observableArrayList(memberList);
            membersTable.setItems(observableMembers);

            if (memberList.isEmpty()) {
                System.out.println("No members found.");
            } else {
                System.out.println("Successfully loaded " + memberList.size() + " members.");
            }
        } catch (Exception e) {
            System.err.println("Error loading member data into the table.");
            e.printStackTrace();
        }
    }


    // ==========================================================
    // 5. ACTION HANDLERS: DELETE AND EDIT
    // ==========================================================

    private void handleDeleteMember(MemberModel member) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Member: " + member.getFirstName() + " " + member.getLastName());
        confirmation.setContentText("Are you sure you want to delete this member? This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (MemberDAO.deleteMember(member.getMemberId())) {
                System.out.println("Member deleted: " + member.getMemberId());
                loadMemberData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member Deleted", "The member was successfully removed.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Deletion Failed", "Could not delete the member from the database.");
            }
        }
    }

    private void handleEditMember(MemberModel member) {
        Dialog<MemberModel> dialog = new Dialog<>();
        dialog.setTitle("Edit Member");
        dialog.setHeaderText("Editing Member ID: " + member.getMemberId());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField(member.getFirstName());
        TextField lastNameField = new TextField(member.getLastName());
        TextField usernameField = new TextField(member.getUsername());

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                // Return a new MemberModel with the updated data
                return new MemberModel(
                        member.getMemberId(),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        usernameField.getText()
                );
            }
            return null;
        });

        Optional<MemberModel> result = dialog.showAndWait();

        result.ifPresent(updatedMember -> {
            if (MemberDAO.updateMember(updatedMember)) {
                System.out.println("Member updated: " + updatedMember.getMemberId());
                loadMemberData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member Updated", "The member's details were successfully saved.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Update Failed", "Could not update the member in the database.");
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
    // 6. IMAGE LOADING UTILITY (Unchanged)
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
            Image imgEquipments = loadImageFromFile(BASE_PATH + "equipments_side_icon.png");
            Image imgLogout = loadImageFromFile(BASE_PATH + "logout.png");
            Image imgEquipmentDashboard = loadImageFromFile(BASE_PATH + "sports.png");
            Image imgIssuedDashboard = loadImageFromFile(BASE_PATH + "issued_admin.png");
            Image imgRemainingDashboard = loadImageFromFile(BASE_PATH + "remaining_admin.png");
            Image imgProductsDashboard = loadImageFromFile(BASE_PATH + "products.png");

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