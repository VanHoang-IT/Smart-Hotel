import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { Row, Col, Card, Button } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert } from "react-bootstrap";
import RentingBarSide from "../../components/RentingBarSide";
import BookingBarSide from "../../components/BookingBarSide";

const AvailableRooms = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchParams] = useSearchParams();

  const checkIn = searchParams.get("checkIn");
  const checkOut = searchParams.get("checkOut");

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await Apis.get(endpoints.availableRooms, {
          params: { checkIn, checkOut },
        });
        setRooms(res.data);
      } catch (error) {
        console.error("Lỗi tải danh sách phòng trống:", error);
        setError("Lỗi tải danh sách phòng: " + (error.response?.data || error.message));
      } finally {
        setLoading(false);
      }
    };

    loadRooms();
  }, [checkIn, checkOut]);
  if (loading) return <MySpinner />;
  if (error) return <Alert variant="danger" className="mt-3 mx-3">{error}</Alert>;

  return (
    <>
      {!loading && rooms.length === 0 && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào trong khoảng thời gian này!
        </Alert>
      )}

      <div className="fs-2 fw-bold mb-3 m-3">
        DANH SÁCH PHÒNG TRỐNG
        {checkIn && checkOut && (
          <span className="fs-6 fw-normal text-muted ms-3">
            ({checkIn} → {checkOut})
          </span>
        )}
      </div>

      <div className="container">
        <Row>
          <Col lg={8}>
            <Row>
              {rooms.map((r) => (
                <Col xs={12} md={6} lg={12} key={r.id} className="p-2">
                  <Card className="shadow-sm border-2 m-1">
                    <Link to={`/rooms/${r.id}`}>
                      <Card.Img
                        variant="top"
                        src={r.mainImage}
                        style={{
                          height: "450px",
                          objectFit: "cover",
                        }}
                      />
                    </Link>

                    <Card.Body>
                      <Link
                        to={`/rooms/${r.id}`}
                        className="text-decoration-none text-dark"
                      >
                        <Card.Title>{r.name}</Card.Title>
                      </Link>

                      <Card.Text>
                        {Number(r.price).toLocaleString("vi-VN")} VND / đêm
                      </Card.Text>

                      <Card.Text>{r.note}</Card.Text>
                    </Card.Body>

                    <Card.Body>
                      <Button
                        as={Link}
                        to={`/rooms/${r.id}`}
                        variant="dark"
                        className="mt-4"
                      >
                        ĐẶT PHÒNG
                      </Button>
                    </Card.Body>
                  </Card>
                </Col>
              ))}
            </Row>
          </Col>
          <Col lg={4}>
            <BookingBarSide />
          </Col>
        </Row>
      </div>

    </>
  );
};
export default AvailableRooms;
