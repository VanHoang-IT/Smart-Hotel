import { useState } from "react";
import { useEffect } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert, Button, Card, Col, Row } from "react-bootstrap";
import { Link } from "react-router-dom";

const Home = () => {
  // const [roomTypes, setRoomTypes] = useState([]);
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

  // const loadRoomTypes = async () => {
  //   try {
  //     let res = await Apis.get(endpoints["roomTypes"]);
  //     setRoomTypes(res.data);
  //   } catch (ex) {
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  useEffect(() => {
    loadRooms();
    // loadRoomTypes();
  }, []);
  return (
    <>
      {rooms.length == 0 && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}
      <Row>
        {rooms.map((r) => {
          return (
            <Col xs={6} md={4} lg={6} key={r.id} className="p-2 w-200 h-25">
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

export default Home;
