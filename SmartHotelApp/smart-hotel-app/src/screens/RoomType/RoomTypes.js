import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Row, Col, Card, Button, Badge, Alert } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import BookingBarSide from "../../components/BookingBarSide";


const renderRoomStatus = (status) => {
  switch (status) {
    case "AVAILABLE":
      return <Badge bg="success">Sẵn sàng</Badge>;
    case "OCCUPIED":
      return <Badge bg="danger">Đã đặt trước</Badge>;
    case "CLEANING":
      return <Badge bg="warning" text="dark">Đang dọn dẹp</Badge>;
    case "MAINTENANCE":
      return <Badge bg="secondary">Đang bảo trì</Badge>;
    default:
      return <Badge bg="info">{status}</Badge>;
  }
};

const RoomType = () => {
  const { typeId } = useParams();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [typeName, setTypeName] = useState("");
  const [typeDescription, setTypeDescription] = useState("");

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);
        const res = await Apis.get(`${endpoints["rooms"]}?typeId=${typeId}`);
        setRooms(res.data);
        if (res.data.length > 0) {
          setTypeName(res.data[0].roomType?.name || "");
          setTypeDescription(res.data[0].roomType?.description || "");
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    loadRooms();
  }, [typeId]);

  if (loading) return <MySpinner />;

  return (
    <Container className="my-5">
      <Row>
        <Col lg={8}>
          <h2 className="text-uppercase fs-1 mb-3">{typeName || "Danh mục phòng"}</h2>
          {typeDescription && <p className="text-muted fs-5 mb-3">{typeDescription}</p>}
          <hr className="mb-4" />

          {rooms.length === 0 && (
            <Alert variant="info">KHÔNG có phòng nào!</Alert>
          )}

          <Row>
            {rooms.map((r) => {
              const isOccupied = r.status === "OCCUPIED";
              return (
                <Col xs={12} md={6} key={r.id} className="mb-4">
                  <Card className="shadow-sm border-2 h-100">
                    <Link
                      to={isOccupied ? "#" : `/rooms/${r.id}`}
                      style={{ pointerEvents: isOccupied ? "none" : "auto" }}
                    >
                      <Card.Img
                        variant="top"
                        src={r.mainImage}
                        style={{ height: 250, objectFit: "cover" }}
                      />
                    </Link>
                    <Card.Body>
                      <Link
                        to={isOccupied ? "#" : `/rooms/${r.id}`}
                        className="text-decoration-none text-dark"
                        style={{ pointerEvents: isOccupied ? "none" : "auto" }}
                      >
                        <Card.Title>{r.name}</Card.Title>
                      </Link>
                      <Card.Text>
                        {Number(r.price).toLocaleString("vi-VN")} VND / đêm
                      </Card.Text>
                      <Card.Text>
                        <strong>Trạng thái: </strong>{renderRoomStatus(r.status)}
                      </Card.Text>
                      <Card.Text className="text-muted">{r.note}</Card.Text>
                    </Card.Body>
                    <Card.Body>
                      <Button
                        as={isOccupied ? "button" : Link}
                        to={isOccupied ? undefined : `/rooms/${r.id}`}
                        disabled={isOccupied}
                        variant={isOccupied ? "secondary" : "dark"}
                      >
                        {isOccupied ? "ĐÃ ĐẶT TRƯỚC" : "ĐẶT PHÒNG"}
                      </Button>
                    </Card.Body>
                  </Card>
                </Col>
              );
            })}
          </Row>
        </Col>

        <Col lg={4}>
          <BookingBarSide />
        </Col>
      </Row>
    </Container>
  );
};

export default RoomType;
