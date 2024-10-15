package aicraftgame;

public class Resource {
    private final String name;
    private int quantity;

    public Resource(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getResourceName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }
    @Override
    public String toString() {
        return name + " (" + quantity + ")";
    }


}
