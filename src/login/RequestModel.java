package login;

import java.sql.Timestamp;

public class RequestModel {
    // Declaring fields as private
    private int id;
    private int userId;
    private int equipmentId;
    private int quantityRequested; // Standardized field name
    private String status;
    private Timestamp requestDate;
    private Timestamp actionDate;

    // Constructor to initialize all fields
    public RequestModel(int id, int userId, int equipmentId, int quantityRequested, String status, Timestamp requestDate, Timestamp actionDate) {
        this.id = id;
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.quantityRequested = quantityRequested;
        this.status = status;
        this.requestDate = requestDate;
        this.actionDate = actionDate;
    }

    // --- Getters (Accessors) ---

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    // Corrected standardized getter method
    public int getQuantityRequested() {
        return quantityRequested;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public Timestamp getActionDate() {
        return actionDate;
    }

    // --- Setters (Mutators) ---

    // Setter for status, useful for updating the object state
    public void setStatus(String status) {
        this.status = status;
    }

    // Optional: toString for easy debugging
    @Override
    public String toString() {
        return "RequestModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", equipmentId=" + equipmentId +
                ", quantityRequested=" + quantityRequested +
                ", status='" + status + '\'' +
                ", requestDate=" + requestDate +
                ", actionDate=" + actionDate +
                '}';
    }
}