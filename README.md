# 🍔 Food Ordering & Delivery Management System

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
```

---

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
