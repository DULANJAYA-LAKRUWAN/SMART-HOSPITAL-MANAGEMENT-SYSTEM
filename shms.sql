-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.40 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for shms_db
CREATE DATABASE IF NOT EXISTS `shms_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `shms_db`;

-- Dumping structure for table shms_db.appointments
CREATE TABLE IF NOT EXISTS `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `doctor_id` int NOT NULL,
  `appointment_date` date NOT NULL,
  `time_slot` time NOT NULL,
  `status` enum('PENDING','COMPLETED','CANCELLED') DEFAULT 'PENDING',
  PRIMARY KEY (`appointment_id`),
  UNIQUE KEY `doc_time_unique` (`doctor_id`,`appointment_date`,`time_slot`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`doctor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.appointments: ~0 rows (approximately)
REPLACE INTO `appointments` (`appointment_id`, `patient_id`, `doctor_id`, `appointment_date`, `time_slot`, `status`) VALUES
	(45, 1, 17, '2026-03-20', '09:00:00', 'COMPLETED'),
	(50, 4, 17, '2026-04-13', '04:50:22', 'PENDING'),
	(51, 15, 17, '2026-03-17', '14:47:52', 'PENDING');

-- Dumping structure for table shms_db.audit_logs
CREATE TABLE IF NOT EXISTS `audit_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `action` varchar(255) NOT NULL,
  `module` varchar(50) DEFAULT NULL,
  `log_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `audit_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.audit_logs: ~2 rows (approximately)
REPLACE INTO `audit_logs` (`log_id`, `user_id`, `action`, `module`, `log_time`) VALUES
	(1, 1, 'PHARMA_SALE: Rs. 5.5', 'PHARMACY_POS', '2026-03-20 14:23:41'),
	(2, 1, 'PHARMA_SALE: Rs. 27.5', 'PHARMACY_POS', '2026-03-20 14:24:08'),
	(3, 1, 'BOOK_APPT: Patient-4 w/ Doctor-17', 'APPT_MGMT', '2026-03-20 14:38:04'),
	(4, 1, 'PHARMA_SALE: Rs. 4070.0', 'PHARMACY_POS', '2026-03-20 14:42:58'),
	(5, 1, 'BOOK_APPT: 2026-03-18 at 20:17:52.332', 'APPT_MGMT', '2026-03-20 14:48:02');

-- Dumping structure for table shms_db.bills
CREATE TABLE IF NOT EXISTS `bills` (
  `bill_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `appointment_id` int DEFAULT NULL,
  `consultation_total` decimal(10,2) DEFAULT '0.00',
  `pharmacy_total` decimal(10,2) DEFAULT '0.00',
  `grand_total` decimal(10,2) NOT NULL,
  `payment_status` enum('PAID','UNPAID') DEFAULT 'UNPAID',
  `bill_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bill_id`),
  KEY `patient_id` (`patient_id`),
  KEY `appointment_id` (`appointment_id`),
  CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  CONSTRAINT `bills_ibfk_2` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.bills: ~0 rows (approximately)
REPLACE INTO `bills` (`bill_id`, `patient_id`, `appointment_id`, `consultation_total`, `pharmacy_total`, `grand_total`, `payment_status`, `bill_date`) VALUES
	(17, 1, 45, 2500.00, 150.00, 2650.00, 'PAID', '2026-03-20 14:31:33'),
	(18, 1, 45, 2500.00, 150.00, 2650.00, 'PAID', '2026-03-20 14:33:14'),
	(19, 1, 45, 2500.00, 150.00, 2650.00, 'PAID', '2026-03-20 14:33:22'),
	(20, 1, 45, 2500.00, 150.00, 2650.00, 'PAID', '2026-03-20 14:34:08');

-- Dumping structure for table shms_db.doctors
CREATE TABLE IF NOT EXISTS `doctors` (
  `doctor_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `specialization` varchar(100) NOT NULL,
  `contact` varchar(15) DEFAULT NULL,
  `consultation_fee` decimal(10,2) NOT NULL,
  PRIMARY KEY (`doctor_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `doctors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.doctors: ~0 rows (approximately)
REPLACE INTO `doctors` (`doctor_id`, `user_id`, `specialization`, `contact`, `consultation_fee`) VALUES
	(17, 8, 'Cardiology', '0771234567', 2500.00),
	(18, 9, 'General Medicine', '0779876543', 1500.00),
	(19, 8, 'Cardiology', '0771234567', 2500.00),
	(20, 9, 'General Medicine', '0779876543', 1500.00),
	(21, 8, 'Cardiology', '0771234567', 2500.00),
	(22, 9, 'General Medicine', '0779876543', 1500.00),
	(23, 8, 'Cardiology', '0771234567', 2500.00),
	(24, 9, 'General Medicine', '0779876543', 1500.00);

-- Dumping structure for table shms_db.inventory_alerts
CREATE TABLE IF NOT EXISTS `inventory_alerts` (
  `alert_id` int NOT NULL AUTO_INCREMENT,
  `medicine_id` int NOT NULL,
  `alert_type` enum('LOW_STOCK','EXPIRY_NEAR') NOT NULL,
  `message` text,
  `is_read` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`alert_id`),
  KEY `medicine_id` (`medicine_id`),
  CONSTRAINT `inventory_alerts_ibfk_1` FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`medicine_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.inventory_alerts: ~4 rows (approximately)
REPLACE INTO `inventory_alerts` (`alert_id`, `medicine_id`, `alert_type`, `message`, `is_read`, `created_at`) VALUES
	(1, 5, 'LOW_STOCK', 'Atorvastatin 10mg is below the threshold of 100 units.', 0, '2026-03-20 14:23:08'),
	(2, 2, 'EXPIRY_NEAR', 'Amoxicillin 250mg batch is expiring in less than 90 days.', 0, '2026-03-20 14:23:08'),
	(3, 5, 'LOW_STOCK', 'Atorvastatin 10mg is below the threshold of 100 units.', 0, '2026-03-20 14:26:28'),
	(4, 2, 'EXPIRY_NEAR', 'Amoxicillin 250mg batch is expiring in less than 90 days.', 0, '2026-03-20 14:26:28'),
	(5, 5, 'LOW_STOCK', 'Atorvastatin 10mg is below the threshold of 100 units.', 0, '2026-03-20 14:27:58'),
	(6, 2, 'EXPIRY_NEAR', 'Amoxicillin 250mg batch is expiring in less than 90 days.', 0, '2026-03-20 14:27:58'),
	(7, 5, 'LOW_STOCK', 'Atorvastatin 10mg is below the threshold of 100 units.', 0, '2026-03-20 14:28:35'),
	(8, 2, 'EXPIRY_NEAR', 'Amoxicillin 250mg batch is expiring in less than 90 days.', 0, '2026-03-20 14:28:35'),
	(9, 5, 'LOW_STOCK', 'Atorvastatin 10mg is below the threshold of 100 units.', 0, '2026-03-20 14:30:12'),
	(10, 2, 'EXPIRY_NEAR', 'Amoxicillin 250mg batch is expiring in less than 90 days.', 0, '2026-03-20 14:30:12');

-- Dumping structure for table shms_db.medicines
CREATE TABLE IF NOT EXISTS `medicines` (
  `medicine_id` int NOT NULL AUTO_INCREMENT,
  `barcode` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL DEFAULT '0',
  `expiry_date` date NOT NULL,
  PRIMARY KEY (`medicine_id`),
  UNIQUE KEY `barcode` (`barcode`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.medicines: ~0 rows (approximately)
REPLACE INTO `medicines` (`medicine_id`, `barcode`, `name`, `category`, `unit_price`, `stock_quantity`, `expiry_date`) VALUES
	(1, 'MED001', 'Paracetamol 500mg', NULL, 5.50, 194, '2027-12-31'),
	(2, 'MED002', 'Amoxicillin 250mg', NULL, 25.00, 100, '2026-06-30'),
	(3, 'MED003', 'Ibuprofen 400mg', NULL, 12.00, 140, '2027-03-31'),
	(4, 'MED004', 'Metformin 500mg', NULL, 8.75, 300, '2027-09-30'),
	(5, 'MED005', 'Atorvastatin 10mg', NULL, 45.00, 70, '2026-12-31'),
	(21, 'MED006', 'Panadol Advance', NULL, 15.00, 500, '2028-01-01'),
	(22, 'MED007', 'Zentel 400mg', NULL, 85.00, 40, '2026-11-15'),
	(23, 'MED008', 'Vicks VapoRub', NULL, 350.00, 20, '2027-05-20');

-- Dumping structure for table shms_db.patients
CREATE TABLE IF NOT EXISTS `patients` (
  `patient_id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `nic_number` varchar(15) DEFAULT NULL,
  `dob` date NOT NULL,
  `gender` enum('MALE','FEMALE','OTHER') DEFAULT NULL,
  `contact_number` varchar(15) NOT NULL,
  `address` text,
  `registered_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `nic_number` (`nic_number`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.patients: ~0 rows (approximately)
REPLACE INTO `patients` (`patient_id`, `first_name`, `last_name`, `nic_number`, `dob`, `gender`, `contact_number`, `address`, `registered_date`) VALUES
	(1, 'Kasun', 'Perera', '199012345678', '1990-05-15', 'MALE', '0712345678', 'No 5, Colombo', '2026-03-20 10:37:29'),
	(2, 'Sanduni', 'Silva', '199587654321', '1995-08-20', 'FEMALE', '0777654321', 'Kandy Road, Gampaha', '2026-03-20 10:37:29'),
	(3, 'Rohan', 'Fernando', '198234567890', '1982-02-10', 'MALE', '0761122334', 'Galle, Southern Province', '2026-03-20 10:37:29'),
	(4, 'Dulanjaya', 'Lakruwan', '200232002030', '2002-11-14', 'MALE', '0714089493', 'N/A', '2026-03-20 11:09:52'),
	(14, 'Nimal', 'Jayasinghe', '851234567V', '1985-11-12', 'MALE', '0771231234', 'Kurunegala', '2026-03-20 14:26:28'),
	(15, 'Fathima', 'Rizla', '948765432V', '1994-04-25', 'FEMALE', '0715566778', 'Negombo', '2026-03-20 14:26:28');

-- Dumping structure for table shms_db.prescriptions
CREATE TABLE IF NOT EXISTS `prescriptions` (
  `prescription_id` int NOT NULL AUTO_INCREMENT,
  `appointment_id` int NOT NULL,
  `notes` text,
  `date_issued` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prescription_id`),
  UNIQUE KEY `appointment_id` (`appointment_id`),
  CONSTRAINT `prescriptions_ibfk_1` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.prescriptions: ~0 rows (approximately)

-- Dumping structure for table shms_db.prescription_items
CREATE TABLE IF NOT EXISTS `prescription_items` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `prescription_id` int NOT NULL,
  `medicine_id` int NOT NULL,
  `dosage` varchar(50) NOT NULL,
  `duration_days` int NOT NULL,
  `quantity_dispensed` int NOT NULL,
  PRIMARY KEY (`item_id`),
  KEY `prescription_id` (`prescription_id`),
  KEY `medicine_id` (`medicine_id`),
  CONSTRAINT `prescription_items_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`prescription_id`),
  CONSTRAINT `prescription_items_ibfk_2` FOREIGN KEY (`medicine_id`) REFERENCES `medicines` (`medicine_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.prescription_items: ~0 rows (approximately)

-- Dumping structure for table shms_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','RECEPTIONIST','DOCTOR','PHARMACIST') NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `status` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table shms_db.users: ~1 rows (approximately)
REPLACE INTO `users` (`user_id`, `username`, `password_hash`, `role`, `full_name`, `status`, `created_at`) VALUES
	(1, 'admin', 'RKHc5bwYVsLsJzZrO9TySw==:BWNyPYEts4dJYHMEABtbwR1Nki/1ewU5O6psFaHASXE=', 'ADMIN', 'System Administrator', 1, '2026-03-20 07:54:00'),
	(8, 'dr.silva', '8NcX4h7XTpwhNtSCsY9eaw==:aZU+DQ3D07kH/DwbvwuZsJFjtNo0czGYbw8SfAu5YWY=', 'DOCTOR', 'Dr. Kamal Silva', 1, '2026-03-20 10:37:29'),
	(9, 'dr.perera', '6QI0Gof4ptFT5ledUS04vA==:jYvGOLA7eoP672E5QNsWQ6nuSWOr9cDE/fCBpbxvbnc=', 'DOCTOR', 'Dr. Nimal Perera', 1, '2026-03-20 10:37:29'),
	(10, 'pharmacist', 'nR0Pw7SIiUk9t+lEodbK1w==:v4JgqtNbilNuEEe6jZpM7qiIc+dniJ9fziQ99kafJvw=', 'PHARMACIST', 'R. Kumari', 1, '2026-03-20 10:37:29');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
