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

-- Dumping structure for table quan_ly_chi_tieu.email
CREATE TABLE IF NOT EXISTS `email` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` int NOT NULL,
  `tieu_de` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email_nguoi_nhan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `loai_email` enum('THONG_BAO_GIAO_DICH','CANH_BAO','BAO_CAO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'THONG_BAO_GIAO_DICH',
  `trang_thai` enum('CHUA_DOC','DA_DOC') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'CHUA_DOC',
  `da_gui` tinyint(1) DEFAULT '0',
  `la_ghim` tinyint(1) DEFAULT '0',
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_doc` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_nguoi_dung` (`nguoi_dung_id`),
  KEY `idx_trang_thai` (`trang_thai`),
  KEY `idx_la_ghim` (`la_ghim`),
  CONSTRAINT `fk_email_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table quan_ly_chi_tieu.email: ~4 rows (approximately)
DELETE FROM `email`;
INSERT INTO `email` (`id`, `nguoi_dung_id`, `tieu_de`, `noi_dung`, `email_nguoi_nhan`, `loai_email`, `trang_thai`, `da_gui`, `la_ghim`, `ngay_tao`, `ngay_doc`) VALUES
	(1, 2, 'Thông báo: Giao dịch mới - CHI', 'Xin chào Bùi Mạnh Hưng,\n\nBạn vừa thêm một giao dịch mới:\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nLoại: CHI\nSố tiền: 1.000.000 đ\nDanh mục: null\nNgày: 2026-01-05\nPhương thức: Chuyển khoản\n📝 Ghi chú: giao diịch có email\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\nCảm ơn bạn đã sử dụng Hệ thống Quản lý Chi tiêu!\n\nTrân trọng,\nĐội ngũ phát triển', 'Manhhungdz3105@gmail.com', 'THONG_BAO_GIAO_DICH', 'DA_DOC', 0, 0, '2026-01-05 04:39:38', '2026-01-05 04:53:08'),
	(3, 4, 'Thông báo: Giao dịch mới - CHI', 'Xin chào duynam,\n\nBạn vừa thêm một giao dịch mới:\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nLoại: CHI\nSố tiền: 2.000.000 đ\nDanh mục: null\nNgày: 2026-01-05\nPhương thức: Chuyển khoản\n📝 Ghi chú: hóa đơn cho duy nam\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\nCảm ơn bạn đã sử dụng Hệ thống Quản lý Chi tiêu!\n\nTrân trọng,\nĐội ngũ phát triển', 'dnam16092005@gmail.com', 'THONG_BAO_GIAO_DICH', 'CHUA_DOC', 1, 0, '2026-01-05 06:26:20', NULL),
	(4, 4, 'Thông báo: Giao dịch đã được cập nhật', 'Xin chào duynam,\n\nMột giao dịch của bạn đã được CẬP NHẬT:\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nLoại: CHI\nSố tiền mới: 2.000.000 đ\nDanh mục: Ăn trưa\nNgày: 2026-01-05\nPhương thức: tiền mặt\nGhi chú: hóa đơn cho duy nam\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\nVui lòng kiểm tra lại thông tin nếu cần.\n\nTrân trọng,\nĐội ngũ phát triển', 'dnam16092005@gmail.com', 'THONG_BAO_GIAO_DICH', 'CHUA_DOC', 1, 0, '2026-01-05 06:57:58', NULL),
	(5, 4, 'Thông báo: Giao dịch đã bị xóa', 'Xin chào duynam,\n\nMột giao dịch của bạn đã bị XÓA khỏi hệ thống:\n\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\nLoại: CHI\nSố tiền: 2.000.000 đ\nDanh mục: Ăn trưa\nNgày giao dịch: 2026-01-05\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\nNếu đây không phải là hành động của bạn, vui lòng liên hệ hỗ trợ.\n\nTrân trọng,\nĐội ngũ phát triển', 'dnam16092005@gmail.com', 'THONG_BAO_GIAO_DICH', 'CHUA_DOC', 1, 0, '2026-01-05 06:58:19', NULL);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
