# 🎓 Grand Horizon Hotel Management System
## Object-Oriented Programming (OOP) Architecture & Viva / Interview Guide

This guide is designed to help you **confidently explain the entire Java codebase** to an instructor, examiner, or interviewer. Every OOP concept maps directly to specific classes and methods in this repository.

---

## 📁 1. High-Level Folder Structure & 23 Modular Source Files

To maintain clean code readability while avoiding excessively long monolithic files, the backend is organized into **23 bite-sized, single-responsibility files** (~25 to 250 lines each). Every file has a clear, focused purpose:

```
HotelManagementSystem_backup/
├── frontend/                     <-- Presentation Layer (HTML5, Vanilla CSS3, Modern JS)
│   ├── login.html                <-- Staff Authentication Portal (Clean, no demo clutter)
│   ├── index.html                <-- Receptionist & Operations Dashboard
│   ├── styles.css                <-- Luxury Grand Horizon styling & theme variables
│   ├── app.js                    <-- Protected client logic, REST calls & session guard
│   └── assets/                   <-- Brand logos and photography
│
├── backend/                      <-- Business Logic & Data Persistence (Pure Java 21)
│   ├── src/                      <-- 23 Clean, Focused Object-Oriented Source Files
│   │   ├── Person.java           <-- Abstraction & Inheritance (Person base, Guest, Staff)
│   │   ├── Room.java             <-- Room Base Class, Interfaces, Status & Subclasses
│   │   ├── RoomFactory.java      <-- Factory Method Pattern for Room Tiers
│   │   ├── User.java             <-- User Base Class, RBAC Roles, Authenticatable
│   │   ├── UserFactory.java      <-- Factory Method Pattern for User Roles
│   │   ├── LoginCredentials.java <-- DTO / Value Object for User Credentials
│   │   ├── Bill.java             <-- Invoice Entity (HAS-A Booking & BillingStrategy)
│   │   ├── BillingStrategy.java  <-- Strategy Pattern (Standard, Corporate, ExtendedStay)
│   │   ├── Booking.java          <-- Reservation Model (HAS-A Guest & Room, BookingStatus)
│   │   ├── HotelStatistics.java  <-- DTO / Value Object for KPI Analytics Reporting
│   │   ├── DatabaseConnection.java<-- Singleton JDBC Connection Manager
│   │   ├── RoomDAO.java          <-- Room Persistence & IRoomDAO Interface
│   │   ├── GuestDAO.java         <-- Guest Persistence & IGuestDAO Interface
│   │   ├── BookingDAO.java       <-- Room Booking Reservations & IBookingDAO
│   │   ├── CheckoutDAO.java      <-- Checkout Billing Transactions & ICheckoutDAO
│   │   ├── UserDAO.java          <-- User Persistence & IUserDAO Interface
│   │   ├── HotelService.java     <-- Core Service Layer Facade & IHotelService
│   │   ├── AuthService.java      <-- Authentication Service, Session State & IAuthService
│   │   ├── LoginFrame.java       <-- Swing Desktop Login Portal Window
│   │   ├── MainFrame.java        <-- Swing Desktop Receptionist Dashboard Window
│   │   ├── HotelWebServer.java   <-- Multi-threaded Java HTTP Server & REST API
│   │   ├── Main.java             <-- System Bootstrap Entry Point
│   │   └── OOPTestRunner.java    <-- Automated 7-Point OOP Verification Test Suite
│   ├── lib/                      <-- MySQL JDBC Driver (mysql-connector-j-8.0.33.jar)
│   └── bin/                      <-- Compiled Java Bytecode (.class files)
│
├── run-web.bat                   <-- 1-Click launcher (Compiles & starts Web server)
├── run-swing.bat                 <-- 1-Click launcher (Compiles & starts Swing Desktop GUI)
├── run-test.bat                  <-- 1-Click launcher (Executes 7 OOP automated tests)
├── OOP_EXPLANATION_GUIDE.md      <-- This guide
└── README.md                     <-- Setup instructions & database credentials
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
* In **`Room.java`**:
  * `interface Bookable`: Declares `bookRoom()`, `checkOut()`, `isAvailable()`.
  * `interface Billable`: Declares `calculateTotalCost(int numberOfNights)`.
  * `interface Displayable`: Declares `displayDetails()`.
* In **`User.java`**:
  * `interface Authenticatable`: Declares `verifyPassword(String)`, `getUsername()`, `getRole()`, `hasPermission(String)`.
* In **`BillingStrategy.java`**:
  * `interface BillingStrategy`: Contract for polymorphic pricing discount calculations.
* In DAO files (`RoomDAO.java`, `GuestDAO.java`, `BookingDAO.java`, `CheckoutDAO.java`, `UserDAO.java`):
  * `IRoomDAO`, `IGuestDAO`, `IBookingDAO`, `ICheckoutDAO`, `IUserDAO` decouple database persistence from business logic.

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

#### 4. Value Objects / DTOs
* **`LoginCredentials.java`**: Encapsulates `username` and `password` with immutable `private final` fields.
* **`HotelStatistics.java`**: Encapsulates hotel KPIs (occupancy, revenue, counts) in an immutable reporting DTO.

---

### 🔹 Pillar 3: Inheritance
> **Definition**: The mechanism where a new class (subclass) derives attributes and behaviors from an existing class (superclass) via `extends` (IS-A relationship).

#### Hierarchy Trees in the Project:
1. **Person Hierarchy (`Person.java` & `User.java`)**:
   $$\text{Person} \xrightarrow{\text{extends}} \text{Guest}$$
   $$\text{Person} \xrightarrow{\text{extends}} \text{Staff}$$
   $$\text{Person} \xrightarrow{\text{extends}} \text{User} \xrightarrow{\text{extends}} \text{AdminUser}, \text{ReceptionistUser}, \text{ManagerUser}$$
   * `Guest`, `Staff`, and `User` automatically inherit `id`, `name`, `phone`, `email` without redundant code.

2. **Room Hierarchy (`Room.java`)**:
   $$\text{Room} \xrightarrow{\text{extends}} \text{StandardRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{DeluxeRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{SuiteRoom}$$
   $$\text{Room} \xrightarrow{\text{extends}} \text{FamilyRoom}$$
   * Subclasses automatically inherit room booking logic, room number tracking, and pricing calculations while supplying room-specific amenities and occupancy limits.

---

### 🔹 Pillar 4: Polymorphism
> **Definition**: The ability for an entity to take multiple forms—allowing one interface or base class to control distinct behaviors.

#### 1. Dynamic / Runtime Polymorphism (Method Overriding)
* **`Room.java`**: Calling `room.displayDetails()` on a base `Room` reference dynamically executes the overridden method of `StandardRoom`, `DeluxeRoom`, `SuiteRoom`, or `FamilyRoom`.
* **`User.java`**: Calling `user.hasPermission("USERS_MANAGE")`:
  * Returns `true` if `user` is an `AdminUser`.
  * Returns `false` if `user` is a `ReceptionistUser`.
* **`BillingStrategy.java`**: Strategy Pattern: Calling `bill.recalculate()` uses whatever `BillingStrategy` is injected at runtime:
  * `StandardBillingStrategy`: 0% discount + 12% GST.
  * `CorporateBillingStrategy`: 10% discount + 12% GST.
  * `ExtendedStayBillingStrategy`: 15% discount for stays $\ge 5$ nights + 12% GST.

#### 2. Static / Compile-Time Polymorphism (Method Overloading)
* **Constructor Overloading** in `Room.java`, `User.java`, and `Booking.java`:
  ```java
  public User(int id, String username, String hash, String name, String role, String email, String phone) { ... }
  public User(String username, String hash, String name, String role) { ... }
  ```
* **Method Overloading** in `AuthService.java`:
  ```java
  public User login(String username, String password) throws AuthenticationException;
  public User login(LoginCredentials credentials) throws AuthenticationException;
  ```

---

## 🎨 3. Design Patterns Implemented

| Pattern | Location | Purpose & Implementation |
|---|---|---|
| **Factory Method** | `RoomFactory.java` | Decouples room instantiation from database strings: `RoomFactory.createRoom(id, num, type, price, status)`. |
| **Factory Method** | `UserFactory.java` | Instantiates polymorphic `AdminUser`, `ReceptionistUser`, or `ManagerUser` based on assigned DB role. |
| **Strategy Pattern** | `BillingStrategy.java` | Swappable financial tariff algorithms (`Standard`, `Corporate`, `ExtendedStay`) without modifying the `Bill` class (Open/Closed Principle). |
| **Singleton Pattern** | `DatabaseConnection.java` & `AuthService.java` | Single shared connection factory and synchronized active session state. |
| **Data Access Object (DAO)** | `RoomDAO.java`, `GuestDAO.java`, `BookingDAO.java`, `CheckoutDAO.java`, `UserDAO.java` | Separates business logic from low-level SQL queries and JDBC result sets. |
| **Service Layer Pattern** | `HotelService.java`, `AuthService.java` | Orchestrates end-to-end workflows (guest registration, booking validation, invoice generation). |
| **Data Transfer Object (DTO)** | `LoginCredentials.java`, `HotelStatistics.java` | Immutable objects encapsulating input credentials and analytics metrics. |

---

## 🤝 4. Object Relationships Explained

* **IS-A (Inheritance)**:
  * `Guest` IS-A `Person` (`Person.java`)
  * `SuiteRoom` IS-A `Room` (`Room.java`)
  * `AdminUser` IS-A `User` (`User.java`)
* **HAS-A (Composition)**:
  * `Booking` HAS-A `Guest` and `Booking` HAS-A `Room` (`Booking.java`): A booking cannot function without referencing both a guest entity and an assigned room entity.
  * `Bill` HAS-A `Booking` and `Bill` HAS-A `BillingStrategy` (`Bill.java`).
* **USES-A (Dependency Injection / Association)**:
  * `HotelService` USES `IRoomDAO`, `IGuestDAO`, `IBookingDAO`, and `ICheckoutDAO` (`HotelService.java`).
  * `AuthService` USES `IUserDAO` (`AuthService.java`).

---

## 🧪 5. How to Verify & Run

### 1. Run Automated OOP Unit/Integration Tests
```powershell
./run-test.bat
# or:
java -cp "backend/lib/*;lib/*;backend/bin" OOPTestRunner
```
*Outputs: 7 Passed, 0 Failed against live MySQL database.*

### 2. Run the Full Web Application
```powershell
./run-web.bat
# Access in browser at: http://localhost:8080/login.html
```

### 3. Run Swing Desktop GUI
```powershell
./run-swing.bat
# or:
java -cp "backend/lib/*;lib/*;backend/bin" Main --swing
```

---

## 💬 6. Top 5 Viva Questions & Ideal Answers

**Q1: Why did you separate RoomFactory and UserFactory into their own files?**
> *Answer*: Decoupling the Factory Method implementations into `RoomFactory.java` and `UserFactory.java` adheres directly to the Single Responsibility Principle (SRP). It isolates the object creation logic from entity domain modeling, allowing future room types or roles to be added without modifying the core entities.

**Q2: How does your project demonstrate Runtime Polymorphism?**
> *Answer*: When `roomDAO.getAllRoomEntities()` loads rooms from MySQL, it uses `RoomFactory` to return a list of `Room` base references holding instances of `StandardRoom`, `DeluxeRoom`, etc. When `room.displayDetails()` or `calculateTotalCost()` is invoked, Java dynamically resolves and runs the concrete subclass implementation at runtime.

**Q3: How are passwords secured in this project?**
> *Answer*: Passwords are never stored in plain text. In `User.java`, `User.hashPassword()` uses standard Java `MessageDigest` to compute a cryptographic SHA-256 hash. Authentication compares the computed hash of user input against the hash stored in MySQL.

**Q4: Where did you use the Strategy Design Pattern?**
> *Answer*: In `BillingStrategy.java`. The `BillingStrategy` interface decouples invoice price calculation from the `Bill` entity. Depending on guest tier or length of stay, `StandardBillingStrategy`, `CorporateBillingStrategy`, or `ExtendedStayBillingStrategy` can be plugged in dynamically without altering the `Bill` class.

**Q5: How are the responsibilities divided between BookingDAO and CheckoutDAO?**
> *Answer*: `BookingDAO.java` handles reservation bookings, room availability queries, and bookings table models. `CheckoutDAO.java` handles checkout settlement transactions, rolling back atomically if database errors occur, and instantiating finalized `Bill` invoices.
