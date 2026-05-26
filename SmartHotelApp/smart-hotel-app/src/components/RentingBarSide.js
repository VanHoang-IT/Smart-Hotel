import { useContext, useEffect, useState } from "react";
import { Container, Form, Button, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyBookingContext } from "../configs/Contexts";
import Apis, { endpoints } from "../configs/Apis";
import cookies from 'react-cookies';

const getNights = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return 1;

  const start = new Date(checkIn);
  const end = new Date(checkOut);
  const diff = end.getTime() - start.getTime();
  const nights = Math.ceil(diff / (1000 * 60 * 60 * 24));

  return Math.max(nights, 1);
};

const RentingBarSide = ({ roomPrice = 0, roomId }) => {
  const navigate = useNavigate();
  const [booking, dispatch] = useContext(MyBookingContext);
  const [services, setServices] = useState([]);
  const [selectedServices, setSelectedServices] = useState([]);

  const todayStr = new Date().toISOString().split("T")[0];

  const isBlocked = (() => {
    if (!booking.checkIn || !booking.checkOut || !booking.bookedDates?.length) return false;
    const start = new Date(booking.checkIn);
    const end = new Date(booking.checkOut);
    return booking.bookedDates.some((d) => {
      const date = new Date(d);
      return date >= start && date <= end;
    });
  })();

  const handleAddToCart = () => {
    if (!booking.checkIn || !booking.checkOut) {
      alert("Vui lÃ²ng chá»n ngÃ y nháº­n vÃ  ngÃ y tráº£ phÃ²ng há»£p lá»‡!");
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
    .reduce((sum, s) => sum + Number(s.price), 0);

  const nights = getNights(booking.checkIn, booking.checkOut);
  const roomTotal = Number(roomPrice) * nights;
  const totalPrice = roomTotal + currentServicesPrice;

  return (
    <div className="mt-4">
      <Container>
        <Row className="justify-content-end">
          <Col>
            <div className="border p-4 rounded bg-white shadow-sm">
              <h3 className="mb-4">Ngày đặt phòng</h3>
              <Form.Group className="mb-4">
                <Form.Label>CheckIn</Form.Label>
                <Form.Control
                  type="date"
                  value={booking.checkIn}
                  min={todayStr}
                  onChange={(e) => handleChange("checkIn", e.target.value)}
                />
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>CheckOut</Form.Label>
                <Form.Control
                  type="date"
                  value={booking.checkOut}
                  min={booking.checkIn || todayStr}
                  onChange={(e) => handleChange("checkOut", e.target.value)}
                />
              </Form.Group>
              <div className="mt-4">
                <h5 className="mb-3">Danh sách dịch vụ phòng {roomId}</h5>
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
                    <span className="fw-semibold">
                      {roomTotal.toLocaleString()} VNĐ
                      <small className="d-block text-end text-muted">
                        {Number(roomPrice).toLocaleString()} x {nights} đêm
                      </small>
                    </span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Tiền dịch vụ</span>
                    <span className="fw-semibold">{currentServicesPrice.toLocaleString()} VNĐ</span>
                </div>
                <hr />
                <div className="d-flex justify-content-between align-items-center">
                    <span className="fw-bold text-danger">Tổng tiền:</span>
                    <span className="fw-bold text-danger fs-5">{totalPrice.toLocaleString()} VNĐ</span>
                </div>
              </div>
              <Button
                onClick={handleAddToCart}
                disabled={isBlocked}
                variant={isBlocked ? "secondary" : "primary"}
                className="w-100 mt-3 fw-bold"
              >
                {isBlocked ? "Phòng đã được đặt vào ngày này" : "Đặt phòng ngay"}
              </Button>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default RentingBarSide;
