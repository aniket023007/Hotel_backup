# Grand Horizon Hotel Management System 🏨

An enterprise-grade **Hotel Management System** built strictly using **Core Java Object-Oriented Programming (OOP)** principles, integrated with **MySQL** and featuring both a modern **Luxury Web Dashboard** and a **Java Swing Desktop GUI**.

---

## 📁 Project Directory

```
c:\Users\Admin\OneDrive\Desktop\HotelManagementSystem_backup
```

---

## 🚀 How to Open and Run in VS Code

### Step 1: Open in VS Code
1. Open **Visual Studio Code**.
2. Click **File** > **Open Folder...** (or press `Ctrl + K, Ctrl + O`).
3. Browse to and select:
   ```
   c:\Users\Admin\OneDrive\Desktop\HotelManagementSystem_backup
   ```
   *(Alternatively, open PowerShell/Command Prompt and run `code c:\Users\Admin\OneDrive\Desktop\HotelManagementSystem_backup`)*

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
  * Abstract classes: `Person`, `Room`.
  * Behavioral interfaces: `Bookable`, `Billable`, `Displayable`.
  * Contract interfaces: `IHotelService`, `IGuestDAO`, `IRoomDAO`, `IBookingDAO`, `ICheckoutDAO`.
* **Encapsulation**:
  * Private member variables, strict input validation, guarded domain mutations (`room.bookRoom()`, `room.checkOut()`, `bill.recalculate()`).
  * Value object: `HotelStatistics` (immutable KPI data carrier).
* **Inheritance**:
  * `Person` $\rightarrow$ `Guest`, `Staff`.
  * `Room` $\rightarrow$ `StandardRoom`, `DeluxeRoom`, `SuiteRoom`, `FamilyRoom`.
* **Polymorphism**:
  * **Runtime Polymorphism**: Dynamic method overriding (`getAmenities()`, `getMaxOccupancy()`, `displayDetails()`).
  * **Strategy Pattern**: `BillingStrategy` polymorphically plugged into `Bill` (`StandardBillingStrategy`, `CorporateBillingStrategy`, `ExtendedStayBillingStrategy`).
  * **Compile-Time Polymorphism**: Overloaded constructors and service methods throughout.

### 2. Design Patterns
* **Factory Method Pattern**: `RoomFactory.createRoom(...)` dynamically instantiates the correct `Room` subclass based on room category.
* **Strategy Pattern**: `BillingStrategy` for polymorphic discount and tax calculations.
* **DAO (Data Access Object) Pattern**: Decouples database queries from business rules.
* **Service Layer Pattern**: `HotelService` orchestrates domain models and DAOs with Dependency Injection.
* **Object Composition**: `Booking` HAS-A `Guest` and `Room`; `Bill` HAS-A `Booking` and `BillingStrategy`.

---

## 🗄️ Database Setup & User Authentication
* **Engine**: MySQL (`localhost:3306`)
* **Database**: `hotel_management`
* **Driver**: `lib/mysql-connector-j-26.7.0.jar`

### 🔐 Database Demo Accounts (SHA-256 Hashed)
Passwords are stored in the MySQL `users` table as SHA-256 hashes. Plain text passwords are validated through Java `MessageDigest` SHA-256 in the domain model (`User.verifyPassword(...)`):

| Role | Username | Password | Stored SHA-256 Hash |
| :--- | :--- | :--- | :--- |
| **🛎️ Receptionist** | `receptionist` | `staff123` | `10176e7b7b24d317acfcf8d2064cfd2f24e154f7b5a96603077d5ef813d6a6b6` |
| **👑 Admin** | `admin` | `Admin@123` / `admin123` | `e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7` |
| **👔 Manager** | `manager` | `manager123` | `866485796cfa8d7c0cf7111640205b83076433547577511d81f8030ae99ecea5` |

