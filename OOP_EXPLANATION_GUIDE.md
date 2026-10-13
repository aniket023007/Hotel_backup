# 🎓 Grand Horizon Hotel Management System
## Object-Oriented Programming (OOP) Architecture & Viva / Interview Guide

This guide is designed to help you **confidently explain the entire Java codebase** to an instructor, examiner, or interviewer. Every OOP concept maps directly to specific classes and methods in this repository.

---

## 📁 1. High-Level Folder Structure

The project is cleanly separated into two distinct layers:

```
HotelManagementSystem_backup/
├── frontend/               <-- Presentation Layer (HTML5, Vanilla CSS3, Modern JS)
│   ├── login.html          <-- Staff Authentication Portal (Clean, no demo clutter)
│   ├── index.html          <-- Receptionist & Operations Dashboard
│   ├── styles.css          <-- Luxury Grand Horizon styling & theme variables
│   ├── app.js              <-- Protected client logic, REST calls & session guard
│   └── assets/             <-- Brand logos and photography
│
├── backend/                <-- Business Logic & Data Persistence (Pure Java 21)
│   ├── src/                <-- Object-Oriented Domain Code, DAOs, Services
│   ├── lib/                <-- MySQL JDBC Driver (mysql-connector-j)
│   └── bin/                <-- Compiled Java Bytecode (.class files)
│
├── run-web.bat             <-- 1-Click launcher (Compiles & starts Web server)
├── run-swing.bat           <-- 1-Click launcher (Compiles & starts Swing Desktop GUI)
├── run-test.bat            <-- 1-Click launcher (Executes 7 OOP automated tests)
├── OOP_EXPLANATION_GUIDE.md<-- This guide
└── README.md               <-- Setup instructions & database credentials
```

---

## 🏛️ 2. The Four Pillars of OOP (With Exact Code Examples)

### 🔹 Pillar 1: Abstraction
> **Definition**: Hiding internal implementation complexities and exposing only essential contracts to the outside world.

#### 1. Abstract Classes
* **`Person.java`** (`abstract class Person`):
  * **Concept**: A generic `Person` cannot exist in hotel operations by itself—a person is either a `Guest`, `Staff`, or `User`.
  * **Contract**: Declares abstract method `public abstract void displayDetails();`, forcing all derived classes to implement how their details are presented.
* **`Room.java`** (`abstract class Room implements Bookable, Billable, Displayable`):
  * **Concept**: Encapsulates common room properties (`roomId`, `roomNumber`, `pricePerNight`, `status`).
  * **Abstract Methods**:
    ```java
    public abstract List<String> getAmenities();
    public abstract int getMaxOccupancy();
    public abstract String getRoomType();
    ```
* **`User.java`** (`abstract class User extends Person implements Authenticatable`):
  * Defines common security state and declares abstract role methods: `getPermissions()`, `hasPermission(String)`, `getRoleDescription()`.

#### 2. Interfaces (Behavioral Contracts)
* **`Bookable.java`**: Declares `bookRoom()`, `checkOut()`, `isAvailable()`.
* **`Billable.java`**: Declares `calculateTotalCost(int numberOfNights)`.
* **`Displayable.java`**: Declares `displayDetails()`.
* **`Authenticatable.java`**: Declares `verifyPassword(String)`, `getUsername()`, `getRole()`.
* **`BillingStrategy.java`**: Contract for polymorphic pricing discount calculations.
* **DAO Interfaces (`IUserDAO`, `IRoomDAO`, `IBookingDAO`, `IGuestDAO`, `ICheckoutDAO`)**: Decouple database operations from business logic.

---

### 🔹 Pillar 2: Encapsulation
> **Definition**: Bundling data (state) and methods (behavior) together while restricting direct access using `private` fields and controlled getters/setters.

#### 1. Private Fields & Validated Mutators
* In **`Room.java`**, fields like `status` and `pricePerNight` are `private`:
  ```java
  private RoomStatus status;
  private double pricePerNight;

  public void setPricePerNight(double price) {
      if (price <= 0) {
          throw new IllegalArgumentException("Price per night must be positive.");
      }
      this.pricePerNight = price;
  }
  ```

