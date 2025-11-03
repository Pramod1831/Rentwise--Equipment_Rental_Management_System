# RentWise: Equipment Rental Management System

## Project Overview
**RentWise** is a comprehensive **desktop application** designed to manage inventory and rental operations for equipment in environments such as **gyms, sports clubs, or university laboratories**. 

<img width="1501" height="973" alt="rentwise-dashboard" src="https://github.com/user-attachments/assets/c81c21e6-d787-4ed8-95d7-69e8c06507f4" />


The system provides **secure authentication** and separate interfaces for **administrators/staff** and **clients**, streamlining the entire rental management process.

### Key Features
- Secure **user authentication** (Admin & User roles)  
- Real-time **inventory tracking**  
- Equipment **request and approval workflow**  
- Simple **MySQL-backed data management**  
- Intuitive **JavaFX interface**  

---

## Technologies Used

| Component | Technology |
|------------|-------------|
| **Frontend/UI** | JavaFX |
| **Backend/Logic** | Java (JDK 17+) |
| **Database** | MySQL |
| **Database Connectivity** | JDBC (MySQL Connector/J) |
| **Password Encryption** | jBCrypt |
| **Build System** | Manual setup (direct JAR integration) |

---

## Getting Started

Follow the steps below to set up and run the **RentWise** application on any system.

---

### Prerequisites

Ensure the following software is installed on your system:

- **Java Development Kit (JDK)** → Version **17 or higher**  
- **MySQL Server** → Any version (e.g., MySQL 8.0)  
- **MySQL Client** → MySQL Workbench or command-line client  
- **Git** → For cloning the repository  

---

### Database Setup (Crucial)

The application requires a configured **MySQL database** before running.

#### A. Create the Database
Open your MySQL client (Workbench or CLI) and run:
```sql
CREATE DATABASE rentwise_db;
```
B. Create Tables and Schema

```sql
Use the following SQL schema to create the required tables:

-- Table: user_account
CREATE TABLE user_account (
    account_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    firstname VARCHAR(45) NOT NULL,
    lastname VARCHAR(45) NOT NULL,
    username VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'user',
    user_is_active TINYINT(1) DEFAULT 1
);

-- Table: equipments
CREATE TABLE equipments (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    issued INT DEFAULT 0,
    image_data LONGBLOB
);

-- Table: requests
CREATE TABLE requests (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    equipment_id INT NOT NULL,
    quantity_requested INT NOT NULL,
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    action_date TIMESTAMP,
    is_acknowledged TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user_account(account_id),
    FOREIGN KEY (equipment_id) REFERENCES equipments(id)
);
```
3. Dependencies (Manual JAR Setup)

Since this project does not use Maven or Gradle, dependencies must be added manually.

Dependency	Purpose	Required Files
JavaFX SDK	UI framework for FXML & controls	Add all platform-specific JARs (e.g., javafx.controls.jar, javafx.fxml.jar) to Module Path
MySQL Connector/J	JDBC driver for database access	mysql-connector-j-x.x.x.jar
jBCrypt	Secure password hashing	jbcrypt-x.x.jar

All JARs should be linked via your IDE (IntelliJ or Eclipse).

4️. Clone and Configure
Clone the Repository
git clone https://github.com/Pramod1831/Rentwise--Equipment_Rental_Management_System.git
cd Rentwise--Equipment_Rental_Management_System

Configure in Your IDE

Open the project in IntelliJ IDEA or Eclipse.

Go to Project Structure → Libraries / Build Path.

Add:

JavaFX SDK JARs → to Module Path

MySQL Connector/J and jBCrypt JARs → to Classpath/Libraries
