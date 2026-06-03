# Smart Hotel

Smart Hotel là hệ thống quản lý và đặt phòng khách sạn được xây dựng: backend Java Spring MVC và frontend ReactJS. Hệ thống hỗ trợ khách hàng tìm kiếm phòng, đặt phòng, thanh toán, đánh giá dịch vụ; đồng thời cung cấp trang quản trị cho admin/nhân viên quản lý phòng, loại phòng, dịch vụ, đặt phòng, người dùng và thống kê doanh thu.

## Mục lục

* [Giới thiệu](#giới-thiệu)
* [Công nghệ sử dụng](#công-nghệ-sử-dụng)
* [Chức năng chính](#chức-năng-chính)
* [Cấu trúc thư mục](#cấu-trúc-thư-mục)
* [Cài đặt và chạy project](#cài-đặt-và-chạy-project)
* [Cấu hình database](#cấu-hình-database)
* [Một số module chính](#một-số-module-chính)
## Giới thiệu

Project **Smart Hotel** được phát triển nhằm mô phỏng một hệ thống quản lý khách sạn hiện đại. Ứng dụng cho phép khách hàng xem thông tin phòng, đặt phòng, thanh toán và đánh giá sau khi sử dụng dịch vụ. Bên cạnh đó, hệ thống cũng cung cấp các chức năng quản trị dành cho admin, nhân viên lễ tân và nhân viên khách sạn.

Project gồm 2 phần chính:

* **Backend:** Java Spring, Hibernate, MySQL, Spring Security, JWT, WebSocket.
* **Frontend:** ReactJS, Bootstrap, Axios, Chart.js, Firebase.

## Công nghệ sử dụng

### Backend

* Java 11
* Maven
* Spring MVC
* Spring Security
* Hibernate ORM
* MySQL
* Thymeleaf
* JWT
* Firebase Admin SDK
* WebSocket
* Cloudinary
* Jakarta Mail
* Docker

### Frontend

* ReactJS
* React Router DOM
* Axios
* Bootstrap
* React Bootstrap
* Chart.js
* Firebase
* SockJS Client
* STOMP
* QR Code React

## Chức năng chính

### Khách hàng

* Đăng ký tài khoản
* Đăng nhập / đăng xuất
* Xem danh sách phòng
* Xem chi tiết phòng
* Tìm kiếm phòng
* Đặt phòng
* Xem thông tin đặt phòng
* Thanh toán
* Đánh giá phòng / dịch vụ
* Cập nhật thông tin cá nhân
* Chat / nhắn tin hỗ trợ

### Admin

* Quản lý người dùng
* Quản lý phòng
* Quản lý loại phòng
* Quản lý dịch vụ khách sạn
* Quản lý đặt phòng
* Quản lý thanh toán
* Quản lý nhân viên
* Quản lý đánh giá
* Xem thống kê doanh thu
* Xem thống kê theo tháng / năm

### Nhân viên / lễ tân

* Xem danh sách đặt phòng
* Cập nhật trạng thái đặt phòng
* Hỗ trợ xử lý thanh toán
* Quản lý thông tin khách hàng
* Theo dõi tình trạng phòng

## Cấu trúc thư mục

```bash
Smart-Hotel/
│
├── SmartHotel/                     # Backend Java Spring
│   ├── src/
│   │   └── main/
│   │       ├── java/com/hvh/
│   │       │   ├── configs/        # Cấu hình hệ thống
│   │       │   ├── controllers/    # Controller xử lý request
│   │       │   ├── dto/            # Data Transfer Object
│   │       │   ├── filters/        # Bộ lọc request / security
│   │       │   ├── payment/        # Xử lý thanh toán
│   │       │   ├── pojo/           # Entity / Model
│   │       │   ├── repository/     # Tầng truy vấn dữ liệu
│   │       │   ├── service/        # Tầng xử lý nghiệp vụ
│   │       │   └── utils/          # Các tiện ích dùng chung
│   │       │
│   │       ├── resources/
│   │       │   ├── static/         # File tĩnh
│   │       │   └── templates/      # Giao diện Thymeleaf
│   │       │
│   │       └── webapp/
│   │
│   ├── pom.xml                     # Cấu hình Maven
│   ├── Dockerfile                  # Cấu hình Docker backend
│   └── docker-compose.yml          # Cấu hình chạy MySQL + app
│
├── SmartHotelApp/
│   └── smart-hotel-app/            # Frontend ReactJS
│       ├── public/
│       ├── src/
│       │   ├── assets/             # Hình ảnh, tài nguyên
│       │   ├── components/         # Component dùng chung
│       │   ├── configs/            # Cấu hình API, Firebase, Context
│       │   ├── reducers/           # Reducer quản lý state
│       │   ├── screens/            # Các màn hình chính
│       │   └── styles/             # CSS
│       │
│       └── package.json
│
├── smarthoteldb.sql                # File database mẫu
└── README.md
```

## Cài đặt và chạy project

### 1. Clone project

```bash
git clone https://github.com/VanHoang-IT/Smart-Hotel.git
cd Smart-Hotel
```

## Cấu hình database

Project sử dụng MySQL. File database mẫu nằm ở:

```bash
smarthoteldb.sql
```

### Cách 1: Chạy MySQL bằng Docker

Di chuyển vào thư mục backend:

```bash
cd SmartHotel
```

Chạy Docker Compose:

```bash
docker compose up -d
```

Theo cấu hình mặc định:

```bash
MYSQL_DATABASE=smarthoteldb
MYSQL_ROOT_PASSWORD=Abcd123
MYSQL_PORT=3307
```

Sau đó import file database:

```bash
mysql -u root -p -P 3307 smarthoteldb < ../smarthoteldb.sql
```

### Cách 2: Tạo database thủ công

Tạo database trong MySQL:

```sql
CREATE DATABASE smarthoteldb;
```

Sau đó import file:

```bash
mysql -u root -p smarthoteldb < smarthoteldb.sql
```

## Chạy backend

Di chuyển vào thư mục backend:

```bash
cd SmartHotel
```

Build project bằng Maven:

```bash
mvn clean package
```

Chạy project trên server hỗ trợ file `.war`, ví dụ Apache Tomcat.

Nếu chạy bằng Docker:

```bash
docker compose up --build
```

Backend mặc định chạy ở:

```bash
http://localhost:8080
```

## Chạy frontend

Di chuyển vào thư mục frontend:

```bash
cd SmartHotelApp/smart-hotel-app
```

Cài đặt thư viện:

```bash
npm install
```

Hoặc nếu dùng Yarn:

```bash
yarn install
```

Chạy frontend:

```bash
npm start
```

Hoặc:

```bash
yarn start
```

Frontend mặc định chạy ở:

```bash
http://localhost:3000
```

## Một số module chính

### Backend Controllers

Backend có các controller chính như:

* `ApiUserController`
* `ApiRoomController`
* `ApiRoomTypeController`
* `ApiReservationController`
* `ApiPaymentController`
* `ApiReviewController`
* `ApiServiceController`
* `ApiStaffController`
* `ApiAdminController`
* `ApiChatController`
* `StatisticController`

Các controller này chịu trách nhiệm nhận request từ client, gọi service tương ứng và trả dữ liệu về frontend.

### Backend Entities

Một số entity chính trong hệ thống:

* `User`
* `CustomerProfile`
* `Room`
* `RoomType`
* `RoomImages`
* `Reservation`
* `ReservationRoom`
* `Payment`
* `Review`
* `Services`
* `ServiceOrder`
* `HousekeepingTask`
* `CartItem`

Các entity này đại diện cho các bảng dữ liệu trong hệ thống khách sạn.

### Frontend Screens

Frontend được chia theo từng nhóm màn hình:

* `Home`
* `Room`
* `Reservation`
* `Checkout`
* `Payment`
* `Review`
* `User`
* `Admin`
* `Staff`
* `Receptionist`

Cách chia này giúp project dễ quản lý, mỗi nhóm màn hình phụ trách một chức năng riêng.

## API và cấu hình

Các cấu hình gọi API được đặt trong thư mục:

```bash
SmartHotelApp/smart-hotel-app/src/configs
```

Một số file cấu hình chính:

* `Apis.js`: cấu hình endpoint và Axios
* `Contexts.js`: quản lý context người dùng
* `Firebase.js`: cấu hình Firebase
* `Chat.js`: cấu hình chat / realtime

## Thanh toán

Project có module thanh toán riêng trong backend:

```bash
SmartHotel/src/main/java/com/hvh/payment
```

Frontend cũng có màn hình thanh toán tại:

```bash
SmartHotelApp/smart-hotel-app/src/screens/Payment
```

Module này dùng để xử lý luồng thanh toán khi khách hàng đặt phòng.

## Thống kê

Hệ thống có chức năng thống kê phục vụ admin, ví dụ:

* Thống kê doanh thu
* Thống kê theo năm
* Thống kê theo tháng
* Thống kê số lượng đặt phòng
* Tổng quan hoạt động khách sạn

Controller liên quan:

```bash
StatisticController.java
```

## Docker

```bash
Dockerfile
docker-compose.yml
```

File `docker-compose.yml` cấu hình 2 service chính:

* `mysql`: database MySQL
* `app`: ứng dụng backend

Chạy toàn bộ bằng lệnh:

```bash
cd SmartHotel
docker compose up --build
```
