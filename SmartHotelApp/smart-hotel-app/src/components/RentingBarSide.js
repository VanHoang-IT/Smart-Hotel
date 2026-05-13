import { useContext, useEffect, useState } from "react";
import { Container, Form, Button, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyBookingContext, MyUserContext } from "../configs/Contexts";
import Apis, { endpoints } from "../configs/Apis";
import cookies from 'react-cookies';

const RentingBarSide = ({ roomPrice = 0, roomId }) => {
  const navigate = useNavigate();
  const [booking, dispatch] = useContext(MyBookingContext);
  const [user] = useContext(MyUserContext);
  const [services, setServices] = useState([]);
  const [selectedServices, setSelectedServices] = useState([]);

  const todayStr = new Date().toISOString().split("T")[0];

  const handleAddToCart = () => {
    if (!booking.checkIn || !booking.checkOut) {
      alert("Vui lòng chọn ngày nhận và ngày trả phòng hợp lệ!");
      return;
    }

    let cart = cookies.load('cart') || {};

    cart[roomId] = {
      "id": roomId,
      "price": roomPrice,
      "checkIn": booking.checkIn,
      "checkOut": booking.checkOut,
      "quantity": 1,
      "services": selectedServices 
    };

    cookies.save('cart', cart, { path: '/' });

    dispatch({
      type: "UPDATE_BOOKING",
      payload: { roomsCount: Object.keys(cart).length }
    });

    alert("Đã thêm vào giỏ hàng tạm thời!");
    navigate("/cart");
  };

  const handleChange = (field, value) => {
    dispatch({
      type: "UPDATE_BOOKING",
      payload: { [field]: value },
    });
  };

  useEffect(() => {
    const loadServices = async () => {
      try {
        let res = await Apis.get(endpoints["extraServices"]);
        setServices(res.data);
      } catch (ex) {}
    };
    loadServices();
  }, []);

  const handleServiceChange = (serviceId) => {
    setSelectedServices(current =>
      current.includes(serviceId)
        ? current.filter((id) => id !== serviceId)
        : [...current, serviceId]
    );
  };

  const currentServicesPrice = services
    .filter(s => selectedServices.includes(s.id))
    .reduce((sum, s) => sum + s.price, 0);

  const totalPrice = roomPrice + currentServicesPrice;

  return (
    <div className="mt-4">
      <Container>
        <Row className="justify-content-end">
          <Col>
            <div className="border p-4 rounded bg-white shadow-sm">
              <h3 className="mb-4">ĐẶT PHÒNG {roomId}</h3>
              <Form.Group className="mb-4">
                <Form.Label>Ngày đến</Form.Label>
                <Form.Control
                  type="date"
                  value={booking.checkIn}
                  min={todayStr}
                  onChange={(e) => handleChange("checkIn", e.target.value)}
                />
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>Ngày đi</Form.Label>
                <Form.Control
                  type="date"
                  value={booking.checkOut}
                  min={booking.checkIn || todayStr}
                  onChange={(e) => handleChange("checkOut", e.target.value)}
                />
              </Form.Group>
              <div className="mt-4">
                <h5 className="mb-3">Dịch vụ cho phòng {roomId}</h5>
                <hr />
                {services.map((s) => (
                  <div key={s.id} className="d-flex justify-content-between align-items-center mb-2">
                    <Form.Check
                      type="checkbox"
                      id={`service-${roomId}-${s.id}`}
                      label={s.name}
                      checked={selectedServices.includes(s.id)}
                      onChange={() => handleServiceChange(s.id)}
                    />
                    <span className="text-muted small">{s.price.toLocaleString()} VNĐ</span>
                  </div>
                ))}
              </div>
              <div className="mt-4 p-3 rounded" style={{ backgroundColor: '#f8f9fa' }}>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Giá phòng:</span>
                    <span className="fw-semibold">{roomPrice.toLocaleString()} VNĐ</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Tiền dịch vụ:</span>
                    <span className="fw-semibold">{currentServicesPrice.toLocaleString()} VNĐ</span>
                </div>
                <hr />
                <div className="d-flex justify-content-between align-items-center">
                    <span className="fw-bold text-danger">TỔNG CỘNG:</span>
                    <span className="fw-bold text-danger fs-5">{totalPrice.toLocaleString()} VNĐ</span>
                </div>
              </div>
              <Button onClick={handleAddToCart} className="w-100 mt-3 fw-bold">
                THÊM VÀO GIỎ HÀNG
              </Button>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default RentingBarSide;