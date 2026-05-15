import { useContext } from "react";
import { Container, Form, Button, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyBookingContext } from "../configs/Contexts";

const BookingBarSide = () => {
  const navigate = useNavigate();
  const [booking, dispatch] = useContext(MyBookingContext);

  const todayStr = new Date().toISOString().split("T")[0];

  const handleBooking = () => {
    navigate(
      `/available?checkIn=${booking.checkIn}&checkOut=${booking.checkOut}`,
    );
  };

  const handleChange = (field, value) => {
    dispatch({
      type: "UPDATE_BOOKING",
      payload: {
        [field]: value,
      },
    });
  };

  return (
    <div className="mt-4">
      <Container>
        <Row className="justify-content-center">
          <Col>
            <div className="border p-4 rounded">
              <h3 className="mb-4">KIỂM TRA PHÒNG TRỐNG</h3>

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

              <Button onClick={handleBooking} className="w-100 mt-3">
                KIỂM TRA
              </Button>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default BookingBarSide;