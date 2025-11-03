package login;

import java.sql.Timestamp;

public class RequestModel {
    private int id;
    private int userId;
    private int equipmentId;
    private int quantityRequested;
    private String status;
    private Timestamp requestDate;
    private Timestamp actionDate;

    public RequestModel(int id, int userId, int equipmentId, int quantityRequested, String status, Timestamp requestDate, Timestamp actionDate) {
        this.id = id;
        this.userId = userId;
        this.equipmentId = equipmentId;
        this.quantityRequested = quantityRequested;
        this.status = status;
        this.requestDate = requestDate;
        this.actionDate = actionDate;
    }


    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

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

    public void setStatus(String status) {
        this.status = status;
    }

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