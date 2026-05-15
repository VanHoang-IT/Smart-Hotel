import { useContext } from "react";
import { Container, Row, Col, Form, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyBookingContext } from "../configs/Contexts";

const BookingBar = () => {
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
          <Col md={8} lg={6}>
            <div className="border p-3 rounded">
              <Row className="g-2 align-items-end">
                <Col md={5}>
                  <Form.Group>
                    <Form.Label>Ngày đến</Form.Label>

                    <Form.Control
                      type="date"
                      value={booking.checkIn}
                      min={todayStr}
                      onChange={(e) => handleChange("checkIn", e.target.value)}
                    />
                  </Form.Group>
                </Col>

                <Col md={5}>
                  <Form.Group>
                    <Form.Label>Ngày đi</Form.Label>

                    <Form.Control
                      type="date"
                      value={booking.checkOut}
                      min={todayStr}
                      onChange={(e) => handleChange("checkOut", e.target.value)}
                    />
                  </Form.Group>
                </Col>

                <Col md={2}>
                  <Button onClick={handleBooking} className="w-100">
                    ĐẶT PHÒNG
                  </Button>
                </Col>
              </Row>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default BookingBar;