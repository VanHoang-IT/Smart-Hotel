import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Row, Col, Card, Button } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert } from "react-bootstrap";

const AvailableRooms = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);
        const res = await Apis.get(endpoints.availableRooms);
        setRooms(res.data);
      } catch (error) {
        console.error("Lỗi tải danh sách phòng trống:", error);
      } finally {
        setLoading(false);
      }
    };

    loadRooms();
  }, []);
  return (
    <>
      {rooms.length === 0 && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}

      <div className="fs-2 fw-bold mb-3 m-3">DANH SÁCH PHÒNG TRỐNG</div>

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
                        to={`/checkout/${r.id}`}
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
        </Row>
      </div>

      {loading && <MySpinner />}
    </>
  );
};
export default AvailableRooms;
