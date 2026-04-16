CREATE DATABASE  IF NOT EXISTS `smarthoteldb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smarthoteldb`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: smarthoteldb
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `customer_profile`
--

DROP TABLE IF EXISTS `customer_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `loyalty_point` int DEFAULT '0',
  `notes` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `customer_profile_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_profile`
--

LOCK TABLES `customer_profile` WRITE;
/*!40000 ALTER TABLE `customer_profile` DISABLE KEYS */;
INSERT INTO `customer_profile` VALUES (1,3,'1990-05-15','123 Nguyễn Huệ, TP.HCM',150,'Khách thích phòng tầng cao'),(2,4,'1995-10-20','456 Lê Lợi, Đà Nẵng',50,'Dị ứng với đậu phộng');
/*!40000 ALTER TABLE `customer_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `housekeeping_task`
--

DROP TABLE IF EXISTS `housekeeping_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `housekeeping_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `task` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('TODO','IN_PROGRESS','DONE') COLLATE utf8mb4_unicode_ci DEFAULT 'TODO',
  `assignee_id` bigint DEFAULT NULL,
  `due_time` timestamp NULL DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  KEY `assignee_id` (`assignee_id`),
  CONSTRAINT `housekeeping_task_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE,
  CONSTRAINT `housekeeping_task_ibfk_2` FOREIGN KEY (`assignee_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `housekeeping_task`
--

LOCK TABLES `housekeeping_task` WRITE;
/*!40000 ALTER TABLE `housekeeping_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `housekeeping_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `issued_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `reservation_id` (`reservation_id`),
  CONSTRAINT `invoice_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,1,2400000.00,'2026-04-02 04:59:14');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `method` enum('CASH','CARD','TRANSFER','E_WALLET') COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','COMPLETED','FAILED','REFUNDED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,2400000.00,'CARD',NULL,'COMPLETED','2026-04-02 04:59:14','2026-04-02 04:59:14');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `status` enum('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_reservation_dates` (`check_in`,`check_out`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer_profile` (`id`),
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,1,'2024-04-01','2024-04-03','CONFIRMED',2,'2026-04-02 04:59:14','2026-04-02 04:59:14'),(2,2,'2024-04-05','2024-04-10','PENDING',NULL,'2026-04-02 04:59:14','2026-04-02 04:59:14');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation_room`
--

DROP TABLE IF EXISTS `reservation_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `price_per_night` decimal(12,2) NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `reservation_room_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservation_room_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation_room`
--

LOCK TABLES `reservation_room` WRITE;
/*!40000 ALTER TABLE `reservation_room` DISABLE KEYS */;
INSERT INTO `reservation_room` VALUES (1,1,4,1200000.00,NULL),(2,2,5,3500000.00,NULL);
/*!40000 ALTER TABLE `reservation_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `rating` int DEFAULT NULL,
  `comment` text COLLATE utf8mb4_unicode_ci,
  `visible` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_type_id` bigint NOT NULL,
  `room_number` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `floor` int DEFAULT NULL,
  `status` enum('AVAILABLE','OCCUPIED','CLEANING','MAINTENANCE') COLLATE utf8mb4_unicode_ci DEFAULT 'AVAILABLE',
  `main_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(12,2) NOT NULL DEFAULT '300000.00',
  `note` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `room_number` (`room_number`),
  KEY `room_type_id` (`room_type_id`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,1,'101',1,'AVAILABLE','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?q=80&w=1000',300000.00,'Gần thang máy'),(2,1,'102',1,'CLEANING','https://images.unsplash.com/photo-1595576508898-0ad5c879a061?q=80&w=1000',300000.00,NULL),(3,2,'201',2,'AVAILABLE','https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=1000',300000.00,'View đẹp nhất tầng 2'),(4,2,'202',2,'OCCUPIED','https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1000',300000.00,'Khách đang ở'),(5,3,'501',5,'AVAILABLE','https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=1000',300000.00,'Penthouse'),(8,1,'504',5,NULL,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775138652/xinsorvp5mgjpwalbjjv.png',300000.00,NULL),(67,1,'401',4,NULL,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775189674/lklhgx1sjv4ktchi8xs5.png',300000.00,NULL),(68,3,'304',3,NULL,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775189820/jjievys8kq7j8kg0nlc6.png',300000.00,NULL);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_images`
--

DROP TABLE IF EXISTS `room_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `room_images_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_images`
--

LOCK TABLES `room_images` WRITE;
/*!40000 ALTER TABLE `room_images` DISABLE KEYS */;
INSERT INTO `room_images` VALUES (1,'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?q=80&w=500',1),(2,'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?q=80&w=500',1),(3,'https://images.unsplash.com/photo-1560448204-603b3fc33ddc?q=80&w=500',3),(4,'https://images.unsplash.com/photo-1591088398332-8a7791972843?q=80&w=500',3);
/*!40000 ALTER TABLE `room_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room_type`
--

DROP TABLE IF EXISTS `room_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_type`
--

LOCK TABLES `room_type` WRITE;
/*!40000 ALTER TABLE `room_type` DISABLE KEYS */;
INSERT INTO `room_type` VALUES (1,'Standard Single',1,'Phòng đơn tiêu chuẩn, đầy đủ tiện nghi cơ bản',1),(2,'Deluxe Double',2,'Phòng đôi cao cấp, hướng biển, có ban công',1),(3,'VIP Suite',4,'Phòng hạng sang, phòng khách riêng, bồn tắm nằm',1);
/*!40000 ALTER TABLE `room_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,'Ăn sáng tại phòng',150000.00,'Combo bún bò + cà phê sữa',1),(2,'Giặt ủi',50000.00,'Tính theo kg đồ thường',1),(3,'Spa & Massage',500000.00,'Liệu trình 60 phút tinh dầu',1);
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_order`
--

DROP TABLE IF EXISTS `service_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  `qty` int DEFAULT '1',
  `unit_price` decimal(12,2) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `ordered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `notes` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `service_order_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `service_order_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_order`
--

LOCK TABLES `service_order` WRITE;
/*!40000 ALTER TABLE `service_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ADMIN','RECEPTIONIST','HOUSEKEEPING','CUSTOMER') COLLATE utf8mb4_unicode_ci DEFAULT 'CUSTOMER',
  `enabled` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin@smarthotel.com','admin','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Nguyễn Quản Trị','090111222','ADMIN',1,'2026-04-02 04:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(2,'reception@smarthotel.com','letan01','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Trần Thị Lễ Tân','090333444','RECEPTIONIST',1,'2026-04-02 04:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(3,'customer01@gmail.com','khachhang01','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Lê Văn Khách','090555666','CUSTOMER',1,'2026-04-02 04:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(4,'customer02@gmail.com','khachhang02','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Phạm Thị Guest','090777888','CUSTOMER',1,'2026-04-02 04:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(5,'a@gmail.com','a','$2a$10$Wu0k4J7GuY9525iXYo8XteR9YAcJ0uvpY80Gm4HoZSpw8DVlyI/r2','b','1',NULL,NULL,NULL,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775385843/ifzntvowhzlpgutstkkq.jpg'),(6,'vh@gmail.com','hoang','123456','a','2','CUSTOMER',1,'2026-04-15 14:24:43','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-16 12:25:04