#### 2. Guarded Domain Mutations
* Instead of directly changing room status from outside, state transitions are encapsulated:
  ```java
  // In Room.java:
  public void bookRoom() {
      if (!isAvailable()) {
          throw new IllegalStateException("Room #" + roomNumber + " is not available.");
      }
      this.status = RoomStatus.BOOKED;
  }
  ```

#### 3. Security Encapsulation (SHA-256 Hashing)
* In **`User.java`**, sensitive credential comparison is strictly encapsulated inside the domain entity:
  ```java
  public boolean verifyPassword(String plainPassword) {
      if (plainPassword == null || plainPassword.isEmpty()) return false;
      String hashedInput = hashPassword(plainPassword); // Uses Java MessageDigest SHA-256
      return hashedInput.equalsIgnoreCase(this.passwordHash);
  }
  ```
  *Plain-text passwords are never stored or exposed; hashing happens inside Java's domain layer.*

---

### 🔹 Pillar 3: Inheritance
> **Definition**: The mechanism where a new class (subclass) derives attributes and behaviors from an existing class (superclass) via `extends` (IS-A relationship).

#### Hierarchy Trees in the Project:
1. **Person Hierarchy**:
   $$\text{Person} \xrightarrow{\text{extends}} \text{Guest}$$
   $$\text{Person} \xrightarrow{\text{extends}} \text{Staff}$$
   $$\text{Person} \xrightarrow{\text{extends}} \text{User} \xrightarrow{\text{extends}} \text{AdminUser}, \text{ReceptionistUser}, \text{ManagerUser}$$
   * *Code Benefit*: `Guest`, `Staff`, and `User` automatically inherit `id`, `name`, `phone`, `email` without writing them repeatedly.

