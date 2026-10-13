import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * =============================================================================
 * PERSON DOMAIN HIERARCHY
 * Demonstrates:
 * - Abstraction: Abstract Person class defining base template for humans in the hotel
 * - Inheritance: Guest and Staff extend Person (IS-A relationship)
 * - Polymorphism: Overridden displayDetails() for Guests and Staff
 * - Encapsulation: Guarded private fields with validated accessors
 * =============================================================================
 */
public abstract class Person implements Displayable {

    private int id;
    private String name;
    private String phone;
    private String email;

    // Overloaded Constructor 1 (Compile-time Polymorphism)
    public Person(int id, String name) {
        this(id, name, "", "");
    }

    // Overloaded Constructor 2
    public Person(int id, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be null or empty.");
        }
        this.id = id;
        this.name = name.trim();
        this.phone = phone != null ? phone.trim() : "";
        this.email = email != null ? email.trim() : "";
    }

    // Encapsulation: Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Person name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    // Abstract method to be polymorphically implemented by subclasses
    @Override
    public abstract void displayDetails();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person[id=" + id + ", name='" + name + "', phone='" + phone + "', email='" + email + "']";
    }
}

/**
 * Guest entity extending Person.
 * Demonstrates Inheritance & Polymorphic displayDetails().
 */
class Guest extends Person {

    private String address;
    private int loyaltyPoints;

    public Guest(int id, String name, String phone) {
        this(id, name, phone, "", "");
    }

    public Guest(int id, String name, String phone, String email, String address) {
        super(id, name, phone, email);
        this.address = address != null ? address.trim() : "";
        this.loyaltyPoints = 0;
    }

    public Guest(String name, String phone, String email, String address) {
        this(0, name, phone, email, address);
    }

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

/**
 * Staff entity extending Person.
 * Demonstrates Inheritance & Polymorphic displayDetails().
 */
class Staff extends Person {

    private String role;
    private String department;
    private double salary;

    public Staff(int id, String name, String phone, String email, String role, String department, double salary) {
        super(id, name, phone, email);
        this.role = role != null ? role.trim() : "Receptionist";
        this.department = department != null ? department.trim() : "Front Desk";
        this.salary = Math.max(0.0, salary);
    }

    public Staff(int id, String name, String role, String department) {
        this(id, name, "", "", role, department, 0.0);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.trim() : "";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "";
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = Math.max(0.0, salary);
    }

    @Override
    public void displayDetails() {
        System.out.println("----- Staff Member -----");
        System.out.println("ID:         " + getId());
        System.out.println("Name:       " + getName());
        System.out.println("Role:       " + role);
        System.out.println("Department: " + department);
        System.out.println("Phone:      " + getPhone());
        System.out.println("------------------------");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("staffId", getId());
        map.put("name", getName());
        map.put("phone", getPhone());
        map.put("email", getEmail());
        map.put("role", role);
        map.put("department", department);
        return map;
    }
}
