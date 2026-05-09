import React from "react";
import { Row, Col } from "react-bootstrap";

const amenitiesList = [
  { id: 1, name: "Điều hòa không khí", icon: "bi bi-snow" },
  { id: 2, name: "Dịch vụ đưa đón sân bay", icon: "bi bi-airplane" },
  { id: 3, name: "Gần khu mua sắm", icon: "bi bi-shop" },
  { id: 4, name: "Internet tốc độ cao", icon: "bi bi-wifi" },
  { id: 5, name: "Giường cỡ King", icon: "bi bi-door-closed" },
  { id: 6, name: "Dịch vụ cho thuê xe", icon: "bi bi-car-front" },
  { id: 7, name: "Dịch vụ phòng", icon: "bi bi-bell" },
  { id: 8, name: "Phòng tắm vòi sen", icon: "bi bi-droplet-fill" },
  { id: 9, name: "Giường đơn tiêu chuẩn", icon: "bi bi-person-workspace" },
  { id: 10, name: "Hồ bơi ngoài trời", icon: "bi bi-water" },
];

const Amenities = () => {
  return (
    <div className="mt-6 pt-6 border-t">
      <h3 className="fs-3 mb-4">Các tiện nghi</h3>

      <Row>
        {amenitiesList.map((item) => (
          <Col key={item.id} xs={12} sm={6} className="mb-2">
            <div className="items-center">
              <i className={`${item.icon} m-3`}></i>
              <span className="fs-5">{item.name}</span>
            </div>
          </Col>
        ))}
      </Row>
    </div>
  );
};

export default Amenities;
