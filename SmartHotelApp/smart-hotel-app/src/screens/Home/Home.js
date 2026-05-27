import { useCallback, useEffect, useRef, useState } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert, Button, Card, Col, Row, Badge } from "react-bootstrap";
import { Link } from "react-router-dom";

const PAGE_SIZE = 8;

const Home = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState("");
  const observer = useRef();

  const loadRooms = useCallback(async (pageNumber) => {
    try {
      if (pageNumber === 1) {
        setLoading(true);
      } else {
        setLoadingMore(true);
      }

      let res = await Apis.get(endpoints["rooms"], {
        params: { page: pageNumber },
      });
      const nextRooms = Array.isArray(res.data) ? res.data : [];

      setRooms((currentRooms) => {
        if (pageNumber === 1) {
          return nextRooms;
        }

        const existingIds = new Set(currentRooms.map((r) => r.id));
        const newRooms = nextRooms.filter((r) => !existingIds.has(r.id));
        return [...currentRooms, ...newRooms];
      });
      setHasMore(nextRooms.length === PAGE_SIZE);
      setError("");
    } catch (ex) {
      setError("Lỗi tải danh sách phòng.");
      setHasMore(false);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);

  useEffect(() => {
    loadRooms(1);
  }, [loadRooms]);

  useEffect(() => {
    if (page > 1) {
      loadRooms(page);
    }
  }, [page, loadRooms]);

  const lastRoomRef = useCallback(
    (node) => {
      if (loading || loadingMore) return;
      if (observer.current) observer.current.disconnect();

      observer.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasMore) {
            setPage((currentPage) => currentPage + 1);
          }
        },
        { rootMargin: "200px" },
      );

      if (node) observer.current.observe(node);
    },
    [hasMore, loading, loadingMore],
  );

  useEffect(() => {
    return () => {
      if (observer.current) {
        observer.current.disconnect();
      }
    };
  }, []);

  const renderRoomStatus = (status) => {
    switch (status) {
      case "AVAILABLE":
        return <Badge bg="success">Sẵn sàng</Badge>;
      case "OCCUPIED":
        return <Badge bg="danger">Đã đặt trước hôm nay</Badge>;
      case "CLEANING":
        return (
          <Badge bg="warning" text="dark">
            Đang dọn dẹp
          </Badge>
        );
      case "MAINTENANCE":
        return <Badge bg="secondary">Đang bảo trì</Badge>;
      default:
        return <Badge bg="info">{status}</Badge>;
    }
  };

  return (
    <>
      {error && (
        <Alert variant="danger" className="mt-2">
          {error}
        </Alert>
      )}

      {!loading && rooms.length === 0 && !error && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}
      <Row>
        {rooms.map((r, index) => {
          const isOccupied = r.status === "OCCUPIED";
          const isLastRoom = index === rooms.length - 1;

          return (
            <Col
              xs={6}
              md={4}
              lg={6}
              key={r.id}
              ref={isLastRoom ? lastRoomRef : null}
              className="p-2 w-200 h-25"
            >
              <Card className=" shadow-sm border-2 m-1">
                <Link
                  to={isOccupied ? "#" : `/rooms/${r.id}`}
                  style={{ pointerEvents: isOccupied ? "none" : "auto" }}
                >
                  <div>
                    <Card.Img
                      variant="top"
                      src={r.mainImage}
                      loading="lazy"
                      style={{ height: "450px", objectFit: "cover" }}
                    />
                  </div>
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
                    <strong>Trạng thái: </strong> {renderRoomStatus(r.status)}
                  </Card.Text>
                  <Card.Text>{r.note}</Card.Text>
                </Card.Body>

                <Card.Body>
                  <Button
                    as={Link}
                    to={`/rooms/${r.id}`}
                    variant={isOccupied ? "secondary" : "dark"}
                    className="mt-4 border-radius-5"
                  >
                    {isOccupied ? "ĐÃ ĐẶT TRƯỚC HÔM NAY" : "ĐẶT PHÒNG"}
                  </Button>
                </Card.Body>
              </Card>
            </Col>
          );
        })}
      </Row>
      {loading && <MySpinner />}
      {loadingMore && <MySpinner />}
    </>
  );
};

export default Home;
