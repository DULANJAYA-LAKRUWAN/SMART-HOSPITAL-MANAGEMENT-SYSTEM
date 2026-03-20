-- ============================================================
-- SHMS - Smart Hospital Management System
-- Failsafe Database Setup Script (v2.0 - Resilient Seed Data)
-- ============================================================

-- 1. Create and use the database
CREATE DATABASE IF NOT EXISTS shms_db;
USE shms_db;

-- ============================================================
-- 2. USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id        INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           ENUM('ADMIN','DOCTOR','PHARMACIST') NOT NULL,
    full_name      VARCHAR(100) NOT NULL,
    status         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 3. PATIENTS TABLE (Synchronized with HeidiSQL column names)
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
    registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
-- 6. MEDICINES TABLE
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
-- 8. AUDIT & INVENTORY TABLES
-- ============================================================
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    action      VARCHAR(255) NOT NULL,
    module      VARCHAR(50),
    log_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS inventory_alerts (
    alert_id    INT AUTO_INCREMENT PRIMARY KEY,
    medicine_id INT NOT NULL,
    alert_type  ENUM('LOW_STOCK','EXPIRY_NEAR') NOT NULL,
    message     TEXT,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
);

-- ============================================================
-- 9. PRESCRIPTIONS & ITEMS
-- ============================================================
CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id   INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id    INT NOT NULL,
    notes             TEXT,
    date_issued       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prescription_items (
    item_id           INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id   INT NOT NULL,
    medicine_id       INT NOT NULL,
    dosage            VARCHAR(100),
    duration          VARCHAR(50),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id),
    FOREIGN KEY (medicine_id)     REFERENCES medicines(medicine_id)
);

-- ============================================================
-- 10. FAILSAFE SEED DATA (Subqueries used to avoid Primary/Foreign Key conflicts)
-- ============================================================

-- Seed Users
INSERT IGNORE INTO users (username, password_hash, role, full_name) VALUES
    ('admin',      'admin123',   'ADMIN',       'System Administrator'),
    ('dr.silva',   'doctor123',  'DOCTOR',      'Dr. Kamal Silva'),
    ('dr.perera',  'doctor123',  'DOCTOR',      'Dr. Nimal Perera'),
    ('pharmacist', 'pharma123',  'PHARMACIST',  'R. Kumari');

-- Seed Doctors (DYNAMIC ID RESOLUTION)
INSERT IGNORE INTO doctors (user_id, specialization, contact, consultation_fee) 
SELECT user_id, 'Cardiology', '0771234567', 2500.00 FROM users WHERE username = 'dr.silva';

INSERT IGNORE INTO doctors (user_id, specialization, contact, consultation_fee) 
SELECT user_id, 'General Medicine', '0779876543', 1500.00 FROM users WHERE username = 'dr.perera';

-- Seed Medicines
INSERT IGNORE INTO medicines (name, barcode, unit_price, stock_quantity, expiry_date) VALUES
    ('Paracetamol 500mg',    'MED001', 5.50,   200, '2027-12-31'),
    ('Amoxicillin 250mg',    'MED002', 25.00,  100, '2026-06-30'),
    ('Ibuprofen 400mg',      'MED003', 12.00,  150, '2027-03-31'),
    ('Metformin 500mg',      'MED004', 8.75,   300, '2027-09-30'),
    ('Atorvastatin 10mg',    'MED005', 45.00,   80, '2026-12-31'),
    ('Panadol Advance',      'MED006', 15.00,  500, '2028-01-01'),
    ('Zentel 400mg',         'MED007', 85.00,   40, '2026-11-15'),
    ('Vicks VapoRub',        'MED008', 350.00,  30, '2027-05-20');

-- Seed Patients (SRI LANKAN FORMATS)
INSERT IGNORE INTO patients (first_name, last_name, nic_number, dob, gender, contact_number, address) VALUES
    ('Kasun',   'Perera',  '199012345678', '1990-05-15', 'MALE',   '0712345678', 'No 5, Colombo'),
    ('Sanduni', 'Silva',   '199587654321', '1995-08-20', 'FEMALE', '0777654321', 'Kandy Road, Gampaha'),
    ('Rohan',   'Fernando','198234567890', '1982-02-10', 'MALE',   '0761122334', 'Galle, Southern Province'),
    ('Nimal',   'Jayasinghe','851234567V', '1985-11-12', 'MALE',   '0771231234', 'Kurunegala'),
    ('Fathima', 'Rizla',   '948765432V', '1994-04-25', 'FEMALE', '0715566778', 'Negombo');

-- Seed Appointments (DYNAMIC ID RESOLUTION)
INSERT IGNORE INTO appointments (patient_id, doctor_id, appointment_date, time_slot, status)
SELECT 
    (SELECT patient_id FROM patients WHERE nic_number = '199012345678' LIMIT 1),
    (SELECT doctor_id FROM doctors d JOIN users u ON d.user_id = u.user_id WHERE u.username = 'dr.silva' LIMIT 1),
    CURDATE(), '09:00:00', 'COMPLETED';

-- Seed Bills (DYNAMIC ID RESOLUTION)
INSERT IGNORE INTO bills (patient_id, appointment_id, consultation_total, pharmacy_total, grand_total, payment_status)
SELECT 
    (SELECT patient_id FROM patients WHERE nic_number = '199012345678' LIMIT 1),
    (SELECT appointment_id FROM appointments WHERE patient_id = (SELECT patient_id FROM patients WHERE nic_number = '199012345678' LIMIT 1) LIMIT 1),
    2500.00, 150.00, 2650.00, 'PAID';

-- Done!
SELECT 'SHMS Failsafe Script Executed Successfully!' AS Deployment_Status;
