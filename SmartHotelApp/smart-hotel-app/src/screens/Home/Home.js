import { useState } from "react";
import { useEffect } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert, Button, Card, Col, Row, Badge } from "react-bootstrap";
import { Link } from "react-router-dom";

const Home = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadRooms = async () => {
    try {
      let res = await Apis.get(endpoints["rooms"]);
      setRooms(res.data);
    } catch (ex) {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRooms();
  }, []);

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

  return (
    <>
      {rooms.length === 0 && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}
      <Row>
        {rooms.map((r) => {
          const isOccupied = r.status === "OCCUPIED";

          return (
            <Col xs={6} md={4} lg={6} key={r.id} className="p-2 w-200 h-25">
              <Card className=" shadow-sm border-2 m-1">
                <Link to={isOccupied ? "#" : `/rooms/${r.id}`} style={{ pointerEvents: isOccupied ? 'none' : 'auto' }}>
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
                    to={isOccupied ? "#" : `/rooms/${r.id}`}
                    className="text-decoration-none text-dark"
                    style={{ pointerEvents: isOccupied ? 'none' : 'auto' }}
                  >
                    <Card.Title>{r.name}</Card.Title>
                  </Link>
                  <Card.Text>
                    {Number(r.price).toLocaleString("vi-VN")} VND / đêm
                  </Card.Text>
                  
                  <Card.Text>
                    <strong>Trạng thái: </strong> {renderRoomStatus(r.status)}
                  </Card.Text>
                  <Card.Text>{r.note}</Card.Text>
                </Card.Body>

                <Card.Body>
                  <Button
                    as={isOccupied ? "button" : Link}
                    to={isOccupied ? undefined : `/rooms/${r.id}`}
                    disabled={isOccupied}
                    variant={isOccupied ? "secondary" : "dark"}
                    className="mt-4 border-radius-5"
                  >
                    {isOccupied ? "ĐÃ ĐẶT TRƯỚC" : "ĐẶT PHÒNG"}
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

export default Home;