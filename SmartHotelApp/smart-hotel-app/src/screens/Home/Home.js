import { use, useState } from "react";
import { useEffect } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Button, Card, Col, Row } from "react-bootstrap";

const Home = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadRoom = async () => {
    try {
      let res = await Apis.get(endpoints["room"]);
      setRoom(res.data);
    } catch (ex) {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRoom();
  }, []);

  return (
    <>
      <Row>
        {room.map((r) => (
          <>
            <Col xs={6} md={3} key={r.id}>
              <Card>
                <Card.Img variant="top" src={r.mainImage} />
                <Card.Body>
                  <Card.Title>{r.name}</Card.Title>
                  <Card.Text>{r.price} VND</Card.Text>
                </Card.Body>
                <Card.Body>
                  <Button variant="danger">Đặt phòng</Button>
                  <Card.Link href="#">Another Link</Card.Link>
                </Card.Body>
              </Card>
            </Col>
          </>
        ))}
      </Row>
      {loading && <MySpinner />}
    </>
  );
};

export default Home;
