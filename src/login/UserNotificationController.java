package login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserNotificationController implements Initializable {

    @FXML private TableView<RequestDisplayModel> notificationsTable;
    @FXML private TableColumn<RequestDisplayModel, String> equipmentNameColumn;
    @FXML private TableColumn<RequestDisplayModel, Integer> quantityColumn;
    @FXML private TableColumn<RequestDisplayModel, String> requestDateColumn;
    @FXML private TableColumn<RequestDisplayModel, String> statusColumn;
    @FXML private TableColumn<RequestDisplayModel, String> approvalDateColumn;
    @FXML private TableColumn<RequestDisplayModel, Void> actionColumn;

    @FXML private HBox homeHBox;
    @FXML private HBox equipmentsHBox;
    @FXML private HBox issuedHBox;
    @FXML private HBox notificationHBox;
    @FXML private HBox logoutHBox;

    @FXML private ImageView home_icon;
    @FXML private ImageView received_icon;
    @FXML private ImageView equipments_side_icon;
    @FXML private ImageView notification_sideview_icon;
    @FXML private ImageView notification_top_icon;
    @FXML private ImageView profile_icon;
    @FXML private ImageView searchIcon;
    @FXML private ImageView equipments_icon;
    @FXML private ImageView received_card_icon;
    @FXML private ImageView remaining_dashboard_icon;
    @FXML private ImageView pending_req_dashboard_icon;
    @FXML private ImageView logout_icon;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupActionColumn();
        loadUserNotifications();
        setupNavigationHandlers();
        loadIcons();
    }

    private void setupTableColumns() {
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
        // This maps to getQuantityRequested()
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityRequested"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // These strings map to the getters in the corrected RequestDisplayModel
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDateDisplay"));
        approvalDateColumn.setCellValueFactory(new PropertyValueFactory<>("approvalDateDisplay"));
    }

    private void loadUserNotifications() {
        int userId = SessionManager.getLoggedInUserId();
        if (userId == -1) {
            System.err.println("User not logged in. Cannot load notifications.");
            return;
        }

        List<RequestModel> requestModels = EquipmentDAO.getUserRequests(userId);
        ObservableList<RequestDisplayModel> displayList = FXCollections.observableArrayList();

        for (RequestModel model : requestModels) {
            String equipmentName = EquipmentDAO.getEquipmentNameById(model.getEquipmentId());

            displayList.add(new RequestDisplayModel(
                    model,
                    "User Request",
                    equipmentName
            ));
        }

        notificationsTable.setItems(displayList);
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<RequestDisplayModel, Void>() {
            final Button btn = new Button("Return");

            {
                btn.getStyleClass().add("return-button");
                btn.setOnAction(event -> {
                    RequestDisplayModel item = getTableView().getItems().get(getIndex());
                    handleReturnAction(item);
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RequestDisplayModel data = getTableView().getItems().get(getIndex());
                    String status = data.getStatus();

                    if ("Approved".equalsIgnoreCase(status)) {
                        btn.setText("Return (" + data.getQuantityRequested() + ")");
                        btn.setDisable(false);
                        setGraphic(btn);
                    } else if ("Return Pending".equalsIgnoreCase(status)) {
                        btn.setText("Return Pending");
                        btn.setDisable(true);
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void handleReturnAction(RequestDisplayModel request) {
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Confirm return of " + request.getQuantityRequested() + " units of " + request.getEquipmentName() + "?\nThis will be sent to Admin for approval.",
                "Confirm Return Request",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = EquipmentDAO.requestReturn(request.getId());

            if (success) {
                JOptionPane.showMessageDialog(null, "Return request submitted successfully. Waiting for Admin approval.", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to submit return request due to a database error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            loadUserNotifications();
        }
    }

    private void setupNavigationHandlers() {
        if (logoutHBox != null) logoutHBox.setOnMouseClicked(event -> handleLogout());
        if (homeHBox != null) homeHBox.setOnMouseClicked(event -> navigateTo("userdashboard.fxml", homeHBox));
        if (equipmentsHBox != null) equipmentsHBox.setOnMouseClicked(event -> navigateTo("user_equipments.fxml", equipmentsHBox));
        if (issuedHBox != null) issuedHBox.setOnMouseClicked(event -> navigateTo("received.fxml", issuedHBox));
    }

    private void handleLogout() {
        SessionManager.clearSession();
        if (logoutHBox != null) navigateTo("login.fxml", logoutHBox);
    }

    private void navigateTo(String fxmlFile, HBox sourceHBox) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Stage stage = (Stage) sourceHBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RentWise");
            stage.show();
        } catch (IOException | NullPointerException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Navigation Error: Could not load " + fxmlFile + ". Check FXML file path and contents.", "Navigation Error", JOptionPane.ERROR_MESSAGE);
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
}