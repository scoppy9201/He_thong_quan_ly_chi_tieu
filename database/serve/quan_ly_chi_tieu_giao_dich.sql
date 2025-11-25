-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: quan_ly_chi_tieu
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `giao_dich`
--

DROP TABLE IF EXISTS `giao_dich`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `giao_dich` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` int NOT NULL,
  `danh_muc_id` int NOT NULL,
  `so_tien` decimal(15,2) NOT NULL,
  `loai_giao_dich` enum('THU','CHI') COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_giao_dich` date NOT NULL,
  `phuong_thuc` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT 'Tiền mặt',
  `ghi_chu` text COLLATE utf8mb4_unicode_ci,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `anh_hoa_don` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ngay` (`ngay_giao_dich`),
  KEY `idx_loai` (`loai_giao_dich`),
  KEY `idx_danh_muc` (`danh_muc_id`),
  KEY `idx_user` (`nguoi_dung_id`),
  CONSTRAINT `giao_dich_ibfk_1` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`id`) ON DELETE CASCADE,
  CONSTRAINT `giao_dich_ibfk_2` FOREIGN KEY (`danh_muc_id`) REFERENCES `danh_muc` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `giao_dich`
--

LOCK TABLES `giao_dich` WRITE;
/*!40000 ALTER TABLE `giao_dich` DISABLE KEYS */;
INSERT INTO `giao_dich` VALUES (1,2,1,15000000.00,'THU','2025-01-10','Chuyển khoản','Lương tháng 1','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(2,2,1,5000000.00,'THU','2025-02-10','Chuyển khoản','Lương tháng 2','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(3,2,2,200000.00,'THU','2025-02-15','Tiền mặt','Bán đồ cũ','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(4,2,3,1000000.00,'THU','2025-03-01','Chuyển khoản','Lãi tiết kiệm','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(5,2,4,300000.00,'THU','2025-03-05','Tiền mặt','Tiền thưởng','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(6,2,5,500000.00,'THU','2025-04-01','Chuyển khoản','Hỗ trợ từ gia đình','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(7,2,6,250000.00,'THU','2025-04-10','Tiền mặt','Nguồn tiền thu nhập từ việc kinh doanh','2025-11-06 16:32:47','2025-11-21 02:21:17','uploads/invoices/2_1763524953469.jpg'),(10,2,11,300000.00,'CHI','2025-01-15','Chuyển khoản','Ăn uống cuối tuần','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(11,2,12,1200000.00,'CHI','2025-02-01','Tiền mặt','Mua quần áo mới','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(12,2,13,450000.00,'CHI','2025-02-10','Tiền mặt','Đi chợ hàng tuần','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(13,2,14,200000.00,'CHI','2025-02-20','Tiền mặt','Đi xem phim','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(14,2,15,3000000.00,'CHI','2025-03-01','Chuyển khoản','Đóng tiền phòng trọ','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(15,2,16,150000.00,'CHI','2025-03-05','Tiền mặt','Mua xăng','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(16,2,17,400000.00,'CHI','2025-03-10','Tiền mặt','Tiền điện tháng 3','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(17,2,18,350000.00,'CHI','2025-04-01','Tiền mặt','Tiền nước tháng 3','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(18,2,19,200000.00,'CHI','2025-04-05','Chuyển khoản','Internet / WiFi','2025-11-06 16:32:47','2025-11-06 16:32:47',NULL),(19,2,20,250000.00,'CHI','2025-04-10','Tiền mặt','Mua đồ sinh hoạt','2025-11-06 16:32:47','2025-11-21 07:48:52','uploads/invoices/2_1763711332901.jpg');
/*!40000 ALTER TABLE `giao_dich` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-25 15:04:05
