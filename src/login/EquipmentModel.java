package login;

public class EquipmentModel {
    private final int id;
    private final String name;
    private final int quantity;
    private final int issued;
    private final int remaining; // Calculated value
    private final byte[] imageData;

    public EquipmentModel(int id, String name, int quantity, int issued, int remaining, byte[] imageData) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.issued = issued;
        this.remaining = remaining;
        this.imageData = imageData;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getIssued() { return issued; }

    public int getRemaining() { return remaining; }

    public byte[] getImageData() { return imageData; }

    @Override
    public String toString() {
        return name + " (Total: " + quantity + ", Issued: " + issued + ", Remaining: " + remaining + ")";
    }
}