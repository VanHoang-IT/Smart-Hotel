import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
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
      {rooms.length == 0 && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}
      <div className="fs-2 font-bold"> DANH SÁCH PHÒNG TRỐNG</div>
      <Row>
        {rooms.map((r) => {
          return (
            <Col xs={6} md={4} lg={7} key={r.id} className="p-2 w-200 h-25 m-4">
              <Card className=" shadow-sm border-2 m-1">
                <Link to={`/rooms/${r.id}`}>
                  <div>
                    <Card.Img
                      variant="top"
                      src={r.mainImage}
                      style={{ height: "450px", objectFit: "cover" }}
                    />
                  </div>
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
                    className="mt-4 border-radius-5"
                  >
                    ĐẶT PHÒNG
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          );
        })}
      </Row>
      {loading && <MySpinner />}
    </>
  );
};
export default AvailableRooms;
