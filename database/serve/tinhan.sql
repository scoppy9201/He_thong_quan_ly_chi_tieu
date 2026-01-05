-- --------------------------------------------------------
-- Máy chủ:                      127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Phiên bản:           12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for quan_ly_chi_tieu
CREATE DATABASE IF NOT EXISTS `quan_ly_chi_tieu` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `quan_ly_chi_tieu`;

-- Dumping structure for table quan_ly_chi_tieu.doan_chat
CREATE TABLE IF NOT EXISTS `doan_chat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` int NOT NULL,
  `tieu_de` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'Đoạn chat mới',
  `la_ghim` tinyint(1) DEFAULT '0',
  `thu_tu_ghim` int DEFAULT NULL,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_nguoi_dung` (`nguoi_dung_id`),
  KEY `idx_ghim` (`la_ghim`,`thu_tu_ghim`),
  KEY `idx_doan_chat_updated` (`nguoi_dung_id`,`ngay_cap_nhat` DESC),
  CONSTRAINT `fk_doan_chat_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quan_ly_chi_tieu.doan_chat: ~0 rows (approximately)
DELETE FROM `doan_chat`;

-- Dumping structure for table quan_ly_chi_tieu.tin_nhan
CREATE TABLE IF NOT EXISTS `tin_nhan` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doan_chat_id` int NOT NULL,
  `vai_tro` enum('USER','ASSISTANT') COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `metadata` json DEFAULT NULL,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_doan_chat` (`doan_chat_id`),
  KEY `idx_ngay_tao` (`ngay_tao`),
  KEY `idx_tin_nhan_chat_role` (`doan_chat_id`,`vai_tro`),
  CONSTRAINT `fk_tin_nhan_doan_chat` FOREIGN KEY (`doan_chat_id`) REFERENCES `doan_chat` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quan_ly_chi_tieu.tin_nhan: ~0 rows (approximately)
DELETE FROM `tin_nhan`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
