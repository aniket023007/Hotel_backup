import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Staff entity extending Person.
 * Further demonstrates Inheritance, Polymorphism, and Encapsulation.
 */
public class Staff extends Person {

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
