# 🍔 Food Ordering & Delivery Management System
<<<<<<< HEAD
### 4th Semester Database Management Systems (DBMS) Project

An enterprise-grade, full-stack food ordering and delivery logistics management system built with **Spring Boot 4 (Java 17)**, **React 18 (Vite & Tailwind CSS)**, and **JavaFX 17**, featuring real-time **STOMP WebSockets**, **JWT Role-Based Access Control (RBAC)**, and **Dual-Database Support (Zero-Install H2 & Microsoft SQL Server)**.

---

## ⚡ Can I Run This Without SQL Server Installed?

> ### **YES! You can run the entire system immediately without installing Microsoft SQL Server.**
> 
> The project includes a built-in **H2 embedded database engine** enabled by default.
> * **Zero External Software Needed**: No SQL Server, no SSMS, and no background services required.
> * **Automatic Setup**: Hibernate automatically generates all relational tables upon application startup.
> * **Automatic Data Seeding**: `DataSeeder.java` automatically seeds **30 restaurants**, hundreds of menu items, an **administrator account**, **15 delivery riders**, and a **test customer account**.
> * **Persistent Local Storage**: Data is stored safely in the `./data/` folder and persists across backend restarts.
> * **Optional SQL Server Mode**: If you or your university instructor want to run against **Microsoft SQL Server**, simply switch the active profile with a single command line argument.

---

## 🏗️ System Architecture & Technology Stack

```
                          ┌──────────────────────────────────────┐
                          │         CLIENT APPLICATIONS          │
                          ├──────────────────┬───────────────────┤
                          │  React 18 (Web)  │  JavaFX 17 (App)  │
                          │  Vite + Tailwind │  FXML + OkHttp    │
                          └─────────┬────────┴─────────┬─────────┘
                                    │                  │
                           REST API │                  │ STOMP WebSockets
                           (JSON)   │                  │ (/ws, /topic/*)
                                    ▼                  ▼
                          ┌──────────────────────────────────────┐
                          │       SPRING BOOT 4 BACKEND          │
                          ├──────────────────────────────────────┤
                          │ • Spring Security + JWT RBAC         │
                          │ • Transactional Service Layer        │
                          │ • SimpleBroker STOMP Messaging Engine │
                          │ • Spring Data JPA + Hibernate ORM    │
                          └──────────────────┬───────────────────┘
                                             │
                                    JDBC Connection
                                             ▼
                          ┌──────────────────────────────────────┐
                          │          DATABASE ENGINE             │
                          ├──────────────────┬───────────────────┤
                          │  Embedded H2     │   MS SQL Server   │
                          │  (Zero-Install)  │   (SSMS Port      │
                          │  Default Profile │    53940 Profile) │
                          └──────────────────┴───────────────────┘
```

### Technology Breakdown
- **Backend API**: Spring Boot 4.0.6, Java 17, Spring Data JPA, Spring Security, Jakarta Validation.
- **Real-Time Communication**: Spring WebSocket with STOMP message broker over SockJS.
- **Authentication**: Stateless JSON Web Tokens (JJWT 0.11.5) with BCrypt password hashing.
- **Web Frontend**: React 18, Vite 6, Tailwind CSS, Lucide Icons, SockJS Client, STOMP.js.
- **Desktop Frontend**: JavaFX 17, FXML, OkHttp 4.12, Google Gson.
- **Documentation**: Professional PDF Architecture & Code Walkthrough Report (`Food_Ordering_System_DBMS_Documentation.pdf`).

---

## 🚀 Quickstart Guide

### Prerequisites
1. **Java Development Kit (JDK 17 or higher)** installed (`java -version`).
2. **Node.js (v18 or higher)** installed (`node -v`).

---

### Step 1: Start the Backend (Zero Installation Required)

1. Open your terminal and navigate to the `backend` folder:
   ```bash
   cd backend
   ```
2. Start the Spring Boot application using the embedded Maven wrapper:
   * **On Windows (PowerShell / Command Prompt)**:
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   * **On macOS / Linux**:
     ```bash
     ./mvnw spring-boot:run
     ```
3. The server will boot on `http://localhost:8081` (configured on 8081 to avoid conflict with other services).
   * On first boot, Hibernate will generate the database schema in `./data/food_order_db.mv.db`.
   * `DataSeeder` will automatically populate 30 restaurants, hundreds of menu items, admin, riders, and customer accounts.
   * **H2 Web Console**: You can visually explore the database in your browser at `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:file:./data/food_order_db`, User: `sa`, Password: *blank*).

