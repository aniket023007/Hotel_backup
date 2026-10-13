import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Guest entity extending Person.
 * Demonstrates Inheritance, Polymorphism (displayDetails override), and Encapsulation.
 */
public class Guest extends Person {

    private String address;
    private int loyaltyPoints;

    // Overloaded Constructor 1 (Backward compatible with original 3-arg constructor)
    public Guest(int id, String name, String phone) {
        this(id, name, phone, "", "");
    }

    // Overloaded Constructor 2 (Full constructor with database attributes)
    public Guest(int id, String name, String phone, String email, String address) {
        super(id, name, phone, email);
        this.address = address != null ? address.trim() : "";
        this.loyaltyPoints = 0;
    }

    // Overloaded Constructor 3 (For new guest registration before ID is assigned)
    public Guest(String name, String phone, String email, String address) {
        this(0, name, phone, email, address);
    }

    // Encapsulation: Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : "";
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }

    // Polymorphic implementation of abstract method
    @Override
    public void displayDetails() {
        System.out.println("----- Guest Profile -----");
        System.out.println("ID:       " + getId());
        System.out.println("Name:     " + getName());
        System.out.println("Phone:    " + getPhone());
        System.out.println("Email:    " + getEmail());
        System.out.println("Address:  " + address);
        System.out.println("Points:   " + loyaltyPoints);
        System.out.println("-------------------------");
    }

    // Serialization helper to convert domain object to map for web API
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("guestId", getId());
        map.put("fullName", getName());
        map.put("phone", getPhone());
        map.put("email", getEmail());
        map.put("address", address);
        map.put("loyaltyPoints", loyaltyPoints);
        return map;
    }

    @Override
    public String toString() {
        return "Guest[id=" + getId() + ", name='" + getName() + "', phone='" + getPhone() + "']";
    }
}
