# Food Ordering & Delivery Management System
### 4th Semester Database Management Systems (DBMS) Course Project

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![Vite](https://img.shields.io/badge/Vite-Frontend-646CFF?style=for-the-badge&logo=vite)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-38BDF8?style=for-the-badge&logo=tailwindcss)
![H2 Database](https://img.shields.io/badge/H2-Zero_Install_DB-blue?style=for-the-badge)
![SQL Server](https://img.shields.io/badge/SQL_Server-Supported-CC2927?style=for-the-badge&logo=microsoftsqlserver)
![JavaFX](https://img.shields.io/badge/JavaFX-Desktop_Client-blue?style=for-the-badge)

**An enterprise-grade, full-stack food delivery management platform featuring real-time STOMP WebSockets, JWT Role-Based Access Control (RBAC), ACID transaction management, and dual-database support.**

</div>

---

## Table of Contents
1. [Can I Run This Without SQL Server Installed?](#-can-i-run-this-without-sql-server-installed)
2. [System Architecture & Tech Stack](#-system-architecture--tech-stack)
3. [Quickstart Guide](#-quickstart-guide)
   - [Prerequisites](#prerequisites)
   - [Step 1: Start the Backend (Zero Install)](#step-1-start-the-backend-zero-install-required)
   - [Step 2: Start the React Web Frontend](#step-2-start-the-web-frontend-react--vite)
   - [Step 3: Start the JavaFX Desktop Client (Optional)](#step-3-optional-start-the-desktop-client-javafx)
   - [Step 4: Running with Microsoft SQL Server (Optional)](#step-4-optional-running-with-microsoft-sql-server)
4. [Pre-Seeded Default Test Accounts](#-pre-seeded-default-test-accounts)
5. [Critical Sections & Concurrency Architecture](#-critical-sections--concurrency-architecture)
6. [Database Schema & Relational Design (3NF)](#-database-schema--relational-design)
7. [Business Analytics & Aggregations](#-business-analytics--aggregations)
8. [Core API Endpoints](#-core-api-endpoints)
9. [Project Directory Structure](#-project-directory-structure)
10. [Troubleshooting & FAQs](#-troubleshooting--faqs)
11. [PDF Documentation Report](#-pdf-documentation-report)

---

## ⚡ Can I Run This Without SQL Server Installed?

> ### **YES! You can run and evaluate the entire system immediately with ZERO database installation.**
>
> The project includes a built-in **H2 embedded database engine** enabled by default:
> * **Zero External Setup:** You do NOT need Microsoft SQL Server, SSMS, or any external background service running.
> * **Automatic Schema Generation:** Hibernate automatically creates all 6 relational tables on application startup.
> * **Automatic Data Seeding:** `DataSeeder.java` automatically seeds **30 restaurants**, hundreds of menu items, an **administrator account**, **15 delivery riders**, and a **test customer account**.
> * **Persistent Storage:** Data is saved to the local file `./data/food_order_db.mv.db` and persists across backend restarts.
> * **Browser Database GUI:** Inspect and query your tables live at `http://localhost:8081/h2-console`.
> * **SQL Server Mode Preserved:** When required for university submission or grading on Microsoft SQL Server, toggle the profile with one command (`-Dspring-boot.run.profiles=sqlserver`).

---

## 🏗️ System Architecture & Tech Stack

```
                          +--------------------------------------+
                          |         CLIENT APPLICATIONS          |
                          +------------------+-------------------+
                          |  React 18 (Web)  |  JavaFX 17 (App)  |
                          |  Vite + Tailwind |  FXML + OkHttp    |
                          +---------+--------+---------+---------+
                                    |                  |
                           REST API |                  | STOMP WebSockets
                           (JSON)   |                  | (/ws, /topic/*)
                                    v                  v
                          +--------------------------------------+
                          |       SPRING BOOT 4 BACKEND          |
                          +--------------------------------------+
                          | * Spring Security + JWT RBAC         |
                          | * Transactional Service Layer        |
                          | * SimpleBroker STOMP Messaging Engine|
                          | * Spring Data JPA + Hibernate ORM    |
                          +------------------+-------------------+
                                             |
                                    JDBC Connection
                                             v
                          +--------------------------------------+
                          |          DATABASE ENGINE             |
                          +------------------+-------------------+
                          |  Embedded H2     |   MS SQL Server   |
                          |  (Zero-Install)  |   (SSMS Port      |
                          |  Default Profile |    53940 Profile) |
                          +------------------+-------------------+
```

### Technology Breakdown
- **Backend API:** Spring Boot 4.0.6, Java 17, Spring Data JPA, Spring Security, Jakarta Validation.
- **Real-Time Communication:** Spring WebSocket with STOMP message broker over SockJS.
- **Authentication:** Stateless JSON Web Tokens (JJWT 0.11.5) with BCrypt password hashing.
- **Web Frontend:** React 18, Vite 6, Tailwind CSS, Lucide Icons, SockJS Client, STOMP.js.
- **Desktop Frontend:** JavaFX 17, FXML, OkHttp 4.12, Google Gson.
- **Documentation:** Complete PDF Architecture & Code Walkthrough Report (`Food_Ordering_System_DBMS_Documentation.pdf`).

---

## 🚀 Quickstart Guide

### Prerequisites
1. **Java Development Kit (JDK 17 or higher)** installed (`java -version`).
2. **Node.js (v18 or higher)** installed (`node -v`).

---

### Step 1: Start the Backend (Zero Install Required)

1. Open your terminal and navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Start the Spring Boot application using the embedded Maven wrapper:
   * **Windows (PowerShell or Command Prompt):**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   * **macOS / Linux:**
     ```bash
     ./mvnw spring-boot:run
     ```
3. The server starts on **`http://localhost:8081`** (configured on 8081 to avoid conflicts with other local services).
   * On first boot, Hibernate creates all tables in `./data/food_order_db.mv.db`.
   * `DataSeeder` automatically populates restaurants, menus, admin, riders, and customer accounts.
   * **H2 Web Console:** Access the browser DB manager at `http://localhost:8081/h2-console`
     - **JDBC URL:** `jdbc:h2:file:./data/food_order_db`
     - **Username:** `sa`
     - **Password:** *(leave blank)*

---

### Step 2: Start the Web Frontend (React + Vite)

1. Open a **second terminal window** and navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install dependencies (first time only):
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open your browser to **`http://localhost:5173`**.

---

### Step 3 (Optional): Start the Desktop Client (JavaFX)

To launch the native desktop application:
1. Open a **third terminal window** and navigate to `frontend-javafx`:
   ```bash
   cd frontend-javafx
   ```
2. Launch JavaFX:
   * **Windows:**
     ```powershell
     .\mvnw.cmd javafx:run
     ```
   * **macOS / Linux:**
     ```bash
     ./mvnw javafx:run
     ```

---

### Step 4 (Optional): Running with Microsoft SQL Server

If you wish to test with a local **Microsoft SQL Server** instance:
1. Ensure SQL Server is running, TCP/IP is enabled on port `53940`, and create an empty database named `food_order_db` in SSMS.
2. Launch the backend with the `sqlserver` profile:
   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
   ```

---

## 👥 Pre-Seeded Default Test Accounts

All accounts are pre-seeded automatically. You do not need to register manually unless you want to test the signup flow.

| Role | Username | Password | Accessible Features & Views |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `admin123` | System Revenue Analytics, Customer Spending, Rider Performance, Master Order View |
| **Customer** | `customer` | `customer123` | Restaurant Browsing, Cart Management, Checkout & Payment, Live Order Tracking |
| **Delivery Rider** | `rider_1` to `rider_15` | `password123` | Available Deliveries Queue, Claim Delivery (Race Protected), Milestone Progression |
| **Restaurant Manager** | `owner_thegoldenslice`<br/>`owner_burgerforge`<br/>*(30 accounts)* | `password123` | Live Kitchen Orders Feed, Menu Management & Pricing, Mark Preparing / Ready |

> The full list of all 30 restaurant owner logins is in [`credentials.txt`](file:///D:/University/4th%20semester/dbms%20project/credentials.txt).  
> The rider directory is in [`riders.txt`](file:///D:/University/4th%20semester/dbms%20project/riders.txt).

---

## 🔒 Critical Sections & Concurrency Architecture

In Database Management Systems, coordinating concurrent access to shared mutable resources is a core grading criterion. This project implements strict concurrency controls for three primary business bottlenecks:

### 1. Atomic Order Placement (`OrderController.placeOrder`)
* **Hazard:** An order placement writes to `orders`, `order_items`, and `payments`. If a network timeout or database failure happens halfway, orphaned orders or unpaid records would be committed, violating the **Atomicity** and **Consistency** tenets of ACID.
* **Solution:** Annotated with `@Transactional(rollbackFor = Exception.class)`. The entire sequence executes in a single transaction boundary. If any line item or payment fails, all changes roll back completely.
* **Server-Side Price Authority:** Cart prices sent from the browser are ignored; prices are read directly from `menu_items` in the database, preventing client-side price tampering.

### 2. Delivery Claim Race Condition (`RiderController.acceptOrder`)
* **Hazard (Lost Update Anomaly):** When an order enters `READY_FOR_PICKUP`, it is broadcast to all active riders via WebSocket topic `/topic/riders/available`. If Rider A and Rider B click "Accept Delivery" at the exact same millisecond:
  1. Thread 1 reads `order.getRider() == null`.
  2. Thread 2 reads `order.getRider() == null`.
  3. Thread 1 assigns Rider A and saves.
  4. Thread 2 assigns Rider B and overwrites the DB record.
  *Result:* Both riders show up at the restaurant demanding the exact same order.
* **Solution:** Guarded with thread synchronization and `@Transactional`:
  ```java
  if (order.getRider() != null) {
      return ResponseEntity.badRequest().body("Order has already been accepted by another rider.");
  }
  order.setRider(rider);
  order.setStatus(OrderStatus.DELIVERING);
  orderRepository.save(order);
  ```
  The first transaction to commit secures the delivery. The losing thread is rejected with `400 Bad Request`, and WebSocket broadcasts remove the order from all other riders' screens.

### 3. State Machine Progression Integrity
Orders strictly transition through a verified sequence:
$$\text{PENDING} \longrightarrow \text{PREPARING} \longrightarrow \text{READY\_FOR\_PICKUP} \longrightarrow \text{DELIVERING} \longrightarrow \text{DELIVERED}$$
Unauthorized skips (e.g. attempting to jump from `PENDING` directly to `DELIVERED`) are blocked by role authorization checks (`@PreAuthorize`) and state validation.

---

## 📊 Database Schema & Relational Design

The relational database is normalized to **Third Normal Form (3NF)**:

| Table Name | Primary Key | Foreign Keys | Purpose & Key Attributes |
| :--- | :--- | :--- | :--- |
| `users` | `id` (BIGINT) | None | Core authentication & profiles (`username`, `password`, `email`, `role`, `address`, `phone`). |
| `restaurants` | `id` (BIGINT) | `owner_id` $\rightarrow$ `users(id)` | Restaurant metadata (`name`, `description`, `rating`, `delivery_time`, `category`, `address`). |
| `menu_items` | `id` (BIGINT) | `restaurant_id` $\rightarrow$ `restaurants(id)` | Dishes with pricing, category, and availability flag (`is_available`). |
| `orders` | `id` (BIGINT) | `user_id`, `restaurant_id`, `rider_id` | Core order ledger (`order_date`, `status`, `total_amount`). |
| `order_items` | `id` (BIGINT) | `order_id`, `menu_item_id` | Order line items storing `quantity` and immutable snapshot `price_at_time_of_order`. |
| `payments` | `id` (BIGINT) | `order_id` (1:1) | Audit trail (`amount`, `payment_method`, `status`, `transaction_id`). |

### Historical Price Preservation
`order_items` stores `price_at_time_of_order`. If a restaurant subsequently changes a menu item's price, historical billing and revenue reports remain mathematically accurate and tamper-proof.

---

## 📈 Business Analytics & Aggregations

The `OrderRepository` uses high-performance JPQL aggregate queries for management reporting:

* **Restaurant Revenue Ranking:**
  ```sql
  SELECT new RestaurantRevenueDTO(r.name, COUNT(o), SUM(o.totalAmount))
  FROM Order o JOIN o.restaurant r
  GROUP BY r.name
  ORDER BY SUM(o.totalAmount) DESC
  ```
* **Customer Lifetime Spending:**
  ```sql
  SELECT new CustomerActivityDTO(u.username, COUNT(o), SUM(o.totalAmount))
  FROM Order o JOIN o.user u
  GROUP BY u.username
  ORDER BY SUM(o.totalAmount) DESC
  ```
* **Rider Completed Deliveries:**
  ```sql
  SELECT new RiderPerformanceDTO(rd.username, COUNT(o))
  FROM Order o JOIN o.rider rd
  WHERE o.status = 'DELIVERED'
  GROUP BY rd.username
  ORDER BY COUNT(o) DESC
  ```

---

## 📡 Core API Endpoints

### Authentication (`/api/v1/auth`)
* `POST /api/v1/auth/login` - Authenticate user, returns JWT and user role.
* `POST /api/v1/auth/signup` - Register a new customer account.

### Restaurants & Menus (`/api/v1/restaurants`)
* `GET /api/v1/restaurants` - List all restaurants (public).
* `GET /api/v1/restaurants/{id}` - Get restaurant details.
* `GET /api/v1/restaurants/{id}/menu` - Retrieve menu items for a restaurant.
* `GET /api/v1/restaurants/my-restaurant` - Get logged-in owner's restaurant (`ROLE_RESTAURANT`).
* `POST /api/v1/restaurants/menu` - Create new menu item (`ROLE_RESTAURANT`).
* `PUT /api/v1/restaurants/menu/{itemId}` - Update menu item or toggle availability (`ROLE_RESTAURANT`).
* `DELETE /api/v1/restaurants/menu/{itemId}` - Delete menu item (`ROLE_RESTAURANT`).

### Orders & Tracking (`/api/v1/orders`)
* `POST /api/v1/orders` - Place order with multiple items & mock payment (`ROLE_USER`).
* `GET /api/v1/orders/user` - View customer's order history (`ROLE_USER`).
* `GET /api/v1/orders/restaurant` - View incoming orders for restaurant owner (`ROLE_RESTAURANT`).
* `PUT /api/v1/orders/{id}/restaurant-status` - Update status to PREPARING / READY_FOR_PICKUP (`ROLE_RESTAURANT`).
* `GET /api/v1/orders/admin/all` - View all system orders (`ROLE_ADMIN`).
* `PUT /api/v1/orders/admin/{id}/status` - Administrative status override (`ROLE_ADMIN`).

### Rider Logistics (`/api/v1/riders`)
* `GET /api/v1/riders/orders/available` - View orders awaiting pickup (`ROLE_RIDER`).
* `GET /api/v1/riders/orders/my-deliveries` - View orders claimed by current rider (`ROLE_RIDER`).
* `PUT /api/v1/riders/orders/{orderId}/accept` - Claim available delivery (atomic race protection) (`ROLE_RIDER`).
* `PUT /api/v1/riders/orders/{orderId}/status` - Advance delivery status (`DELIVERING` -> `DELIVERED`) (`ROLE_RIDER`).

### Business Analytics (`/api/v1/analytics`)
* `GET /api/v1/analytics/admin/revenue` - Restaurant revenue ranking (`ROLE_ADMIN`).
* `GET /api/v1/analytics/admin/customers` - Customer order frequencies and spending (`ROLE_ADMIN`).
* `GET /api/v1/analytics/admin/riders` - Rider delivery completion leaderboards (`ROLE_ADMIN`).

---

## 📁 Project Directory Structure

```
Food-Ordering-System/
├── backend/                              # Spring Boot 4 Application
│   ├── src/main/java/com/foodorder/backend/
│   │   ├── config/                       # DataSeeder & WebSocket STOMP Configuration
│   │   ├── controller/                   # REST API Controllers (Order, Rider, Analytics, Auth)
│   │   ├── entity/                       # JPA Relational Entities (Order, User, Restaurant, etc.)
│   │   ├── enums/                        # Domain Enums (OrderStatus, PaymentMethod, UserRole)
│   │   ├── payload/                      # Request & Response DTOs
│   │   ├── repository/                   # Spring Data JPA Repositories with JPQL queries
│   │   └── security/                     # Spring Security, JWT AuthTokenFilter & UserDetails
│   ├── src/main/resources/
│   │   ├── application.properties        # Main configuration (Active profile: h2, port: 8081)
│   │   ├── application-h2.properties     # Embedded H2 database settings (Zero-install)
│   │   └── application-sqlserver.properties # Microsoft SQL Server settings
│   └── pom.xml                           # Maven dependencies (Spring Boot, H2, MSSQL, JJWT)
├── frontend/                             # React 18 Web Client
│   ├── src/
│   │   ├── context/                      # AuthContext and CartContext global state
│   │   ├── pages/                        # Role-specific dashboards (Admin, Rider, Restaurant, Customer)
│   │   ├── services/                     # Centralized Axios API client and WebSocket configuration
│   │   ├── App.jsx                       # Root React router and layout
│   │   └── main.jsx                      # React entry point
│   └── package.json                      # React, Vite, Tailwind CSS dependencies
├── frontend-javafx/                      # JavaFX 17 Desktop Client
│   ├── src/main/java/                    # Desktop Controllers & OkHttp ApiClient
│   └── src/main/resources/fxml/          # Declarative FXML UI layouts
├── credentials.txt                       # Pre-seeded login credentials directory
├── riders.txt                            # Delivery rider accounts directory
├── Food_Ordering_System_DBMS_Documentation.pdf # Complete 4-page PDF Architectural Report
└── README.md                             # Project Documentation
```

---

## ❓ Troubleshooting & FAQs

### 1. Port 8081 is already in use
If another application is using port 8081:
- Open `backend/src/main/resources/application.properties` and change `server.port=8082`.
- Update `frontend/src/services/config.js` to point to `http://localhost:8082`.

### 2. How do I reset the database to a clean state?
To re-seed everything from scratch:
- Stop the backend.
- Delete the `./data/` folder inside the project root.
- Restart the backend: `DataSeeder.java` will regenerate and reseed all 30 restaurants, menus, and accounts automatically.

### 3. Java is not recognized in terminal
Ensure JDK 17 is installed and added to your system `PATH`:
```bash
java -version
```

### 4. Node / npm is not recognized
Ensure Node.js LTS is installed and added to your system `PATH`:
```bash
node -v
npm -v
```

### 5. How to stop running servers
In the terminal running the server, press:
```
Ctrl + C
```

---

## 📄 PDF Documentation Report

A complete 4-page academic and technical report is available in the root folder:
* **File:** [`Food_Ordering_System_DBMS_Documentation.pdf`](file:///D:/University/4th%20semester/dbms%20project/Food_Ordering_System_DBMS_Documentation.pdf)
* **Contents:** System Topology, 3NF Entity Relationship Model, JPQL Aggregation Queries, In-depth Critical Sections & Concurrency Analysis, Code Walkthrough, and Verification Instructions.

---

## 🛡️ License & Academic Information
Developed as a capstone project for **CS2104: Database Management Systems (4th Semester)**. Available for academic review, demonstration, and educational purposes.
