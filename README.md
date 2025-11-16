# 📘 CPS510 Assignment 9 — Oracle Database with Java UI

### 🏫 Toronto Metropolitan University  
**Course:** CPS510 – Database Systems  
**Project Title:** SQL E-Commerce Database with Java UI  
**Submission Date:** Week of Nov 17 (Project Demo)  

---

## 🧑‍💻 Team Members

| Name | Role | Description |
|------|------|--------------|
| **Basmulla Atekulla** | Lead Developer & Database Integrator | Set up Oracle 11g connection, configured JDBC driver, developed and tested Java Swing UI, and managed GitHub repository. |
| **Rochelle Deanna** | UI/UX Designer | Enhanced the Swing UI layout and appearance, organized panels and buttons, wrote demo notes and documentation. |
| **Michelle** | SQL Architect & Tester | Designed and verified `CREATE`, `INSERT`, and `SELECT` SQL scripts; ensured normalization (3NF/BCNF) and consistent dummy records. |

---

## 💡 Project Description

This project demonstrates an **interactive Java Swing user interface** connected to the **Oracle 11g database** hosted at TMU.  
The UI allows users to:
- Create, populate, query, and drop tables  
- Display query results dynamically in a text area  
- Handle Oracle login credentials and session management gracefully  

The backend connects via **JDBC (ojdbc6.jar)**, executing external `.sql` scripts for each operation.

---


---

## ⚙️ Requirements

- **Oracle 11g Database** (TMU Server: `oracle.scs.ryerson.ca:1521:orcl`)  
- **JDK 8 or newer**  
- **ojdbc6.jar** in `/lib`  
- Access to **SSH** on `thebe` or `europa` server  
- Tables normalized to 3NF/BCNF with dummy records

---

## 🔗 Database Connection Setup

1. Download `ojdbc6-11.2.0.4.jar` and place it in the `/lib` directory.  
2. Ensure the Oracle DB is reachable:  

Host: oracle.scs.ryerson.ca
Port: 1521
SID: orcl

3. Credentials are prompted securely on startup and saved locally in  
`.db_credentials.properties` (optional).

