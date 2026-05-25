import React, { useState, useEffect } from "react";
import {
  Container,
  Table,
  Badge,
  Spinner,
  Alert,
  Card,
  Form,
  Modal,
} from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import Calendar from "../../components/Calender";

const STATUS_OPTIONS = ["AVAILABLE", "OCCUPIED", "CLEANING", "MAINTENANCE"];

const statusLabel = (status) => {
  switch (status) {
    case "AVAILABLE":
      return <Badge bg="success">Trống</Badge>;
    case "OCCUPIED":
      return <Badge bg="danger">Đang có khách</Badge>;
    case "CLEANING":
      return (
        <Badge bg="warning" text="dark">
          Đang dọn dẹp
        </Badge>
      );
    case "MAINTENANCE":
      return <Badge bg="secondary">Bảo trì</Badge>;
    default:
      return (
        <Badge bg="light" text="dark">
          {status || "—"}
        </Badge>
      );
  }
};

const RoomStatusManager = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updating, setUpdating] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);

  const loadRooms = async () => {
    try {
      setLoading(true);
      const res = await Apis.get(endpoints.rooms);
      setRooms(res.data);
    } catch (ex) {
      console.error(ex);
      setError("Không thể tải danh sách phòng.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRooms();
  }, []);

  const handleStatusChange = async (roomId, newStatus) => {
    try {
      setUpdating(roomId);
      await authApis().patch(
        `${endpoints.updateRoomStatus(roomId)}?status=${newStatus}`,
      );
      setRooms((prev) =>
        prev.map((r) => (r.id === roomId ? { ...r, status: newStatus } : r)),
      );
    } catch (ex) {
      console.error(ex);
      alert("Cập nhật trạng thái thất bại!");
    } finally {
      setUpdating(null);
    }
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2">Đang tải danh sách phòng...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="mt-5">
        <Alert variant="danger">{error}</Alert>
      </Container>
    );
  }

  return (
    <Container className="mt-5 mb-5">
      <Card className="shadow-sm border-0">
        <Card.Header className="bg-primary text-white py-3">
          <h4 className="mb-0 fw-bold text-center">QUẢN LÝ TRẠNG THÁI PHÒNG</h4>
        </Card.Header>
        <Card.Body>
          {rooms.length === 0 ? (
            <Alert variant="info" className="text-center">
              Không có phòng nào.
            </Alert>
          ) : (
            <>
              <Table hover responsive className="align-middle">
                <thead className="table-light">
                  <tr>
                    <th>Phòng</th>
                    <th>Tầng</th>
                    <th>Loại phòng</th>
                    <th>Trạng thái hiện tại</th>
                    <th>Cập nhật trạng thái</th>
                    <th>Lịch đặt phòng</th>
                  </tr>
                </thead>
                <tbody>
                  {rooms.map((room) => (
                    <tr key={room.id}>
                      <td className="fw-bold">{room.name}</td>
                      <td>{room.floor ?? "—"}</td>
                      <td>{room.roomType?.name ?? "—"}</td>
                      <td>{statusLabel(room.status)}</td>
                      <td style={{ minWidth: 200 }}>
                        <div className="d-flex gap-2 align-items-center">
                          <Form.Select
                            size="sm"
                            defaultValue={room.status || ""}
                            onChange={(e) =>
                              handleStatusChange(room.id, e.target.value)
                            }
                            disabled={updating === room.id}
                            style={{ maxWidth: 180 }}
                          >
                            {STATUS_OPTIONS.map((s) => (
                              <option key={s} value={s}>
                                {s}
                              </option>
                            ))}
                          </Form.Select>
                          {updating === room.id && (
                            <Spinner
                              animation="border"
                              size="sm"
                              variant="primary"
                            />
                          )}
                        </div>
                      </td>
                      <td>
                        <span
                          role="button"
                          title="Xem lịch đặt phòng"
                          style={{ cursor: "pointer", fontSize: 20 }}
                          onClick={() => setSelectedRoom(room)}
                        >
                          📅
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>

              <Modal
                show={!!selectedRoom}
                onHide={() => setSelectedRoom(null)}
                size="lg"
                centered
              >
                <Modal.Header closeButton>
                  <Modal.Title>
                    Lịch đặt phòng — {selectedRoom?.name}
                  </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                  {selectedRoom && <Calendar roomId={selectedRoom.id} />}
                </Modal.Body>
              </Modal>
            </>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default RoomStatusManager;
