import React, { useEffect, useState, useContext, useCallback } from "react";
import {
  Container,
  Table,
  Badge,
  Spinner,
  Alert,
  Card,
  Button,
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const statusMap = {
  PENDING: { label: "Chờ thanh toán", bg: "warning", text: "dark" },
  CONFIRMED: { label: "Đã xác nhận", bg: "primary" },
  CHECKED_IN: { label: "Đang ở", bg: "info" },
  CHECKED_OUT: { label: "Đã trả phòng", bg: "success" },
  CANCELLED: { label: "Đã hủy", bg: "danger" },
};

const StatusBadge = ({ status }) => {
  const s = statusMap[status] || { label: status, bg: "secondary" };
  return (
    <Badge bg={s.bg} text={s.text}>
      {s.label}
    </Badge>
  );
};

const MyReservations = () => {
  const [user] = useContext(MyUserContext);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const load = useCallback(async () => {
    try {
      const res = await authApis().get(endpoints.myReservations);
      setReservations(res.data);
    } catch (err) {
      setError("Không thể tải lịch sử đặt phòng. Vui lòng thử lại.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!user) {
      navigate("/login?next=/my-reservations");
      return;
    }
    load();
  }, [user, load]);

  useEffect(() => {
    window.addEventListener("reservationUpdated", load);
    return () => window.removeEventListener("reservationUpdated", load);
  }, [load]);

  if (!user) return null;

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2">Đang tải lịch sử đặt phòng...</p>
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
          <h4 className="mb-0 fw-bold text-center">
            LỊCH SỬ ĐẶT PHÒNG CỦA TÔI
          </h4>
        </Card.Header>
        <Card.Body>
          {reservations.length === 0 ? (
            <Alert variant="info" className="text-center">
              Bạn chưa có đơn đặt phòng nào.
              <div className="mt-3">
                <Button variant="primary" onClick={() => navigate("/")}>
                  Đặt phòng ngay
                </Button>
              </div>
            </Alert>
          ) : (
            <Table hover responsive className="align-middle">
              <thead className="table-light">
                <tr>
                  <th>#</th>
                  <th>Ngày nhận phòng</th>
                  <th>Ngày trả phòng</th>
                  <th>Trạng thái</th>
                  <th className="text-center">Chi tiết</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map((r) => (
                  <tr key={r.id}>
                    <td className="fw-bold text-primary">#{r.id}</td>
                    <td>
                      {r.checkIn
                        ? new Date(r.checkIn).toLocaleDateString("vi-VN")
                        : "N/A"}
                    </td>
                    <td>
                      {r.checkOut
                        ? new Date(r.checkOut).toLocaleDateString("vi-VN")
                        : "N/A"}
                    </td>
                    <td>
                      <StatusBadge status={r.status} />
                    </td>
                    <td className="text-center">
                      <div className="d-flex gap-2 justify-content-center align-items-center">
                        <Button
                          variant="outline-primary"
                          size="sm"
                          onClick={() =>
                            navigate(`/reservation-detail/${r.id}`)
                          }
                        >
                          Xem chi tiết
                        </Button>
                        {r.status === "PENDING" && (
                          <Button
                            variant="outline-warning"
                            size="sm"
                            onClick={() =>
                              navigate(`/reservation-detail/${r.id}`)
                            }
                          >
                            Thanh toán
                          </Button>
                        )}
                        {(r.status === "CONFIRMED" ||
                          r.status === "CHECKED_OUT") &&
                          !r.reviewed && (
                            <Button
                              variant="outline-success"
                              size="sm"
                              onClick={() => navigate(`/review/${r.id}`)}
                            >
                              Nhận xét
                            </Button>
                          )}
                        {(r.status === "CONFIRMED" ||
                          r.status === "CHECKED_OUT") &&
                          r.reviewed && <Badge bg="success">Đã đánh giá</Badge>}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default MyReservations;
