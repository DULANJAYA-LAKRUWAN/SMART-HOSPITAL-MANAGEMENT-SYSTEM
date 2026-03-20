-- ============================================================
-- SHMS - Smart Hospital Management System
-- Database Setup Script
-- Run this in MySQL Workbench or phpMyAdmin
-- ============================================================

-- 1. Create and use the database
CREATE DATABASE IF NOT EXISTS shms_db;
USE shms_db;

-- ============================================================
-- 2. USERS TABLE (Login Accounts)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,   -- Store plain text for now; hash in production
    role           ENUM('ADMIN','DOCTOR','PHARMACIST') NOT NULL,
    full_name      VARCHAR(100) NOT NULL,
    status         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. PATIENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    patient_id     INT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    nic_number     VARCHAR(20)  NOT NULL UNIQUE,
    dob            DATE         NOT NULL,
    gender         ENUM('MALE','FEMALE','OTHER') NOT NULL,
    contact_number VARCHAR(15),
    address        TEXT,
    registered_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 4. DOCTORS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id            INT          NOT NULL,
    specialization     VARCHAR(100) NOT NULL,
    contact            VARCHAR(15),
    consultation_fee   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- 5. APPOINTMENTS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT NOT NULL,
    doctor_id        INT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot        TIME NOT NULL,
    status           ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
    booked_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(doctor_id),
    UNIQUE KEY no_double_booking (doctor_id, appointment_date, time_slot)
);

-- ============================================================
-- 6. MEDICINES TABLE (Pharmacy/Inventory)
-- ============================================================
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    barcode         VARCHAR(50)  UNIQUE,
    unit_price      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock_quantity  INT NOT NULL DEFAULT 0,
    expiry_date     DATE,
    added_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 7. BILLS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS bills (
    bill_id              INT AUTO_INCREMENT PRIMARY KEY,
    patient_id           INT NOT NULL,
    appointment_id       INT,
    consultation_total   DECIMAL(10,2) DEFAULT 0.00,
    pharmacy_total       DECIMAL(10,2) DEFAULT 0.00,
    grand_total          DECIMAL(10,2) DEFAULT 0.00,
    payment_status       ENUM('PAID','UNPAID','PARTIAL') DEFAULT 'UNPAID',
    bill_date            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)     REFERENCES patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
);

-- ============================================================
-- 8. SEED DATA - Default Users (passwords are plain text for dev)
-- ============================================================
INSERT IGNORE INTO users (username, password_hash, role, full_name) VALUES
    ('admin',      'admin123',   'ADMIN',       'System Administrator'),
    ('dr.silva',   'doctor123',  'DOCTOR',      'Dr. Kamal Silva'),
    ('dr.perera',  'doctor123',  'DOCTOR',      'Dr. Nimal Perera'),
    ('pharmacist', 'pharma123',  'PHARMACIST',  'R. Kumari');

-- ============================================================
-- 9. SEED DATA - Sample Doctors (linked to doctor user accounts)
-- ============================================================
INSERT IGNORE INTO doctors (user_id, specialization, contact, consultation_fee) VALUES
    (2, 'Cardiology', '0771234567', 2500.00),
    (3, 'General Medicine', '0779876543', 1500.00);

-- ============================================================
-- 10. SEED DATA - Sample Medicines
-- ============================================================
INSERT IGNORE INTO medicines (name, barcode, unit_price, stock_quantity, expiry_date) VALUES
    ('Paracetamol 500mg',    'MED001', 5.50,   200, '2027-12-31'),
    ('Amoxicillin 250mg',    'MED002', 25.00,  100, '2026-06-30'),
    ('Ibuprofen 400mg',      'MED003', 12.00,  150, '2027-03-31'),
    ('Metformin 500mg',      'MED004', 8.75,   300, '2027-09-30'),
    ('Atorvastatin 10mg',    'MED005', 45.00,   80, '2026-12-31');

-- ============================================================
-- 11. SEED DATA - Sample Patients
-- ============================================================
INSERT IGNORE INTO patients (first_name, last_name, nic_number, dob, gender, contact_number, address) VALUES
    ('Kasun',   'Perera',  '199012345678', '1990-05-15', 'MALE',   '0712345678', 'No 5, Colombo'),
    ('Sanduni', 'Silva',   '199587654321', '1995-08-20', 'FEMALE', '0777654321', 'Kandy Road, Gampaha'),
    ('Rohan',   'Fernando','198234567890', '1982-02-10', 'MALE',   '0761122334', 'Galle, Southern Province');

-- ============================================================
-- 12. AUDIT LOGS (Security & Tracking)
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    action      VARCHAR(255) NOT NULL,
    module      VARCHAR(50),
    log_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ============================================================
-- 13. INVENTORY ALERTS (Stock Tracking)
-- ============================================================
CREATE TABLE IF NOT EXISTS inventory_alerts (
    alert_id    INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    alert_type  ENUM('LOW_STOCK','EXPIRY_NEAR') NOT NULL,
    message     TEXT,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
);

-- Done!
SELECT 'SHMS Database Setup Enhanced with Audit & Alerts!' AS Status;
