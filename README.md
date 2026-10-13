# Grand Horizon Hotel Management System 🏨

An enterprise-grade **Hotel Management System** built strictly using **Core Java Object-Oriented Programming (OOP)** principles, integrated with **MySQL** and featuring both a modern **Luxury Web Dashboard** and a **Java Swing Desktop GUI**.

---

## 📁 Project Directory & Clean 23-File Modular Architecture

```
c:\Users\Admin\OneDrive\Desktop\HotelManagementSystem_backup
```

The backend is cleanly structured into **23 modular source files** in `backend/src/`, each focused on a single responsibility (~25 to 250 lines each):
- **`Person.java`**: Abstract base `Person`, `Guest`, and `Staff`.
- **`Room.java`**: Abstract base `Room`, `Bookable`, `Billable`, `Displayable`, `RoomStatus`, and subclasses (`StandardRoom`, `DeluxeRoom`, `SuiteRoom`, `FamilyRoom`).
- **`RoomFactory.java`**: Factory Method Pattern for creating rooms dynamically.
- **`User.java`**: Abstract base `User`, `Authenticatable`, `AuthenticationException`, and subclasses (`AdminUser`, `ReceptionistUser`, `ManagerUser`).
- **`UserFactory.java`**: Factory Method Pattern for creating users dynamically.
- **`LoginCredentials.java`**: DTO / Value Object encapsulating login credentials.
- **`Bill.java`**: `Bill` invoice entity (HAS-A `Booking`, HAS-A `BillingStrategy`).
- **`BillingStrategy.java`**: Strategy Pattern (`BillingStrategy`, `StandardBillingStrategy`, `CorporateBillingStrategy`, `ExtendedStayBillingStrategy`).
- **`Booking.java`**: `Booking` reservation domain model & `BookingStatus` enum.
- **`HotelStatistics.java`**: DTO / Value Object for KPI Analytics reporting.
- **`DatabaseConnection.java`**: Singleton JDBC database connection manager.
- **`RoomDAO.java`**: `RoomDAO` and `IRoomDAO` interface.
- **`GuestDAO.java`**: `GuestDAO` and `IGuestDAO` interface.
- **`BookingDAO.java`**: `BookingDAO` and `IBookingDAO` interface for reservation bookings.
- **`CheckoutDAO.java`**: `CheckoutDAO` and `ICheckoutDAO` interface for checkouts and billing.
- **`UserDAO.java`**: `UserDAO` and `IUserDAO` interface.
- **`HotelService.java`**: `HotelService` and `IHotelService` business workflow facade.
- **`AuthService.java`**: `AuthService` singleton and `IAuthService` authentication facade.
- **`LoginFrame.java`**: Swing Desktop GUI Login Portal window.
- **`MainFrame.java`**: Swing Desktop GUI Receptionist Dashboard window.
- **`HotelWebServer.java`**: Multi-threaded Java HTTP Server & REST API.
- **`Main.java`**: CLI & Bootstrap entrypoint.
- **`OOPTestRunner.java`**: Automated 7-point OOP verification test runner.

---

## 🚀 How to Open and Run in VS Code

### Step 1: Open in VS Code
1. Open **Visual Studio Code**.
2. Click **File** > **Open Folder...** (or press `Ctrl + K, Ctrl + O`).
3. Browse to and select:
   ```
   c:\Users\Admin\OneDrive\Desktop\HotelManagementSystem_backup
   ```

---

### Step 2: Run Directly in VS Code (Choose any method)

#### Method A: Using VS Code "Run and Debug" (F5) ⭐ Recommended
1. Open the **Run and Debug** view on the left sidebar (`Ctrl + Shift + D`).
2. Select your desired launch profile from the dropdown at the top:
   - **`1. Run Hotel Web Application (Default)`**: Starts the Web REST API server on port 8080 and automatically opens the **Login Portal**.
   - **`2. Run Swing Desktop Application`**: Launches the classic receptionist desktop GUI login portal.
   - **`3. Run OOP Validation Tests`**: Executes the 7-point automated OOP verification suite.
3. Click the green **Play (▷)** button or press **`F5`**.
4. The **Staff Login Portal** opens automatically at:
   👉 **http://localhost:8080/login.html**

