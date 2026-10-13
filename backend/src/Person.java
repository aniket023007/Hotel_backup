import java.util.Objects;

/**
 * Abstract Person base class representing common attributes and behaviors
 * for humans in the hotel system (Guests, Staff).
 * Demonstrates Abstraction and Encapsulation.
 */



public abstract class Person implements Displayable {

    private int id;
    private String name;
    private String phone;
    private String email;

    // Overloaded Constructor 1
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

    // Getters and Setters (Encapsulation)
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
