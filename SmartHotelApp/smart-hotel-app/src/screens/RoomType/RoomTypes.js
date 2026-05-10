import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Container, Row, Col, Card, Button } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import BookingBarSide from "../../components/BookingBarSide";

const typeDescriptions = {
  "Standard Single":
    "Không gian gọn gàng, tiện nghi cơ bản, lý tưởng cho khách đi một mình.",
  "Deluxe Double":
    "Phòng rộng rãi với giường đôi thoải mái, phù hợp cho cặp đôi hoặc nghỉ dưỡng.",
  "VIP Suite":
    "Hạng phòng cao cấp, không gian riêng biệt và tiện nghi sang trọng.",
};

const RoomType = () => {
  const { typeId } = useParams();
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [typeName, setTypeName] = useState("");

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);

        let res = await Apis.get(`${endpoints["rooms"]}?typeId=${typeId}`);

        setRooms(res.data);

        if (res.data.length > 0) {
          setTypeName(res.data[0].roomType?.name || "");
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    loadRooms();
  }, [typeId]);

  const Descriptions = typeDescriptions[typeName] || typeDescriptions.Default;

  if (loading) return <MySpinner />;

  return (
    <Container className="my-5">
      <Row>
        <Col lg={8}>
          <h2 className="text-uppercase fs-1 mb-3">
            {typeName || "Danh mục phòng"}
          </h2>

          <p className="text-muted fs-5 mb-3">{Descriptions}</p>

          <hr className="mb-4 w-27" />

          <Row>
            {rooms.length === 0 && (
              <Col>
                <div className="alert alert-info">KHÔNG có phòng nào!</div>
              </Col>
            )}

            {rooms.map((r) => (
              <Col xs={12} md={6} lg={12} key={r.id} className="mb-4">
                <Card className="h-100 shadow-sm border-2 m-4">
                  <Link to={`/rooms/${r.id}`}>
                    <Card.Img src={r.mainImage} />
                  </Link>

                  <Card.Body>
                    <Card.Title className="font-bold fs-3">
                      <Link
                        to={`/rooms/${r.id}`}
                        className="text-decoration-none text-dark"
                      >
                        {r.name}
                      </Link>
                    </Card.Title>

                    <Card.Text>
                      {Number(r.price).toLocaleString("vi-VN")} VND / đêm
                    </Card.Text>

                    <Card.Text className="f6-6 text-muted">{r.note}</Card.Text>

                    <Button
                      as={Link}
                      to={`/rooms/${r.id}`}
                      variant="dark"
                      size="fs-6"
                    >
                      ĐẶT PHÒNG
                    </Button>
                  </Card.Body>
                </Card>

                <hr className="mt-2 w-27" />
              </Col>
            ))}
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
