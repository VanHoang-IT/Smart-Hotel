-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: smarthoteldb
-- ------------------------------------------------------
-- Server version	9.3.0

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
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `loyalty_point` int DEFAULT '0',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
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
  `task` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('TODO','IN_PROGRESS','DONE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TODO',
  `assignee_id` bigint DEFAULT NULL,
  `due_time` timestamp NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
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
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reservation_id` bigint NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `method` enum('CASH','CARD','TRANSFER','E_WALLET') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('PENDING','COMPLETED','FAILED','REFUNDED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
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
  `check_in` date DEFAULT NULL,
  `check_out` date DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
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
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
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
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `visible` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  CONSTRAINT `review_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
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
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_type_id` bigint NOT NULL,
  `floor` int DEFAULT NULL,
  `status` enum('AVAILABLE','OCCUPIED','CLEANING','MAINTENANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'AVAILABLE',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(12,2) NOT NULL DEFAULT '300000.00',
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `room_type_id` (`room_type_id`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,'Modern Home',1,1,'AVAILABLE','https://alloggio.qodeinteractive.com/wp-content/uploads/2020/03/h1-room-img-04.jpg',550000.00,'Không gian hiện đại, thiết kế tối giản nhưng đầy đủ tiện nghi, phù hợp cho những ai yêu thích sự tinh tế và thoải mái.'),(2,'Casa Mancini',1,1,'AVAILABLE','https://alloggio.qodeinteractive.com/wp-content/uploads/2020/03/room-featured-img-06.jpg',550000.00,'Phòng mang phong cách Ý sang trọng, ấm cúng với nội thất tinh xảo, đem lại cảm giác thư giãn và đẳng cấp.'),(3,'Bright Suite',2,2,'AVAILABLE','https://alloggio.qodeinteractive.com/wp-content/uploads/2020/03/room-featured-img-01.jpg',850000.00,'Căn phòng tràn ngập ánh sáng tự nhiên, rộng rãi và thoáng đãng, lý tưởng cho kỳ nghỉ nhẹ nhàng và dễ chịu.'),(4,'Sea Home',2,2,'AVAILABLE','https://alloggio.qodeinteractive.com/wp-content/uploads/2020/03/room-single-gallery-img-15.jpg',850000.00,'Không gian hướng biển mát mẻ, thiết kế gần gũi thiên nhiên, mang lại trải nghiệm nghỉ dưỡng thư thái.'),(5,'House Ciardi',3,5,'AVAILABLE','https://alloggio.qodeinteractive.com/wp-content/uploads/2020/03/room-featured-img-10.jpg',1250000.00,'Phòng mang phong cách cổ điển pha chút hiện đại, ấm áp và riêng tư, phù hợp cho những ai tìm kiếm sự yên bình.');
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
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `room_images_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room_images`
--

LOCK TABLES `room_images` WRITE;
/*!40000 ALTER TABLE `room_images` DISABLE KEYS */;
INSERT INTO `room_images` VALUES (1,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056149/copy_of_1fc23ac7-2026-4df9-89a8-88d994c54117_ed1952.png',1),(2,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056112/2_n8ikdo_529d6c.jpg',1),(3,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056317/4_xd81vt_4fbfca.jpg',3),(4,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056317/4_xd81vt_4fbfca.jpg',3),(6,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056260/2_igyul7_e4f6bc.jpg',2),(7,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056256/3_b9cv9w_e4f6bc.jpg',2),(8,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056253/4_clbokl_e4f6bc.jpg',2),(9,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056248/1_ttlklt_e4f6bc.jpg',2),(10,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056325/3_taxxki_4fbfca.jpg',3),(11,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056322/2_oj38q6_4fbfca.jpg',3),(12,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056383/1_v7ik4u_5d3a54.jpg',4),(13,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056377/2_lyz6wg_5d3a54.jpg',4),(14,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056372/3_end6an_5d3a54.jpg',4),(15,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056368/4_eblwoy_5d3a54.jpg',4),(16,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056423/1_mzoonv_fed253.jpg',5),(17,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056420/3_gu2cwg_fed253.jpg',5),(18,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056417/2_rsgb8n_fed253.jpg',5),(19,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056413/4_feayjc_fed253.jpg',5),(20,'https://res.cloudinary.com/dlwy7kulj/image/upload/v1778056107/3_j2hxa4_529d6c.jpg',1);
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
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
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
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reservation_id` (`reservation_id`),
  KEY `service_id` (`service_id`),
  CONSTRAINT `service_order_ibfk_1` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `service_order_ibfk_2` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_order`
--

LOCK TABLES `service_order` WRITE;
/*!40000 ALTER TABLE `service_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (2,'Dọn phòng',300000.00,'Tính theo kg đồ thường',1),(3,'Đưa đón',400000.00,'Dịch vụ xe riêng tiện lợi, đặt lịch trước để đảm bảo đúng giờ.',1),(4,'Ẩm thực cá nhân',900000.00,'Bữa ăn riêng tư với thực đơn chọn sẵn, phục vụ tại phòng hoặc nhà hàng.',1),(5,'Ẩm thực couple',1700000.00,'Không gian lãng mạn với set menu đặc biệt, phù hợp cho những dịp đặc biệt.',1);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ROLE_ADMIN','RECEPTIONIST','ROLE_CUSTOMER','ROLE_STAFF') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ROLE_CUSTOMER',
  `enabled` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin@smarthotel.com','admin','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Nguyễn Quản Trị','0901112220','ROLE_ADMIN',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(2,'reception@smarthotel.com','letan01','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Trần Thị Lễ Tân','0903334440','RECEPTIONIST',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(3,'customer01@gmail.com','khachhang01','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Lê Văn Khách','0905556660','ROLE_CUSTOMER',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(4,'customer02@gmail.com','khachhang02','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Phạm Thị Guest','0907778880','ROLE_CUSTOMER',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(5,'2351050032do@ou.edu.vn','ngocdo','$2a$10$Wu0k4J7GuY9525iXYo8XteR9YAcJ0uvpY80Gm4HoZSpw8DVlyI/r2','Nguyễn Ngọc Đô','0243423430','ROLE_CUSTOMER',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775385843/ifzntvowhzlpgutstkkq.jpg'),(6,'2351050047hoang@ou.edu.vn ','hoang','$2a$10$auLcOBgZaWB6P3lnC5f.aOjEt8I.ry6IslhgrEq8MlawN.P0mp.My','Huỳnh Văn Hoàng','0534235460','ROLE_CUSTOMER',1,'2026-04-15 21:24:43','https://res.cloudinary.com/dlwy7kulj/image/upload/v1775204528/cloud_abmgyq.png'),(8,'test@gmail.com','staff02','$2a$10$5C79grQnLLoWTHJpSCG7muW2ZJbPmdF91ru5.ab7yrFrnr3dCLj8K','Nhân Viên 02','0435234350','ROLE_STAFF',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1778077069/so8nngyj4b1sheatteb9.png'),(9,'0335823877v123h@gmail.com','staff03','$2a$10$5FswGPp2opWOQDCJNBZm/uT75Rk70/JgnkMdH587zKcGvOdvu7rFG','Nhân Viên 01','0234263450','ROLE_STAFF',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1778077173/edkjzjosmwpi8bnaj7mg.jpg'),(10,'0335823877vh222@gmail.com','staff04','$2a$10$SY2vmfzoT3CLvvKElfghtewliFC4CnRtRxBu0YIKZM5JNwuOYIVOa','Nhân Viên 03','0335823877','ROLE_STAFF',1,'2026-04-02 11:58:53','https://res.cloudinary.com/dlwy7kulj/image/upload/v1778078210/jzqyd5zjlcfo03zjuj11.jpg');
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

-- Dump completed on 2026-05-21 17:35:57
