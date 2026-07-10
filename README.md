# 🍔 Food Ordering & Delivery Management System (DBMS Project)

Welcome to the **Food Ordering & Delivery Management System**! This is a complete, full-stack application built for a 4th-semester Database Management Systems (DBMS) project. 

The project consists of three main parts:
1. **Backend**: A Spring Boot API connecting to a Microsoft SQL Server database.
2. **Web Frontend**: A modern, interactive web application built with React, Vite, and Tailwind CSS.
3. **Desktop Frontend**: A native desktop application built with JavaFX.

This guide is written specifically for beginners. If you or your friend have never run an application like this before, just follow these step-by-step instructions carefully!

---

## 🛠️ Step 0: Prerequisites (Installation Checklist)

Before running the application, you need to install a few essential tools on your computer. Download and install them in this order:

### 1. Java Development Kit (JDK 17)
The backend and the desktop app run on Java 17.
* **Download**: Go to [Eclipse Temurin JDK 17 (LTS)](https://adoptium.net/temurin/releases/?version=17) or [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads/#java17).
* **Installation**: Download the installer for Windows (`.msi` or `.exe`), run it, and complete the installation.
* **Verification**: Open Command Prompt (press `Win + R`, type `cmd`, and hit Enter) and type:
  ```bash
  java -version
  ```
  It should output `openjdk version "17.x.x"` or similar.

### 2. Node.js & npm (for the React Frontend)
Node.js is required to install and run the React web frontend.
* **Download**: Go to [Node.js Official Website](https://nodejs.org/) and download the **LTS (Long Term Support)** version.
* **Installation**: Run the downloaded installer and accept all default options.
* **Verification**: Open Command Prompt and type:
  ```bash
  node -v
  ```
  It should output a version number like `v20.x.x` or similar.

### 3. Microsoft SQL Server & SSMS
This project uses **Microsoft SQL Server** as its database.
* **SQL Server Developer/Express**: Download and install [SQL Server Express (Free)](https://www.microsoft.com/en-us/sql-server/sql-server-downloads). Choose the **Basic** installation type.
* **SQL Server Management Studio (SSMS)**: Download and install [SSMS (Graphical Interface)](https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms) to manage the database easily.

---

## 🗄️ Step 1: Set up the Database

The backend needs a database named `food_order_db` running on your local SQL Server.

### A. Enable TCP/IP on SQL Server (Crucial!)
By default, SQL Server might not allow connections from Java applications. You must enable TCP/IP:
1. Press `Win + S` and search for **SQL Server Configuration Manager** (if you can't find it, look for `Computer Management` -> `Services and Applications` -> `SQL Server Configuration Manager`).
2. Expand **SQL Server Network Configuration** -> click **Protocols for MSSQLSERVER** (or your server name).
3. Right-click **TCP/IP** and choose **Enable**.
4. Right-click **TCP/IP** again, select **Properties**, go to the **IP Addresses** tab, scroll all the way down to **IPAll**, and ensure:
   * **TCP Dynamic Ports** is either blank or matches your custom port.
   * **TCP Port** is set to `1433` (the standard port) or your desired port.
   * *Note: The current backend configuration expects the port `53940`. If you use the standard port `1433`, you will modify this in Step 2.*
5. Go to **SQL Server Services** in the left sidebar, right-click **SQL Server (MSSQLSERVER)**, and click **Restart**.

### B. Create the Database & Seed Initial Data
1. Open **SQL Server Management Studio (SSMS)**.
2. Connect to your database server (typically using **Windows Authentication** or `sa` login).
3. Click **New Query** at the top.
4. Run the following command to create the database:
   ```sql
   CREATE DATABASE food_order_db;
   ```
   Select this text and click **Execute** (or press `F5`).
5. Close the query, and select the newly created `food_order_db` from the dropdown list on the top-left toolbar (or run `USE food_order_db;` in a query window).
6. File-open and copy the SQL scripts from `backend/seed.sql` into a new query window and click **Execute**. This will populate the tables with sample restaurants and menus!

---

## ⚙️ Step 2: Configure & Run the Backend API

The backend is a Spring Boot application. It connects to the SQL database and provides data to both frontends.

### A. Update Database Credentials
Before running, tell the backend how to log into **your** local SQL Server:
1. Open the folder `backend/src/main/resources/` and open the file `application.properties` in a text editor (like Notepad, VS Code, or Notepad++).
2. Look for these lines:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:53940;databaseName=food_order_db;encrypt=true;trustServerCertificate=true;
   spring.datasource.username=sa
   spring.datasource.password=Alaqmar53
   ```
3. Update them to match your SQL Server settings:
   * **Port**: Change `53940` to whatever port your SQL Server is running on (usually `1433`).
   * **Username**: Set your SQL Server username (standard is `sa`).
   * **Password**: Set your SQL Server password (replace `Alaqmar53` with your SQL password).
4. Save and close the file.

### B. Start the Backend
1. Open **Command Prompt** or **PowerShell**.
2. Navigate to your project's `backend` folder. You can do this by typing:
   ```cmd
   cd "C:\path\to\your\project\backend"
   ```
   *(Hint: You can also open the `backend` folder in Windows File Explorer, click on the address bar at the top, type `cmd`, and press Enter. This automatically opens Command Prompt in that folder!)*
3. Run the following command to start the Spring Boot application:
   ```cmd
   .\mvnw spring-boot:run
   ```
   *(On Mac or Linux, use `./mvnw spring-boot:run`)*
4. Wait for the compilation. Once you see a message like **`Started BackendApplication in X.XXX seconds`**, the backend is successfully running on **`http://localhost:8080`**! Keep this terminal window open.

---

## 🌐 Step 3: Run the React Web Frontend

The React frontend provides a web portal for Customers, Restaurant Owners, and Delivery Riders.

1. Open a **new** Command Prompt window.
2. Navigate to the `frontend` folder:
   ```cmd
   cd "C:\path\to\your\project\frontend"
   ```
3. Install the required Node packages (only needed the first time you run the project):
   ```cmd
   npm install
   ```
4. Start the Vite development server:
   ```cmd
   npm run dev
   ```
5. The terminal will display a local address, usually:
   **`http://localhost:5173`**
6. Open your favorite web browser and go to `http://localhost:5173`. You can now interact with the gorgeous web app! Keep this terminal window open as well.

---

## 🖥️ Step 4: Run the JavaFX Desktop Client (Optional)

The project also comes with a desktop-based frontend built in JavaFX, which can be useful for managing orders or looking at the interface in a desktop window format.

1. Open a **third** Command Prompt window.
2. Navigate to the `frontend-javafx` folder:
   ```cmd
   cd "C:\path\to\your\project\frontend-javafx"
   ```
3. Run the desktop application using its Maven wrapper:
   ```cmd
   .\mvnw javafx:run
   ```
4. A beautiful standalone window application should open up on your desktop!

---

## 🔑 Default Accounts & Credentials

To test the application, you can log in using the pre-configured accounts:

### 1. General Users (from `seed.sql`)
* **Admin Portal**:
  * **Username**: `admin`
  * **Password**: `admin123`
* **Regular Customer**:
  * **Username**: `user`
  * **Password**: `admin123`

### 2. Restaurant Owners (from `credentials.txt`)
Use these to log in as a restaurant owner and manage the menu/orders:
* **The Golden Slice**: Username: `owner_thegoldenslice` | Password: `password123`
* **Burger Forge**: Username: `owner_burgerforge` | Password: `password123`
* **Sushi Master**: Username: `owner_sushimaster` | Password: `password123`
* *(More credentials can be found in [credentials.txt](file:///d:/University/4th%20semester/dbms%20project/credentials.txt))*

### 3. Delivery Riders (from `riders.txt`)
Use these to log in as a delivery rider and accept/deliver orders:
* **Rider 1**: Username: `rider_1` | Password: `password123`
* **Rider 2**: Username: `rider_2` | Password: `password123`
* *(More credentials can be found in [riders.txt](file:///d:/University/4th%20semester/dbms%20project/riders.txt))*

---

## ❓ Troubleshooting & FAQs

### Q1: I get a `SQLServerException: Connection refused` or `TCP/IP connection failed`
* **Solution**: 
  1. Make sure your Microsoft SQL Server service is running.
  2. Double-check that TCP/IP is **Enabled** in the SQL Server Configuration Manager (see Step 1-A).
  3. Ensure your port and password in `backend/src/main/resources/application.properties` are correct. If your SQL Server runs on the default port, change `53940` to `1433` in the URL.

### Q2: I get a `'java' is not recognized as an internal or external command` error
* **Solution**: This means Java JDK 17 is either not installed or is not added to your system's Environment Variables.
  1. Make sure you installed JDK 17.
  2. Search for "Edit the system environment variables" in your Windows search bar.
  3. Click **Environment Variables**.
  4. Under System Variables, look for `Path`, click Edit, and add the path to your Java installation's `bin` folder (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.x.x\bin`).
  5. Restart your Command Prompt and try again.

### Q3: How do I stop the servers?
* **Solution**: Click on the terminal window running the backend or frontend and press **`Ctrl + C`** on your keyboard, then type `Y` and press Enter to stop the process.

---

Enjoy exploring your Food Ordering & Delivery system! Let me know if you run into any other questions. 🚀
#   o n l i n e - f o o d - o r d e r i n g - s y s t e m  
 