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
-- Table structure for table `danh_muc`
--

DROP TABLE IF EXISTS `danh_muc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `danh_muc` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten_danh_muc` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `loai_danh_muc` enum('THU','CHI') COLLATE utf8mb4_unicode_ci NOT NULL,
  `danh_muc_cha_id` int DEFAULT NULL,
  `bieu_tuong` text COLLATE utf8mb4_unicode_ci,
  `thu_tu_hien_thi` int DEFAULT '0',
  `cap_do` tinyint DEFAULT '1',
  `trang_thai` enum('ACTIVE','INACTIVE') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `ngay_tao` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_loai_danh_muc` (`loai_danh_muc`),
  KEY `idx_danh_muc_cha` (`danh_muc_cha_id`),
  KEY `idx_trang_thai` (`trang_thai`),
  KEY `idx_thu_tu` (`thu_tu_hien_thi`),
  CONSTRAINT `danh_muc_ibfk_1` FOREIGN KEY (`danh_muc_cha_id`) REFERENCES `danh_muc` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `danh_muc`
--

LOCK TABLES `danh_muc` WRITE;
/*!40000 ALTER TABLE `danh_muc` DISABLE KEYS */;
INSERT INTO `danh_muc` VALUES (1,'Ăn uống','CHI',NULL,'E:\\HeThongQuanLyChiTieu\\image\\healthy-nutrition.png',1,1,'ACTIVE','Chi tiêu ăn uống hàng ngày','2025-11-05 16:31:37','2025-11-15 08:42:30'),(2,'Đi lại','CHI',NULL,'E:\\HeThongQuanLyChiTieu\\image\\travel-bag.png',2,1,'ACTIVE','Chi tiêu đi lại','2025-11-05 16:31:37','2025-11-15 09:15:44'),(3,'Giải trí','CHI',NULL,'E:\\HeThongQuanLyChiTieu\\image\\cinema.png',3,1,'ACTIVE','Chi tiêu giải trí','2025-11-05 16:31:37','2025-11-15 09:16:05'),(4,'Phát triển bản thân','CHI',NULL,'E:\\HeThongQuanLyChiTieu\\image\\studying.png',4,1,'ACTIVE','Chi tiêu học tập, sách vở','2025-11-05 16:31:37','2025-11-16 05:29:19'),(5,'Thu nhập lương','THU',NULL,'E:\\HeThongQuanLyChiTieu\\image\\wage.png',5,1,'ACTIVE','Thu nhập từ lương hàng tháng','2025-11-05 16:31:37','2025-11-15 09:19:26'),(6,'Thu nhập kinh doanh','THU',NULL,'E:\\HeThongQuanLyChiTieu\\image\\online-shop.png',6,1,'ACTIVE','Thu nhập từ kinh doanh, buôn bán','2025-11-05 16:31:37','2025-11-15 09:18:59'),(7,'Đầu tư','THU',NULL,'E:\\HeThongQuanLyChiTieu\\image\\earning.png',7,1,'ACTIVE','Thu từ đầu tư, chứng khoán, tiền gửi','2025-11-05 16:31:37','2025-11-15 09:19:51'),(8,'Ăn sáng','CHI',1,'E:\\HeThongQuanLyChiTieu\\image\\a breakfast icon sho.png',1,2,'ACTIVE','Chi tiêu ăn sáng','2025-11-05 16:31:46','2025-11-15 15:26:13'),(9,'Ăn trưa','CHI',1,'E:\\HeThongQuanLyChiTieu\\image\\a dinner icon featur.png',2,2,'ACTIVE','Chi tiêu ăn trưa','2025-11-05 16:31:46','2025-11-15 15:26:38'),(10,'Ăn tối','CHI',1,'E:\\HeThongQuanLyChiTieu\\image\\a lunch icon featuri.png',3,2,'ACTIVE','Chi tiêu ăn tối','2025-11-05 16:31:46','2025-11-15 15:26:52'),(11,'Đồ ăn vặt','CHI',1,'E:\\HeThongQuanLyChiTieu\\image\\a snack icon featuri.png',4,2,'ACTIVE','Snack, đồ ăn nhẹ','2025-11-05 16:31:46','2025-11-15 15:28:24'),(12,'Xe bus','CHI',2,'E:\\HeThongQuanLyChiTieu\\image\\bus-stop.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:32:38'),(13,'Taxi/Grab','CHI',2,'E:\\HeThongQuanLyChiTieu\\image\\taxi.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:34:15'),(14,'Xăng xe','CHI',2,'E:\\HeThongQuanLyChiTieu\\image\\petrol.png',3,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:35:57'),(15,'Bảo dưỡng xe','CHI',2,'E:\\HeThongQuanLyChiTieu\\image\\service.png',4,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:37:23'),(16,'Xem phim','CHI',3,'E:\\HeThongQuanLyChiTieu\\image\\watching-a-movie.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:38:48'),(17,'Game','CHI',3,'E:\\HeThongQuanLyChiTieu\\image\\console.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:39:53'),(18,'Du lịch','CHI',3,'E:\\HeThongQuanLyChiTieu\\image\\travel-and-tourism.png',3,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:41:26'),(19,'Cafe & Bar','CHI',3,'E:\\HeThongQuanLyChiTieu\\image\\latte-art.png',4,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:53:25'),(20,'Sách','CHI',4,'E:\\HeThongQuanLyChiTieu\\image\\books.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:45:38'),(21,'Khóa học online','CHI',4,'E:\\HeThongQuanLyChiTieu\\image\\online-training.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:46:00'),(23,'Lương chính','THU',5,'E:\\HeThongQuanLyChiTieu\\image\\payday.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:57:04'),(24,'Thưởng','THU',5,'E:\\HeThongQuanLyChiTieu\\image\\incentive.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:57:53'),(25,'Bán hàng online','THU',6,'E:\\HeThongQuanLyChiTieu\\image\\shopping-online.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:54:10'),(26,'Bán quán cafe','THU',6,'E:\\HeThongQuanLyChiTieu\\image\\coffee-shop.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:53:49'),(27,'Chứng khoán','THU',7,'E:\\HeThongQuanLyChiTieu\\image\\buy.png',1,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:49:03'),(28,'Tiền gửi ngân hàng','THU',7,'E:\\HeThongQuanLyChiTieu\\image\\deposit.png',2,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:48:32'),(29,'Bất động sản','THU',7,'E:\\HeThongQuanLyChiTieu\\image\\growth.png',3,2,'ACTIVE','','2025-11-05 16:31:46','2025-11-15 15:50:02');
/*!40000 ALTER TABLE `danh_muc` ENABLE KEYS */;
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
