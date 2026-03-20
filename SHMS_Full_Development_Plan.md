# 📘 Smart Hospital Management System (SHMS): Full Development Plan

## 1. Project Overview
The SHMS is a technical, enterprise-grade software solution designed for medical efficiency. This plan outlines a phased approach to ensure all **Distinction-level** criteria are met.

---

## 2. Phase 1: Foundation & Core Logic (Completed)
*   **Layered Architecture Setup**: 
    *   UI (Swing) → Service (Logic) → DAO (Data Access) → DB (MySQL).
*   **Database Schema**: Initialized with `shms_db` and core tables (Users, Patients, Doctors).
*   **Authentication System**: Secure Login with `UserDAO` and dynamic redirection to `MainDashboardUI`.
*   **Role-Based Access Control (RBAC)**: Menus visibility toggling depending on user role (Admin/Doctor/Pharmacist).

---

## 3. Phase 2: Patient & Doctor Management (In Progress)
*   **Patient CRUD Module**: 
    *   `PatientPanel` UI, `PatientDAO`, and `Patient` model.
    *   Load and refresh patients in a `JTable`.
*   **Doctor Registry**: 
    *   `DoctorPanel` UI, `DoctorDAO` using SQL JOINs.
    *   Manage specialization, fees, and contact data.

---

## 4. Phase 3: Appointment Logic (The Business Engine)
*   **Booking Interface**: 
    *   `AppointmentPanel` featuring date picking and slot selection.
*   **Conflict Prevention Logic**: 
    *   Algorithm to prevent double-booking a specific doctor on the same date/time slot.
*   **Patient Search**: Wildcard searching (by name/NIC) to link to appointments.

---

## 5. Phase 4: Pharmacy, Inventory & Peripherals
*   **Medicine Management**: CRUD operations for the drug stock.
*   **Peripheral Integration**: 
    *   **Barcode Scanner**: Use `KeyListener` to auto-fill medicine details on the POS screen.
    *   **Thermal Printer Simulation**: Generate print-ready receipt layouts using `java.awt.print`.
*   **Inventory Decay**: Automatically subtract drug quantity upon payment completion.

---

## 6. Phase 5: Billing & Financial Reporting
*   **Consolidated Billing**: Fetching consultation fees + pharmacy subtotals into one final bill.
*   **JasperReports Integration**: Professional invoice generation (.jrxml).
*   **Payment Tracking**: Managing "Paid" vs "Unpaid" statuses with update triggers.

---

## 7. Phase 6: KPI Reporting (System Insights)
*   **8 Mandatory Reports**:
    1. Daily Revenue Chart.
    2. Patient Footfall Analytics.
    3. Low-Stock Drug Alerts.
    4. Doctor Performance Report.
    5. Monthly Disease Distribution.
    6. Pending Bills Audit.
    7. Staff Attendance/Audit Logs.
    8. Fast-Moving Medicine List.

---

## 8. Phase 7: Quality Assurance & Testing
*   **JUnit 5 Integration**: Writing unit tests for critical methods (e.g., Billing formulas).
*   **Log4j Implementation**: Diverting system logs into external `.log` files for auditing.
*   **Error Handling**: Global exception handling with professional UI alerts (JOptionPane).

---

## 9. Phase 8: Final Deployment & Documentation
*   **EXE Packaging**: Converting JAR into a standalone Windows executable using **Launch4j**.
*   **Full SRS & UML Update**: Finalizing diagrams (Use Case, Sequence, ER) to match final DB state.
*   **Viva Preparation**: Compiling a 20-page "Question & Answer" guide for the project defense.

---

## 10. Academic Success Criteria
*   **Normalization**: Database must be at least 3NF.
*   **Patterns**: Correct usage of Singleton, DAO, and Model patterns.
*   **UI/UX**: Clean, responsive layout with modern Nimbus Look and Feel.
