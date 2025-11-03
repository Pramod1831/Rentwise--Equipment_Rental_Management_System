package login;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class RequestDisplayModel {
    private final RequestModel requestObject;
    private final String userName;
    private final String equipmentName;
    private final int quantityRequested;
    private final String status;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public RequestDisplayModel(RequestModel request, String userName, String equipmentName) {
        this.requestObject = request;
        this.userName = userName;
        this.equipmentName = equipmentName;

        this.quantityRequested = request.getQuantityRequested();
        this.status = request.getStatus();
    }

    public int getId() { return requestObject.getId(); }
    public String getEquipmentName() { return equipmentName; }
    public String getStatus() { return status; }
    public String getUserName() { return userName; }
    public RequestModel getRequestObject() { return requestObject; }

    public int getQuantity() {
        return quantityRequested;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public String getRequestDate() {
        return getRequestDateDisplay();
    }

    public String getRequestDateDisplay() {
        Timestamp ts = requestObject.getRequestDate();
        return (ts != null) ? ts.toLocalDateTime().format(FORMATTER) : "-";
    }

    public String getApprovalDateDisplay() {
        Timestamp ts = requestObject.getActionDate();
        return (ts != null) ? ts.toLocalDateTime().format(FORMATTER) : "-";
    }
}