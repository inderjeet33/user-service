# Prerana Helpline Foundation – Donation Management Platform

## 📌 Overview
Prerana Helpline Foundation is a role-based donation management platform designed to connect **individual donors**, **NGOs**, and **moderators** in a structured and transparent way.

The system allows donors to raise donation offers, moderators to verify and assign NGOs, and NGOs to manage assigned donation requests.

---

## 👥 User Roles

The application supports **three types of users**:

### 1. Individual (Donor)
- Can register & login
- Can create donation offers
- Can track donation status
- Can view assigned NGO details once assigned

### 2. NGO
- Can register & login
- Must complete profile and get verified
- Receives donation assignments from moderators
- Can update donation progress/status

### 3. Moderator
- Special role created from backend / database
- Can review donation offers
- Can verify NGOs
- Can assign donation offers to NGOs
- Has access to reports and Excel exports

---

## 🔐 Authentication & Role Handling

- Login is **role-based**
- Same login screen is used, but access is controlled via role
- Moderator role is **not publicly registered**
- Moderator access is granted by updating the user role in the database

---

## 🔄 Application Flow

### 🧍 Individual (Donor) Flow
1. Register / Login
2. Create donation offer
3. View offer status
4. Wait for moderator assignment
5. Track progress until completion

---

### 🏢 NGO Flow
1. Register / Login
2. Complete NGO profile
3. Get verified by moderator
4. Receive assigned donation offers
5. Update donation status:
   - Assigned → In Progress → Completed / Rejected

---

### 🧑‍⚖️ Moderator Flow
1. Login using moderator credentials
2. View all donation offers
3. View & verify NGO profiles
4. Assign donation offers to NGOs
5. Monitor donation lifecycle
6. Export reports (Excel)

---

## 🛠 Moderator Role Setup (Important)

Moderator users are **created via backend/database**.

Steps:
1. Create a user normally
2. Update the user role in DB to `MODERATOR`
3. Moderator login UI becomes accessible
4. Moderator dashboard is enabled automatically

> Moderator registration is intentionally restricted for security.

---

## 🧰 Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- JPA / Hibernate

### Frontend
- React (Vite)
- Axios
- React Router
- CSS Modules

---

## ▶pull the frontend and backend repos and make sure to provide with proper connection db string and Run the Project Locally
