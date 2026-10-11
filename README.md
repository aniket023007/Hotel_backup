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
   - **`1. Run Hotel Web Application (Default)`**: Starts the Web REST API server on port 8080.
   - **`2. Run Swing Desktop Application`**: Launches the classic receptionist desktop GUI.
   - **`3. Run OOP Validation Tests`**: Executes the 5-point automated OOP verification suite.
3. Click the green **Play (▷)** button or press **`F5`**.
4. If running the Web Application, open your browser to:
   👉 **http://localhost:8080/index.html**

#### Method B: Using 1-Click Batch Scripts
In the VS Code terminal (or by double-clicking in Windows File Explorer):
- **Web App**: Run `.\run-web.bat` (Compiles, starts server, and auto-opens browser)
- **Desktop Swing GUI**: Run `.\run-swing.bat`
- **OOP Tests**: Run `.\run-test.bat`

#### Method C: Using VS Code Terminal Command Line
```powershell
# Compile the project:
javac -cp "lib/*;src" -d bin src/*.java

# Run Web Server:
java -cp "lib/*;bin;src" Main

# Run Desktop Swing GUI:
java -cp "lib/*;bin;src" Main --swing

# Run OOP Validation Tests:
java -cp "lib/*;bin;src" Main --test
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

## 🗄️ Database Setup
* **Engine**: MySQL (`localhost:3306`)
* **Database**: `hotel_management`
* **Driver**: `lib/mysql-connector-j-26.7.0.jar`