2. **Room Hierarchy**:
   $$\text{Room} \xrightarrow{\text{extends}} \text{StandardRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{DeluxeRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{SuiteRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{FamilyRoom}$$
   * *Code Benefit*: Subclasses inherit room pricing, booking logic, and status lifecycle, only defining their specific amenities and occupancy rules.

---

### 🔹 Pillar 4: Polymorphism
> **Definition**: "Many forms"—the ability of an object to take on different forms or for a method call to exhibit different behavior depending on runtime context.

#### 1. Runtime Polymorphism (Dynamic Method Overriding)
* **`Room` polymorphic behavior**:
  ```java
  Room r1 = new StandardRoom("101", 1500);
  Room r2 = new SuiteRoom("301", 7500);

  // Calls the overridden method of the specific subclass at runtime:
  r1.getMaxOccupancy(); // Returns 1
  r2.getMaxOccupancy(); // Returns 4
  ```
* **Role-Based Authorization (`User` subclasses)**:
  * In `AdminUser.java`: `hasPermission("USERS_MANAGE")` returns `true`.
  * In `ReceptionistUser.java`: `hasPermission("USERS_MANAGE")` returns `false`.

#### 2. Compile-Time Polymorphism (Method Overloading)
* **Constructor Overloading** in `User.java`:
  ```java
  // Overload 1 (Full parameters):
  public User(int id, String username, String hash, String name, String role, String email, String phone)
  // Overload 2 (Minimal parameters for fast initialization):
  public User(String username, String hash, String name, String role)
  ```
* **Method Overloading** in `AuthService.java`:
  ```java
  public User login(String username, String password);
  public User login(LoginCredentials credentials); // Accepts value object
  ```

---

## 🎨 3. Design Patterns Implemented

| Design Pattern | Where It Is Used | Purpose |
| :--- | :--- | :--- |
| **Factory Method Pattern** | `RoomFactory.java`<br>`UserFactory.java` | Decouples caller from concrete constructors. Given `"Suite"`, `RoomFactory` returns a new `SuiteRoom`. Given `"Administrator"`, `UserFactory` returns an `AdminUser`. |
| **Strategy Pattern** | `BillingStrategy.java`<br>`StandardBillingStrategy.java`<br>`CorporateBillingStrategy.java`<br>`ExtendedStayBillingStrategy.java` | Encapsulates different discounting algorithms into interchangeable classes. Injected into `Bill.java`. |
| **Data Access Object (DAO)** | `UserDAO`, `RoomDAO`, `GuestDAO`, `BookingDAO`, `CheckoutDAO` | Decouples raw JDBC SQL statements (`PreparedStatement`) from business entities. |
| **Service Layer Pattern** | `HotelService.java`<br>`AuthService.java` | Coordinates transactions and business rules between DAOs and domain objects. |
| **Singleton Pattern** | `AuthService.getInstance()` | Maintains global synchronized authentication state across sessions. |
| **Object Composition** | `Booking.java`, `Bill.java` | HAS-A relationships: A `Booking` HAS-A `Guest` and `Room`; a `Bill` HAS-A `Booking` and `BillingStrategy`. |

---

## 💡 4. Top 6 Viva / Interview Questions & Ready Answers

### Q1: "Where have you used Polymorphism in your project?"
> *"I used both Dynamic (Runtime) and Static (Compile-time) Polymorphism. For Runtime Polymorphism, my `Room` abstract class declares `getMaxOccupancy()` and `getAmenities()`. When `RoomFactory` creates a `StandardRoom` or `SuiteRoom`, calling `room.getMaxOccupancy()` executes the overridden method of that specific subclass. I also used the Strategy Pattern in `Bill` where `BillingStrategy.applyDiscount()` behaves differently depending on whether `CorporateBillingStrategy` (10% discount) or `StandardBillingStrategy` (no discount) is passed. For Compile-time Polymorphism, I overloaded methods such as `AuthService.login(String, String)` and `AuthService.login(LoginCredentials)`."*

### Q2: "How is Encapsulation demonstrated?"
> *"All domain entities have `private` fields with validated accessors. Furthermore, sensitive logic is encapsulated: `User.verifyPassword(...)` encapsulates SHA-256 password hashing using Java's `MessageDigest` directly inside the entity. Plain text passwords are never stored or compared in raw form. Room booking state changes are also guarded through methods like `room.bookRoom()` rather than allowing public access to change room status directly."*

### Q3: "What is the difference between Abstraction and Encapsulation in your code?"
> *"Abstraction focuses on the outside view: `Room.java` and interfaces like `Bookable` define what a room can do (`bookRoom()`, `checkOut()`) without revealing MySQL or internal state. Encapsulation focuses on the inside: keeping fields like `pricePerNight` private and validating that prices cannot be negative before assigning them."*

### Q4: "Why did you use the Factory Method Pattern?"
> *"In `RoomFactory.java` and `UserFactory.java`, the calling classes (like `RoomDAO` or `HotelWebServer`) don't need to know the concrete subclass constructors. When a room type string like `'Deluxe'` arrives from MySQL, `RoomFactory.createRoom()` instantiates the correct `DeluxeRoom` instance."*

### Q5: "How does authentication work between the frontend and database?"
> *"When a user enters credentials on `frontend/login.html`, a JSON request is sent to Java's `HotelWebServer` at `/api/auth/login`. The server delegates to `AuthService.login()`, which queries MySQL via `UserDAO.findByUsername()`. The `User.verifyPassword()` method hashes the input with SHA-256 and compares it against the stored hash in the `users` table. If it matches, the polymorphic `User` object is returned and session is granted."*

### Q6: "Why did you create DAO interfaces?"
> *"To follow the Dependency Inversion Principle (the D in SOLID). The `HotelService` and `AuthService` depend on the abstraction (`IUserDAO`, `IRoomDAO`), not the concrete MySQL implementation. If we later switch from MySQL to PostgreSQL or MongoDB, we only write a new DAO implementation without touching the business rules."*
