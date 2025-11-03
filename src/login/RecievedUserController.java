package login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RecievedUserController implements Initializable {

    @FXML private TableView<RequestDisplayModel> receivedTable;
    @FXML private TableColumn<RequestDisplayModel, String> requestDateColumn;
    @FXML private TableColumn<RequestDisplayModel, String> equipmentNameColumn;
    @FXML private TableColumn<RequestDisplayModel, Integer> quantityColumn;
    @FXML private TableColumn<RequestDisplayModel, String> statusColumn;
    @FXML private TableColumn<RequestDisplayModel, Void> actionsColumn;

    @FXML private HBox logoutHBox;
    @FXML private HBox homeHBox;
    @FXML private HBox issuedHBox; // Current screen's HBox
    @FXML private HBox equipmentsHBox;
    @FXML private HBox notificationHBox;

    @FXML private ImageView home_icon;
    @FXML private ImageView received_icon; // The main icon for this view
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView searchIcon;
    @FXML private ImageView logout_icon;
    @FXML private ImageView equipments_icon;
    @FXML private ImageView received_card_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;

    private int currentUserId;
    private ObservableList<RequestDisplayModel> userReceivedItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        currentUserId = SessionManager.getLoggedInUserId();
        if (currentUserId == -1) {
            System.err.println("ERROR: No active user session found. Please log in.");
            return;
        }

        loadIcons();
        setupNavigationHandlers();

        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupActionsColumn();

        loadUserReceivedItems();
        receivedTable.setItems(userReceivedItems);
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<RequestDisplayModel, Void>() {
            private final Button returnButton = new Button("Return Item");

            {
                returnButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-cursor: hand;");

                returnButton.setOnAction(event -> {
                    RequestDisplayModel item = getTableView().getItems().get(getIndex());
                    handleReturn(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RequestDisplayModel displayModel = getTableView().getItems().get(getIndex());
                    String status = displayModel.getRequestObject().getStatus();

                    if ("Approved".equals(status)) {
                        setGraphic(returnButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void handleReturn(RequestDisplayModel displayModel) {
        RequestModel request = displayModel.getRequestObject();

        boolean success = EquipmentDAO.requestReturn(request.getId());

        if (success) {
            request.setStatus("Return Pending");
            showAlert("Return Requested", "Return for " + displayModel.getEquipmentName() + " has been submitted to the Admin for final check.", Alert.AlertType.INFORMATION);

            loadUserReceivedItems();
            receivedTable.refresh();
        } else {
            showAlert("Failed", "Failed to submit return request. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void loadUserReceivedItems() {
        List<RequestModel> rawRequests = EquipmentDAO.getUserRequests(currentUserId);

        List<RequestModel> relevantRequests = rawRequests.stream()
                .filter(req -> "Approved".equals(req.getStatus()) ||
                        "Returned".equals(req.getStatus()) ||
                        "Return Pending".equals(req.getStatus()))
                .collect(Collectors.toList());

        userReceivedItems.clear();

        for (RequestModel req : relevantRequests) {
            String equipName = EquipmentDAO.getEquipmentNameById(req.getEquipmentId());
            // Assuming RequestDisplayModel requires the RequestModel object, User Name (N/A here), and Equipment Name
            userReceivedItems.add(new RequestDisplayModel(req, "N/A", equipName));
        }
    }


    private void setupNavigationHandlers() {
        if (logoutHBox != null) {
            logoutHBox.setOnMouseClicked(event -> handleLogout());
        }

        if (homeHBox != null) {
            homeHBox.setOnMouseClicked(event -> navigateTo("userdashboard.fxml", homeHBox));
        }

        if (equipmentsHBox != null) {
            equipmentsHBox.setOnMouseClicked(event -> navigateTo("user_equipments.fxml", equipmentsHBox));
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
            System.err.println("NULL POINTER: FXML file not found at resource path: " + fxmlFile + ". Check your resource path.");
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
            System.err.println("Failed to load image: " + fileName + ". Error: " + e.getMessage());
            return null;
        }
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

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleLogout() {
        System.out.println("User is attempting to log out.");
        try {
            SessionManager.clearSession();

            Connection activeConnection = DatabaseConnection.getActiveConnection();
            if (activeConnection != null) {
                activeConnection.close();
                DatabaseConnection.clearActiveConnection();
                System.out.println("Database: Active connection closed and cleared on logout.");
            }

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("login.fxml")));
            Parent root = loader.load();

            Stage stage = (Stage) logoutHBox.getScene().getWindow();
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