---

### Step 2: Start the Web Frontend (React + Vite)

1. Open a **second terminal window** and navigate to the `frontend` folder:
   ```bash
   cd frontend
   ```
2. Install dependencies (if not already installed):
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Open your browser and go to **`http://localhost:5173`**.

---

### Step 3 (Optional): Start the Desktop Client (JavaFX)

If you wish to test the native desktop application:
1. Open a **third terminal window** and navigate to `frontend-javafx`:
   ```bash
   cd frontend-javafx
   ```
2. Launch the JavaFX application:
   ```powershell
   .\mvnw.cmd javafx:run
   ```

---

### Step 4 (Optional): Running with Microsoft SQL Server

If you wish to evaluate using a local **Microsoft SQL Server** instance:
1. Ensure SQL Server is installed, TCP/IP is enabled on port `53940` (or update port in `backend/src/main/resources/application-sqlserver.properties`), and create an empty database named `food_order_db` in SSMS.
2. Run the backend with the `sqlserver` profile:
   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
   ```

---

## 👥 Pre-Seeded Default Test Accounts

All accounts are pre-seeded on startup. You do not need to register manually unless you want to test the signup flow.

| Role | Username | Password | Accessible Views & Permissions |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `admin123` | System-wide Revenue Analytics, Customer Activity, Rider Performance, All Orders |
| **Customer** | `customer` | `customer123` | Restaurant Browsing, Shopping Cart, Checkout & Payment, Live Order Tracking |
| **Delivery Rider** | `rider_1` to `rider_15` | `password123` | Available Delivery Queue, Claim Delivery (Race-condition protected), Status Updates |
| **Restaurant Manager** | `owner_thegoldenslice`<br/>`owner_burgerforge`<br/>*(30 accounts)* | `password123` | Incoming Live Order Feed, Menu & Availability Management, Kitchen Status Updates |

> Full list of 30 restaurant accounts is available in [`credentials.txt`](file:///D:/University/4th%20semester/dbms%20project/credentials.txt).

---

## 🔒 Critical Sections & Concurrency Architecture

In Database Management Systems, handling concurrent access to shared resources without data corruption is paramount. This project implements strict concurrency controls for three primary business bottlenecks:

### 1. Atomic Order Placement (`OrderController.placeOrder`)
* **Problem**: Placing an order writes to `orders`, `order_items`, and `payments`. If a failure occurs midway, orphaned orders or unpaid items could be committed to the database.
* **Solution**: Annotated with `@Transactional(rollbackFor = Exception.class)`. The entire transaction executes under ACID isolation. If any item validation fails or price tampering is detected, all database modifications rollback automatically.
* **Server-Side Price Authority**: Item prices are read directly from `menu_items` in the database, preventing malicious clients from forging cart prices.

### 2. Race Condition in Delivery Acceptance (`RiderController.acceptOrder`)
* **Problem**: When an order reaches `READY_FOR_PICKUP`, it is broadcast to all active riders via WebSocket topic `/topic/riders/available`. If Rider A and Rider B click "Accept Delivery" simultaneously:
  1. Both threads could read `order.getRider() == null`.
  2. Both assign their respective rider entity.
  3. One rider overwrites the other (Lost Update Anomaly), resulting in double-dispatch.
* **Solution**: Guarded with `@Transactional` and thread synchronization. The order's rider slot is verified atomically:
  ```java
  if (order.getRider() != null) {
      return ResponseEntity.badRequest().body("Order has already been accepted by another rider.");
  }
  order.setRider(rider);
  order.setStatus(OrderStatus.DELIVERING);
  orderRepository.save(order);
  ```
  The first rider to commit wins the delivery; the second rider receives an immediate rejection, and WebSocket broadcasts remove the order from all competitor screens.

### 3. State Machine Progression Integrity
Orders strictly transition through a verified sequence:
$$\text{PENDING} \longrightarrow \text{PREPARING} \longrightarrow \text{READY\_FOR\_PICKUP} \longrightarrow \text{DELIVERING} \longrightarrow \text{DELIVERED}$$
Unauthorized skips (e.g. attempting to mark a `PENDING` order as `DELIVERED`) are blocked by role authorization checks (`@PreAuthorize`) and state validation.

---

## 📊 Database Schema & Relational Design

The relational database is normalized to **Third Normal Form (3NF)**:

| Table Name | Primary Key | Foreign Keys | Purpose & Key Attributes |
| :--- | :--- | :--- | :--- |
| `users` | `id` (BIGINT) | None | Core authentication & user profiles (`username`, `password`, `email`, `role`, `address`, `phone`). |
| `restaurants` | `id` (BIGINT) | `owner_id` $\rightarrow$ `users(id)` | Restaurant metadata (`name`, `description`, `rating`, `delivery_time`, `category`, `address`). |
| `menu_items` | `id` (BIGINT) | `restaurant_id` $\rightarrow$ `restaurants(id)` | Menu items with price, availability flag (`is_available`), and dish categories. |
| `orders` | `id` (BIGINT) | `user_id`, `restaurant_id`, `rider_id` | Core order ledger (`order_date`, `status`, `total_amount`). |
| `order_items` | `id` (BIGINT) | `order_id`, `menu_item_id` | Order line items storing `quantity` and immutable historical snapshot `price_at_time_of_order`. |
| `payments` | `id` (BIGINT) | `order_id` (1:1) | Payment audit trail (`amount`, `payment_method`, `status`, `transaction_id`). |

### Relational Aggregations for Analytics
The `OrderRepository` uses high-performance JPQL aggregate queries for admin intelligence:
* **Restaurant Revenue**: `GROUP BY r.name ORDER BY SUM(o.totalAmount) DESC`
* **Customer Lifetime Spending**: `GROUP BY u.username ORDER BY SUM(o.totalAmount) DESC`
* **Rider Completed Deliveries**: `WHERE o.status = 'DELIVERED' GROUP BY rd.username ORDER BY COUNT(o) DESC`

---

## 📡 Core API Endpoints

### Authentication & Profiles (`/api/v1/auth`)
* `POST /api/v1/auth/login` - Authenticate user, returns JWT and user role.
* `POST /api/v1/auth/signup` - Register a new customer account.

### Restaurants & Menus (`/api/v1/restaurants`)
* `GET /api/v1/restaurants` - List all restaurants (public).
* `GET /api/v1/restaurants/{id}` - Get restaurant details.
* `GET /api/v1/restaurants/{id}/menu` - Retrieve menu items for a restaurant.
* `GET /api/v1/restaurants/my-restaurant` - Get logged-in owner's restaurant (`ROLE_RESTAURANT`).
* `POST /api/v1/restaurants/menu` - Create new menu item (`ROLE_RESTAURANT`).
* `PUT /api/v1/restaurants/menu/{itemId}` - Update menu item details or availability (`ROLE_RESTAURANT`).
* `DELETE /api/v1/restaurants/menu/{itemId}` - Delete menu item (`ROLE_RESTAURANT`).

### Orders & Tracking (`/api/v1/orders`)
* `POST /api/v1/orders` - Place a new order with multiple items & mock payment (`ROLE_USER`).
* `GET /api/v1/orders/user` - View logged-in customer's order history (`ROLE_USER`).
* `GET /api/v1/orders/restaurant` - View incoming orders for owner's restaurant (`ROLE_RESTAURANT`).
* `PUT /api/v1/orders/{id}/restaurant-status` - Update status to PREPARING or READY_FOR_PICKUP (`ROLE_RESTAURANT`).
* `GET /api/v1/orders/admin/all` - View all system orders (`ROLE_ADMIN`).
* `PUT /api/v1/orders/admin/{id}/status` - Manually override order status (`ROLE_ADMIN`).

### Rider Logistics (`/api/v1/riders`)
* `GET /api/v1/riders/orders/available` - View orders awaiting pickup (`ROLE_RIDER`).
* `GET /api/v1/riders/orders/my-deliveries` - View orders claimed by current rider (`ROLE_RIDER`).
* `PUT /api/v1/riders/orders/{orderId}/accept` - Claim an available delivery (atomic race protection) (`ROLE_RIDER`).
* `PUT /api/v1/riders/orders/{orderId}/status` - Advance delivery status (`DELIVERING` $\rightarrow$ `DELIVERED`) (`ROLE_RIDER`).

### Business Analytics (`/api/v1/analytics`)
* `GET /api/v1/analytics/admin/revenue` - Restaurant revenue ranking (`ROLE_ADMIN`).
* `GET /api/v1/analytics/admin/customers` - Customer order frequencies and spending (`ROLE_ADMIN`).
* `GET /api/v1/analytics/admin/riders` - Rider delivery completion leaderboards (`ROLE_ADMIN`).

---

## 📁 Repository Structure

```
├── backend/                              # Spring Boot 4 Application
│   ├── src/main/java/com/foodorder/backend/
│   │   ├── config/                       # DataSeeder & WebSocket STOMP Configuration
│   │   ├── controller/                   # REST API Controllers (Order, Rider, Analytics, etc.)
│   │   ├── entity/                       # JPA Relational Entities (Order, User, Restaurant, etc.)
│   │   ├── enums/                        # Domain Enums (OrderStatus, PaymentMethod, UserRole)
│   │   ├── payload/                      # Request & Response DTOs
│   │   ├── repository/                   # Spring Data JPA Repositories with JPQL queries
│   │   └── security/                     # Spring Security, JWT AuthTokenFilter & UserDetails
│   ├── src/main/resources/
│   │   ├── application.properties        # Main configuration (Active profile: h2)
│   │   ├── application-h2.properties     # Embedded H2 database settings (Zero-install)
│   │   └── application-sqlserver.properties # Microsoft SQL Server settings
│   └── pom.xml                           # Maven dependencies (Spring Boot, H2, MSSQL, JJWT)
├── frontend/                             # React 18 Web Client
│   ├── src/
│   │   ├── context/                      # AuthContext and CartContext global state
│   │   ├── pages/                        # Role-specific dashboards (Admin, Rider, Restaurant, Customer)
│   │   ├── services/                     # Axios API client and WebSocket service
│   │   ├── App.jsx                       # Root React router and layout
│   │   └── main.jsx                      # React entry point
│   └── package.json                      # React, Vite, Tailwind CSS dependencies
├── frontend-javafx/                      # JavaFX 17 Desktop Client
│   ├── src/main/java/                    # Desktop Controllers & OkHttp ApiClient
│   └── src/main/resources/fxml/          # Declarative FXML UI layouts
├── credentials.txt                       # Pre-seeded login credentials for all roles
├── riders.txt                            # Delivery rider account list
├── Food_Ordering_System_DBMS_Documentation.pdf # Complete PDF Architectural Report
└── README.md                             # Project Documentation
=======

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![Vite](https://img.shields.io/badge/Vite-Frontend-646CFF?style=for-the-badge&logo=vite)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-38BDF8?style=for-the-badge&logo=tailwindcss)
![SQL Server](https://img.shields.io/badge/SQL_Server-Database-CC2927?style=for-the-badge&logo=microsoftsqlserver)
![JavaFX](https://img.shields.io/badge/JavaFX-Desktop-blue?style=for-the-badge)

**A complete Full Stack Food Ordering & Delivery Management System built for a Database Management Systems (DBMS) course project.**

</div>

---

# 📑 Table of Contents

- 📖 Project Overview
- 🛠 Prerequisites
- 🗄 Database Setup
- ⚙ Backend Setup
- 🌐 React Frontend Setup
- 🖥 JavaFX Desktop Setup
- 🔑 Demo Accounts
- 📂 Project Structure
- ❓ Troubleshooting

---

# 📖 Project Overview

This project consists of **three independent components**:

| Component | Technology | Purpose |
|-----------|------------|---------|
| 🔧 Backend | Spring Boot | REST API & Business Logic |
| 🌐 Web Frontend | React + Vite + Tailwind CSS | Customer, Admin & Restaurant Portal |
| 🖥 Desktop Client | JavaFX | Native Desktop Application |

---

# 🛠 Prerequisites

Install the following software before running the project.

## ☕ Java 17

Required for both the backend and JavaFX application.

Download:

- Eclipse Temurin JDK 17 (Recommended)
- Oracle JDK 17

Verify installation:

```bash
java -version
```

Expected output:

```
openjdk version "17.x.x"
```

---

## 🟢 Node.js (LTS)

Required for the React frontend.

Verify installation:

```bash
node -v
```

---

## 🗄 Microsoft SQL Server

Install:

- SQL Server Express / Developer Edition
- SQL Server Management Studio (SSMS)

---

# 🗄 Database Setup

## 1️⃣ Create Database

Open SSMS and execute:

```sql
CREATE DATABASE food_order_db;
```

---

## 2️⃣ Import Database

Execute:

```
backend/seed.sql
```

This will create all tables and insert sample data.

---

## 3️⃣ Enable TCP/IP

Open

```
SQL Server Configuration Manager
```

Navigate to

```
SQL Server Network Configuration
```

Enable

```
TCP/IP
```

Restart the SQL Server service afterwards.

> **Note**
>
> Default SQL Server port is **1433**.
>
> Your current project uses **53940**.
>
> Change whichever one is appropriate.

---

# ⚙ Backend Setup

Open

```
backend/src/main/resources/application.properties
```

Update the following:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=food_order_db;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD
>>>>>>> 3cea1a69a7ccd25eb8ccfca60e3bad94cb554219
```

---

<<<<<<< HEAD
## 📄 PDF Documentation Report

A comprehensive 4-page academic and technical report has been compiled directly into the root folder:
* **File**: [`Food_Ordering_System_DBMS_Documentation.pdf`](file:///D:/University/4th%20semester/dbms%20project/Food_Ordering_System_DBMS_Documentation.pdf)
* **Contents**: System Topology, 3NF Entity Relationship Model, JPQL Aggregation Queries, In-depth Critical Sections & Concurrency Analysis, Code Walkthrough, and Verification Instructions.

---

## 🛡️ License & Course Information
Developed as a capstone project for **CS2104: Database Management Systems (4th Semester)**. Available for academic review, demonstration, and educational purposes.
=======
## Run Backend

Navigate to:

```bash
cd backend
```

Start the server:

Windows

```bash
.\mvnw spring-boot:run
```

Mac/Linux

```bash
./mvnw spring-boot:run
```

Backend will start at:

```
http://localhost:8080
```

---

# 🌐 React Frontend

Navigate to:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Run development server:

```bash
npm run dev
```

Open:

```
http://localhost:5173
```

---

# 🖥 JavaFX Desktop Application

Navigate to:

```bash
cd frontend-javafx
```

Run:

Windows

```bash
.\mvnw javafx:run
```

Mac/Linux

```bash
./mvnw javafx:run
```

---

# 🔑 Demo Accounts

## 👨‍💼 Administrator

| Username | Password |
|----------|----------|
| admin | admin123 |

---

## 👤 Customer

| Username | Password |
|----------|----------|
| user | admin123 |

---

## 🍕 Restaurant Owners

| Restaurant | Username | Password |
|------------|----------|----------|
| The Golden Slice | owner_thegoldenslice | password123 |
| Burger Forge | owner_burgerforge | password123 |
| Sushi Master | owner_sushimaster | password123 |

More accounts are available inside:

```
credentials.txt
```

---

## 🛵 Delivery Riders

| Username | Password |
|----------|----------|
| rider_1 | password123 |
| rider_2 | password123 |

Additional rider accounts are available in:

```
riders.txt
```

---

# 📂 Project Structure

```
Food-Ordering-System
│
├── backend
│   ├── src
│   ├── seed.sql
│   └── pom.xml
│
├── frontend
│   ├── src
│   ├── public
│   └── package.json
│
├── frontend-javafx
│   ├── src
│   └── pom.xml
│
├── credentials.txt
├── riders.txt
└── README.md
```

---

# ❓ Troubleshooting

## SQL Connection Refused

✔ Make sure SQL Server is running.

✔ Enable TCP/IP.

✔ Verify username/password.

✔ Verify SQL Server port.

---

## Java Not Recognized

Install Java 17 and ensure it has been added to the system **PATH**.

Verify:

```bash
java -version
```

---

## Node Not Recognized

Reinstall Node.js LTS and verify:

```bash
node -v
```

---

## Stop Servers

Press

```
Ctrl + C
```

inside the terminal running the application.

---

# 🎉 You're Ready!

You should now have:

- ✅ SQL Server Running
- ✅ Spring Boot Backend Running
- ✅ React Frontend Running
- ✅ JavaFX Desktop Application Running

Happy Coding! 🚀

If you find this project helpful, consider giving it a ⭐ on GitHub.
>>>>>>> 3cea1a69a7ccd25eb8ccfca60e3bad94cb554219
