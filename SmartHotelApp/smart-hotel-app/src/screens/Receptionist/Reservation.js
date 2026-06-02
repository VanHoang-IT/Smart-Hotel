import React, { useState, useEffect, useRef, useCallback } from "react";
import {
  Container,
  Table,
  Alert,
  Badge,
  Spinner,
  Card,
  Button,
  Form,
  Row,
  Col,
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";

const PAGE_SIZE = 10;

const Reservation = () => {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [page, setPage] = useState(1);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState({
    status: "",
    fromDate: "",
    toDate: "",
  });
  const debounceRef = useRef(null);
  const observerRef = useRef();
  const navigate = useNavigate();

  useEffect(() => {
    checkRole();
  }, []);

  useEffect(() => {
    if (loading) return;
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      fetchReservations(1, filters, true);
    }, 600);
    return () => clearTimeout(debounceRef.current);
  }, [filters]);

  useEffect(() => {
    return () => {
      if (observerRef.current) observerRef.current.disconnect();
    };
  }, []);

  const checkRole = async () => {
    setLoading(true);
    try {
      const profileRes = await authApis().get(endpoints.profile);
      const userRole = profileRes.data.role || "";
      if (!userRole.includes("RECEPTIONIST")) {
        navigate("/");
        return;
      }
      await fetchReservations(1, filters, true);
    } catch (err) {
      console.error(err);
      setError(
        "Không thể tải dữ liệu đặt phòng. Vui lòng kiểm tra lại quyền truy cập hoặc đăng nhập lại.",
      );
    } finally {
      setLoading(false);
    }
  };

  const fetchReservations = async (p, f, reset = false) => {
    try {
      if (p === 1) setLoading(true);
      else setLoadingMore(true);

      const params = { page: p };
      if (f.status) params.status = f.status;
      if (f.fromDate) params.fromDate = f.fromDate;
      if (f.toDate) params.toDate = f.toDate;

      const res = await authApis().get(endpoints.reservations, { params });
      const data = Array.isArray(res.data) ? res.data : [];

      if (reset) {
        setReservations(data);
      } else {
        setReservations((prev) => {
          const existingIds = new Set(prev.map((r) => r.id));
          return [...prev, ...data.filter((r) => !existingIds.has(r.id))];
        });
      }
      setPage(p);
      setHasMore(data.length === PAGE_SIZE);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
      setLoadingMore(false);
    }
  };

  const lastRowRef = useCallback(
    (node) => {
      if (loading || loadingMore) return;
      if (observerRef.current) observerRef.current.disconnect();
      observerRef.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && hasMore) {
            fetchReservations(page + 1, filters, false);
          }
        },
        { rootMargin: "200px" },
      );
      if (node) observerRef.current.observe(node);
    },
    [loading, loadingMore, hasMore, page, filters],
  );

  const renderStatusBadge = (status) => {
    switch (status) {
      case "PENDING":
        return (
          <Badge bg="warning" text="dark">
            Chờ xử lý / Chưa thanh toán
          </Badge>
        );
      case "CONFIRMED":
        return <Badge bg="primary">Đã xác nhận</Badge>;
      case "CHECKED_IN":
        return <Badge bg="info">Đang ở</Badge>;
      case "CHECKED_OUT":
        return <Badge bg="success">Đã trả phòng</Badge>;
      case "CANCELLED":
        return <Badge bg="danger">Đã hủy</Badge>;
      default:
        return <Badge bg="secondary">{status}</Badge>;
    }
  };

  const handleDelete = async (e, id) => {
    e.stopPropagation();
    if (
      !window.confirm(
        `Bạn có chắc muốn xóa đơn #${id}? Hành động này không thể hoàn tác!`,
      )
    )
      return;
    try {
      await authApis().delete(endpoints.deleteReservation(id));
      setReservations((prev) => prev.filter((r) => r.id !== id));
    } catch (err) {
      console.error(err);
      alert("Xóa thất bại!");
    }
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2">Đang tải danh sách đặt phòng...</p>
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
          <h4 className="mb-0 fw-bold text-center">QUẢN LÝ ĐƠN ĐẶT PHÒNG</h4>
        </Card.Header>
        <Card.Body>
          <Row className="g-2 mb-4">
            <Col md={3}>
              <Form.Label className="fw-bold">Trạng thái</Form.Label>
              <Form.Select
                value={filters.status}
                onChange={(e) =>
                  setFilters({ ...filters, status: e.target.value })
                }
              >
                <option value="">Tất cả</option>
                <option value="PENDING">Chờ xử lý</option>
                <option value="CONFIRMED">Đã xác nhận</option>
                <option value="CHECKED_IN">Đang ở</option>
                <option value="CHECKED_OUT">Đã trả phòng</option>
                <option value="CANCELLED">Đã hủy</option>
              </Form.Select>
            </Col>
            <Col md={3}>
              <Form.Label className="fw-bold">Từ ngày</Form.Label>
              <Form.Control
                type="date"
                value={filters.fromDate}
                onChange={(e) =>
                  setFilters({ ...filters, fromDate: e.target.value })
                }
              />
            </Col>
            <Col md={3}>
              <Form.Label className="fw-bold">Đến ngày</Form.Label>
              <Form.Control
                type="date"
                value={filters.toDate}
                onChange={(e) =>
                  setFilters({ ...filters, toDate: e.target.value })
                }
              />
            </Col>
            <Col md={3} className="d-flex align-items-end">
              <Button
                variant="outline-secondary"
                className="w-100"
                onClick={() =>
                  setFilters({ status: "", fromDate: "", toDate: "" })
                }
              >
                Xóa lọc
              </Button>
            </Col>
          </Row>

          {reservations.length === 0 ? (
            <Alert variant="info" className="text-center">
              Không có đơn đặt phòng nào phù hợp.
            </Alert>
          ) : (
            <>
              <Table hover responsive className="align-middle">
                <thead className="table-light">
                  <tr>
                    <th>Mã đơn</th>
                    <th>Ngày nhận phòng</th>
                    <th>Ngày trả phòng</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {reservations.map((res, index) => {
                    const isLast = index === reservations.length - 1;
                    return (
                      <tr
                        key={res.id}
                        ref={isLast ? lastRowRef : null}
                        onClick={() =>
                          navigate(`/reservation-detail/${res.id}`)
                        }
                        style={{ cursor: "pointer" }}
                        title="Click để xem chi tiết"
                      >
                        <td className="fw-bold text-primary">#{res.id}</td>
                        <td>
                          {res.checkIn
                            ? new Date(res.checkIn).toLocaleDateString("vi-VN")
                            : "N/A"}
                        </td>
                        <td>
                          {res.checkOut
                            ? new Date(res.checkOut).toLocaleDateString("vi-VN")
                            : "N/A"}
                        </td>
                        <td>{renderStatusBadge(res.status)}</td>
                        <td>
                          <Button
                            variant="danger"
                            size="sm"
                            onClick={(e) => handleDelete(e, res.id)}
                          >
                            Xóa
                          </Button>
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
    </Container>
  );
};

export default Reservation;
