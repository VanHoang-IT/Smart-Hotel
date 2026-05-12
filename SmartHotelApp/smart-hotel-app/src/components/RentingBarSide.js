import { useContext } from "react";
import { Container, Form, Button, Row, Col } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyBookingContext } from "../configs/Contexts";

const RentingBarSide = ({ room }) => {
  const navigate = useNavigate();

  const [booking, dispatch] = useContext(MyBookingContext);

  const todayStr = new Date().toISOString().split("T")[0];

  const isAvailable = room?.status === "AVAILABLE";

  const handleBooking = () => {
    if (!room || !isAvailable) return;

    dispatch({
      type: "ADD_ROOM",
      payload: {
        id: room.id,
        name: room.name,
        price: room.price,
      },
    });

    navigate("/checkout");
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
        <Row className="justify-content-end">
          <Col>
            <div className="border p-4 rounded">
              <h3 className="mb-4">CHI TIẾT ĐẶT PHÒNG</h3>

              <p>
                Trạng thái:{" "}
                <b className={isAvailable ? "text-success" : "text-danger"}>
                  {room?.status}
                </b>
              </p>

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

              <Form.Group className="mb-4">
                <Form.Label>Người lớn</Form.Label>
                <Form.Select
                  value={booking.adults}
                  onChange={(e) =>
                    handleChange("adults", Number(e.target.value))
                  }
                >
                  {[1, 2, 3, 4, 5].map((n) => (
                    <option key={n} value={n}>
                      {n}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              <Form.Group className="mb-4">
                <Form.Label>Trẻ em</Form.Label>
                <Form.Select
                  value={booking.children}
                  onChange={(e) =>
                    handleChange("children", Number(e.target.value))
                  }
                >
                  {[0, 1, 2, 3].map((n) => (
                    <option key={n} value={n}>
                      {n}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>

              <Button
                onClick={handleBooking}
                className="w-100 mt-3"
                disabled={!isAvailable}
                variant={isAvailable ? "primary" : "secondary"}
              >
                {isAvailable ? "Đặt phòng" : "Không khả dụng"}
              </Button>
            </div>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default RentingBarSide;
