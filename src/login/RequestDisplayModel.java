package login;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class RequestDisplayModel {
    // Required properties for data display and actions
    private final RequestModel requestObject;
    private final String userName;
    private final String equipmentName;
    private final int quantityRequested;
    private final String status;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructor: Takes the raw data object and the fetched equipment name
    public RequestDisplayModel(RequestModel request, String userName, String equipmentName) {
        this.requestObject = request;
        this.userName = userName;
        this.equipmentName = equipmentName;

        // FIX: Use the confirmed getter for requested quantity
        this.quantityRequested = request.getQuantityRequested();
        this.status = request.getStatus();
    }

    // --- Standard Getters for TableView CellValueFactory ---

    public int getId() { return requestObject.getId(); }
    public String getEquipmentName() { return equipmentName; }
    public String getStatus() { return status; }
    public String getUserName() { return userName; }
    public RequestModel getRequestObject() { return requestObject; }

    // --- FIXES for 'unreadable property' warnings from the Console Log ---

    /** * FIX: Supports the 'quantity' binding found in the log.
     * Required for some FXML/TableColumn definition using `<PropertyValueFactory property="quantity"/>`.
     */
    public int getQuantity() {
        return quantityRequested;
    }

    /**
     * Required by the 'quantityColumn' definition in UserNotificationController.
     */
    public int getQuantityRequested() {
        return quantityRequested;
    }

    /**
     * FIX: Supports the 'requestDate' binding found in the log.
     * Required for some FXML/TableColumn definition using `<PropertyValueFactory property="requestDate"/>`.
     */
    public String getRequestDate() {
        return getRequestDateDisplay();
    }

    // --- Getters for Date Columns defined in UserNotificationController ---

    /**
     * Required by requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDateDisplay")).
     */
    public String getRequestDateDisplay() {
        Timestamp ts = requestObject.getRequestDate();
        return (ts != null) ? ts.toLocalDateTime().format(FORMATTER) : "-";
    }

    /**
     * Required by approvalDateColumn.setCellValueFactory(new PropertyValueFactory<>("approvalDateDisplay")).
     */
    public String getApprovalDateDisplay() {
        Timestamp ts = requestObject.getActionDate();
        return (ts != null) ? ts.toLocalDateTime().format(FORMATTER) : "-";
    }
}