import { useState } from "react";
import { Container, Row, Col, Form, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const BookingBar = () => {
  const navigate = useNavigate();
  const { checkIn, setCheckIn, checkOut, setCheckOut } = useState("");

  const todayStr = new Date().toISOString().split("T")[0];

  const handleBooking = () => {
    navigate(`/available?checkIn=${checkIn}&checkOut=${checkOut}`);
  };

  return (
    <div className="mt-4">
      <Container>
        <div className="border p-3">
          <Row className="g-2 align-items-end">
            <Col md={3}>
              <Form.Group>
                <Form.Label>Ngày đến</Form.Label>
                <Form.Control
                  type="date"
                  value={checkIn}
                  min={todayStr}
                  onChange={(e) => setCheckIn(e.target.value)}
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Ngày đi</Form.Label>
                <Form.Control
                  type="date"
                  value={checkOut}
                  min={todayStr}
                  onChange={(e) => setCheckOut(e.target.value)}
                />
              </Form.Group>
            </Col>

            <Col md={2}>
              <Form.Group>
                <Form.Label>Người lớn</Form.Label>
                <Form.Select>
                  {[1, 2, 3, 4, 5].map((n) => (
                    <option key={n}>{n}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>

            <Col md={2}>
              <Form.Group>
                <Form.Label>Trẻ em</Form.Label>
                <Form.Select>
                  {[0, 1, 2, 3].map((n) => (
                    <option key={n}>{n}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>

            <Col md={2}>
              <Button onClick={handleBooking} className="w-100">
                ĐẶT PHÒNG
              </Button>
            </Col>
          </Row>
        </div>
      </Container>
    </div>
  );
};

export default BookingBar;
