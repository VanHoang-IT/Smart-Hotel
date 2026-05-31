import React, { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Table,
  Badge,
  Spinner,
  Alert,
  Card,
  Form,
  Modal,
  Button,
  ButtonGroup,
} from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import Calendar from "../../components/Calender";

const STATUS_OPTIONS = ["AVAILABLE", "OCCUPIED", "CLEANING", "MAINTENANCE"];
const PAGE_SIZE = 8;

const STATUS_DISPLAY = {
  AVAILABLE: { label: "Trống", bg: "success" },
  OCCUPIED: { label: "Đang có khách", bg: "danger" },
  CLEANING: { label: "Đang dọn dẹp", bg: "warning", text: "dark" },
  MAINTENANCE: { label: "Bảo trì", bg: "secondary" },
};

const statusLabel = (status) => {
  const s = STATUS_DISPLAY[status];
  if (!s)
    return (
      <Badge bg="light" text="dark">
        {status || "—"}
      </Badge>
    );
  return (
    <Badge bg={s.bg} text={s.text}>
      {s.label}
    </Badge>
  );
};

const RoomStatusManager = () => {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [error, setError] = useState(null);
  const [updating, setUpdating] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [filterStatus, setFilterStatus] = useState("ALL");
  const observer = useRef();
  const navigate = useNavigate();

  const loadRooms = useCallback(async (pageNumber, status = "ALL") => {
    try {
      if (pageNumber === 1) setLoading(true);
      else setLoadingMore(true);

      const params = { page: pageNumber };
      if (status !== "ALL") params.status = status;

      const res = await Apis.get(endpoints.rooms, { params });
      const nextRooms = Array.isArray(res.data) ? res.data : [];

      setRooms((currentRooms) => {
        if (pageNumber === 1) return nextRooms;
        const existingIds = new Set(currentRooms.map((r) => r.id));
        return [
          ...currentRooms,
          ...nextRooms.filter((r) => !existingIds.has(r.id)),
        ];
      });
      setHasMore(nextRooms.length === PAGE_SIZE);
      setError(null);
    } catch (ex) {
      console.error(ex);
      setError("Không thể tải danh sách phòng.");
      setHasMore(false);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  }, []);

  useEffect(() => {
    const init = async () => {
      try {
        const profileRes = await authApis().get(endpoints.profile);
        const userRole = profileRes.data.role || "";
        if (!userRole.includes("RECEPTIONIST")) {
          setLoading(false);
          navigate("/");
          return;
        }
        loadRooms(1, "ALL");
      } catch (ex) {
        console.error(ex);
        setError("Không thể tải danh sách phòng.");
        setLoading(false);
      }
    };
    init();
  }, [loadRooms, navigate]);

  useEffect(() => {
    if (page > 1) loadRooms(page, filterStatus);
  }, [page, filterStatus, loadRooms]);

  const handleFilterChange = (status) => {
    setFilterStatus(status);
    setPage(1);
    setRooms([]);
    loadRooms(1, status);
  };

  const lastRoomRef = useCallback(
    (node) => {
      if (loading || loadingMore) return;
      if (observer.current) observer.current.disconnect();
      observer.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasMore) setPage((p) => p + 1);
        },
        { rootMargin: "200px" },
      );
      if (node) observer.current.observe(node);
    },
    [hasMore, loading, loadingMore],
  );

  useEffect(() => {
    return () => {
      if (observer.current) observer.current.disconnect();
    };
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
          {/* Filter bar */}
          <div className="d-flex align-items-center gap-3 mb-4 flex-wrap">
            <span className="fw-semibold text-muted small">
              Lọc theo trạng thái:
            </span>
            <ButtonGroup size="sm">
              <Button
                variant={
                  filterStatus === "ALL" ? "primary" : "outline-secondary"
                }
                onClick={() => handleFilterChange("ALL")}
              >
                Tất cả
              </Button>
              {STATUS_OPTIONS.map((s) => {
                const d = STATUS_DISPLAY[s];
                return (
                  <Button
                    key={s}
                    variant={filterStatus === s ? d.bg : `outline-${d.bg}`}
                    onClick={() => handleFilterChange(s)}
                  >
                    {d.label}
                  </Button>
                );
              })}
            </ButtonGroup>
          </div>

          {rooms.length === 0 ? (
            <Alert variant="info" className="text-center">
              Không có phòng nào
              {filterStatus !== "ALL"
                ? ` với trạng thái "${STATUS_DISPLAY[filterStatus]?.label}"`
                : ""}
              .
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
                  {rooms.map((room, index) => {
                    const isLastRoom = index === rooms.length - 1;
                    return (
                      <tr key={room.id} ref={isLastRoom ? lastRoomRef : null}>
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
                    );
                  })}
                </tbody>
              </Table>

              {loadingMore && (
                <div className="text-center py-3">
                  <Spinner animation="border" size="sm" variant="primary" />
                </div>
              )}
            </>
          )}
        </Card.Body>
      </Card>

      <Modal
        show={!!selectedRoom}
        onHide={() => setSelectedRoom(null)}
        size="lg"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>Lịch đặt phòng — {selectedRoom?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedRoom && <Calendar roomId={selectedRoom.id} />}
        </Modal.Body>
      </Modal>
    </Container>
  );
};

export default RoomStatusManager;