#### Method B: Using 1-Click Batch Scripts
In the VS Code terminal (or by double-clicking in Windows File Explorer):
- **Web App & Login**: Run `.\run-web.bat` (Compiles, starts server, and auto-opens login page)
- **Desktop Swing GUI**: Run `.\run-swing.bat`
- **OOP Tests**: Run `.\run-test.bat`

#### Method C: Using VS Code Terminal Command Line
```powershell
# Compile the project:
javac -cp "backend/lib/*;lib/*;backend/src" -d backend/bin backend/src/*.java

# Run Web Server (Auto-opens http://localhost:8080/login.html):
java -cp "backend/lib/*;lib/*;backend/bin" Main

# Run Desktop Swing GUI:
java -cp "backend/lib/*;lib/*;backend/bin" Main --swing

# Run OOP Validation Tests:
java -cp "backend/lib/*;lib/*;backend/bin" OOPTestRunner
```

---

## 🏛️ OOP Architecture & Design Patterns

### 1. The Four Pillars of OOP
* **Abstraction**:
  * Abstract classes: `Person`, `Room`, `User`.
  * Behavioral interfaces: `Bookable`, `Billable`, `Displayable`, `Authenticatable`.
  * Contract interfaces: `IHotelService`, `IAuthService`, `IGuestDAO`, `IRoomDAO`, `IBookingDAO`, `ICheckoutDAO`, `IUserDAO`.
* **Encapsulation**:
  * Private member variables, strict input validation, guarded domain mutations (`room.bookRoom()`, `room.checkOut()`, `bill.recalculate()`).
  * Cryptographic hashing: `User.hashPassword(...)` encapsulates SHA-256 computation inside the domain model.
  * Value objects: `LoginCredentials`, `HotelStatistics` (immutable data carriers).
* **Inheritance**:
  * `Person` $\rightarrow$ `Guest`, `Staff`, `User`.
  * `User` $\rightarrow$ `AdminUser`, `ReceptionistUser`, `ManagerUser`.
  * `Room` $\rightarrow$ `StandardRoom`, `DeluxeRoom`, `SuiteRoom`, `FamilyRoom`.
* **Polymorphism**:
  * **Runtime Polymorphism**: Dynamic method overriding (`getAmenities()`, `getMaxOccupancy()`, `displayDetails()`, `hasPermission()`).
  * **Strategy Pattern**: `BillingStrategy` polymorphically plugged into `Bill` (`StandardBillingStrategy`, `CorporateBillingStrategy`, `ExtendedStayBillingStrategy`).
  * **Compile-Time Polymorphism**: Overloaded constructors and overloaded methods (`login(username, password)` vs `login(credentials)`).

### 2. Design Patterns
* **Factory Method Pattern**: `RoomFactory.createRoom(...)` and `UserFactory.createUser(...)`.
* **Strategy Pattern**: `BillingStrategy` for polymorphic discount and tax calculations.
* **DAO (Data Access Object) Pattern**: Decouples database queries from business rules.
* **Service Layer Pattern**: `HotelService` and `AuthService` orchestrate domain models and DAOs with Dependency Injection.
* **Singleton Pattern**: `DatabaseConnection` and `AuthService.getInstance()`.
* **Object Composition**: `Booking` HAS-A `Guest` and `Room`; `Bill` HAS-A `Booking` and `BillingStrategy`.

---

## 🗄️ Database Setup & User Authentication
* **Engine**: MySQL (`localhost:3306`)
* **Database**: `hotel_management`
* **Driver**: `lib/mysql-connector-j-26.7.0.jar`

### 🔐 Database Accounts (SHA-256 Hashed)
Passwords are stored in the MySQL `users` table as SHA-256 hashes. Plain text passwords are validated through Java `MessageDigest` SHA-256 in the domain model (`User.verifyPassword(...)`):

| Role | Username | Password | Stored SHA-256 Hash |
| :--- | :--- | :--- | :--- |
| **🛎️ Receptionist** | `receptionist` | `staff123` | `10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6` |
| **👑 Admin** | `admin` | `Admin@123` / `admin123` | `e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7` |
| **👔 Manager** | `manager` | `manager123` | `866485796cfa8d7c0cf7111640205b83076433547577511d81f8030ae99ecea5` |
