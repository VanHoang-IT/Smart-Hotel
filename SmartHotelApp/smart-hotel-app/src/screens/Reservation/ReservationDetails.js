import React, { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Container,
  Card,
  Row,
  Col,
  Table,
  Form,
  Button,
  Badge,
  Spinner,
  Alert,
} from "react-bootstrap";
import Apis, { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const statusLabel = {
  PENDING: "Chờ thanh toán",
  CONFIRMED: "Đã xác nhận",
  CHECKED_IN: "Đang ở",
  CHECKED_OUT: "Đã trả phòng",
  CANCELLED: "Đã hủy",
};

const ReservationDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user] = useContext(MyUserContext);
  const canEdit = user && user.role === "RECEPTIONIST";
  const isCustomer = !canEdit;

  const [reservation, setReservation] = useState(null);
  const [serviceOrders, setServiceOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [services, setServices] = useState([]);
  const [selectedServiceId, setSelectedServiceId] = useState("");
  const [qty, setQty] = useState(1);
  const [adding, setAdding] = useState(false);
  const [payLoading, setPayLoading] = useState(false);

  const resStatuses = [
    "PENDING",
    "CONFIRMED",
    "CHECKED_IN",
    "CHECKED_OUT",
    "CANCELLED",
  ];
  const soStatuses = ["PENDING", "PROCESSING", "COMPLETED", "CANCELED"];
  const paymentStatuses = ["PENDING", "COMPLETED", "FAILED", "REFUNDED"];

  const loadData = async () => {
    try {
      const [resDetail, svcRes] = await Promise.all([
        authApis().get(endpoints.reservationDetail(id)),
        Apis.get(endpoints.extraServices),
      ]);
      setReservation(resDetail.data);
      setServiceOrders(resDetail.data.serviceOrders || []);
      setServices(svcRes.data);
      if (svcRes.data.length > 0) setSelectedServiceId(svcRes.data[0].id);
    } catch (ex) {
      console.error("Lỗi tải chi tiết:", ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  useEffect(() => {
    const handleUpdate = (e) => {
      if (e.detail.reservationId === Number(id)) {
        setReservation((prev) => ({ ...prev, status: e.detail.status }));
      }
    };
    window.addEventListener("reservationUpdated", handleUpdate);
    return () => window.removeEventListener("reservationUpdated", handleUpdate);
  }, [id]);

  const handleAddService = async (e) => {
    e.preventDefault();
    if (!selectedServiceId) return;
    try {
      setAdding(true);
      await authApis().post(endpoints.serviceOrders(id), {
        serviceId: selectedServiceId,
        qty,
      });
      const resDetail = await authApis().get(endpoints.reservationDetail(id));
      setReservation(resDetail.data);
      setServiceOrders(resDetail.data.serviceOrders || []);
      setQty(1);
    } catch (ex) {
      alert("Thêm dịch vụ thất bại!");
    } finally {
      setAdding(false);
    }
  };

  const handlePayVNPay = async () => {
    try {
      setPayLoading(true);
      const res = await authApis().post(endpoints.vnpayLink, {
        reservationId: id,
      });
      if (res.data?.payUrl) window.location.href = res.data.payUrl;
    } catch (ex) {
      alert("Lỗi tạo link thanh toán!");
    } finally {
      setPayLoading(false);
    }
  };

  const handlePayMoMo = async () => {
    try {
      setPayLoading(true);
      const res = await authApis().post(endpoints.momoLink, {
        reservationId: id,
        amount: 0,
      });
      if (res.data?.payUrl) window.location.href = res.data.payUrl;
    } catch (ex) {
      alert("Lỗi tạo link thanh toán!");
    } finally {
      setPayLoading(false);
    }
  };

  const handleUpdateResStatus = async (newStatus) => {
    try {
      await authApis().patch(endpoints.updateReservationStatus(id), {
        status: newStatus,
      });
      setReservation({ ...reservation, status: newStatus });
    } catch (ex) {
      alert("Lỗi: Không thể cập nhật trạng thái phòng.");
    }
  };

  const handleUpdateServiceStatus = async (orderId, newStatus) => {
    try {
      await authApis().patch(endpoints.updateServiceOrderStatus(orderId), {
        status: newStatus,
      });
      const resDetail = await authApis().get(endpoints.reservationDetail(id));
      setReservation(resDetail.data);
      setServiceOrders(resDetail.data.serviceOrders || []);
    } catch (ex) {
      alert("Lỗi: Không thể cập nhật trạng thái dịch vụ.");
    }
  };

  const handleUpdatePaymentStatus = async (paymentId, newStatus) => {
    try {
      await authApis().patch(endpoints.updatePaymentStatus(paymentId), {
        status: newStatus,
      });
      setReservation({
        ...reservation,
        payments: reservation.payments.map((p) =>
          p.id === paymentId ? { ...p, status: newStatus } : p,
        ),
      });
    } catch (ex) {
      alert("Lỗi: Không thể cập nhật thanh toán.");
    }
  };

  const formatDate = (timestamp) => {
    if (!timestamp) return "N/A";
    return new Date(timestamp).toLocaleDateString("vi-VN");
  };

  if (loading)
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" />
      </Container>
    );
  if (!reservation)
    return (
      <Container className="mt-5">
        <Alert variant="danger">Không tìm thấy thông tin đơn đặt phòng!</Alert>
      </Container>
    );

  return (
    <Container className="mt-4 mb-5">
      <h2 className="mb-4 fw-bold text-primary">
        Chi tiết Đơn Đặt Phòng #{id}
      </h2>
      <Row>
        <Col lg={4}>
          <Card className="shadow-sm border-0 mb-4">
            <Card.Header className="bg-dark text-white fw-bold">
              Thông tin Đơn đặt
            </Card.Header>
            <Card.Body>
              <p>
                <strong>Khách hàng:</strong> {reservation.customerName}
              </p>
              <p>
                <strong>Người tạo đơn:</strong>{" "}
                {reservation.createdByName || "Khách tự đặt"}
              </p>
              <p>
                <strong>Ngày Check-in:</strong>{" "}
                {formatDate(reservation.checkIn)}
              </p>
              <p>
                <strong>Ngày Check-out:</strong>{" "}
                {formatDate(reservation.checkOut)}
              </p>
              <hr />
              <Form.Group>
                <Form.Label className="fw-bold">
                  Trạng thái đặt phòng:
                </Form.Label>
                {isCustomer ? (
                  <div>
                    <Badge
                      bg={
                        reservation.status === "CONFIRMED"
                          ? "primary"
                          : reservation.status === "CHECKED_IN"
                            ? "info"
                            : reservation.status === "CHECKED_OUT"
                              ? "success"
                              : reservation.status === "CANCELLED"
                                ? "danger"
                                : "warning"
                      }
                      text={
                        reservation.status === "PENDING" ? "dark" : undefined
                      }
                    >
                      {statusLabel[reservation.status] || reservation.status}
                    </Badge>
                  </div>
                ) : (
                  <Form.Select
                    value={reservation.status}
                    onChange={(e) => handleUpdateResStatus(e.target.value)}
                    className="border-primary text-primary fw-bold"
                  >
                    {resStatuses.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </Form.Select>
                )}
              </Form.Group>
            </Card.Body>
          </Card>

          <Card className="shadow-sm border-0 mb-4">
            <Card.Header className="bg-info text-white fw-bold">
              Phòng Đã Đặt
            </Card.Header>
            <Card.Body>
              {reservation.rooms?.length > 0 ? (
                <ul>
                  {reservation.rooms.map((r) => (
                    <li key={r.id}>
                      Phòng <strong>{r.roomName}</strong>
                      <br />
                      <span className="text-muted">
                        {Number(r.pricePerNight).toLocaleString()}đ / đêm
                      </span>
                    </li>
                  ))}
                </ul>
              ) : (
                <Alert variant="warning">Chưa có phòng nào</Alert>
              )}
            </Card.Body>
          </Card>

          <Card className="shadow-sm border-0">
            <Card.Header className="bg-success text-white fw-bold">
              Thanh Toán
            </Card.Header>
            <Card.Body>
              {reservation.payments?.length > 0
                ? reservation.payments.map((payment) => (
                    <div key={payment.id} className="mb-3 border-bottom pb-2">
                      <p>
                        <strong>Tổng tiền:</strong>{" "}
                        <span className="text-danger fw-bold">
                          {Number(payment.totalAmount).toLocaleString()} VNĐ
                        </span>
                      </p>
                      <p>
                        <strong>Phương thức:</strong>{" "}
                        <Badge bg="info">{payment.method}</Badge>
                      </p>
                      <Form.Group>
                        <Form.Label className="small fw-bold">
                          Trạng thái thanh toán:
                        </Form.Label>
                        {isCustomer ? (
                          <div>
                            <Badge
                              bg={
                                payment.status === "COMPLETED"
                                  ? "success"
                                  : payment.status === "FAILED"
                                    ? "danger"
                                    : "warning"
                              }
                              text={
                                payment.status === "PENDING"
                                  ? "dark"
                                  : undefined
                              }
                            >
                              {payment.status}
                            </Badge>
                          </div>
                        ) : (
                          <Form.Select
                            size="sm"
                            value={payment.status}
                            onChange={(e) =>
                              handleUpdatePaymentStatus(
                                payment.id,
                                e.target.value,
                              )
                            }
                          >
                            {paymentStatuses.map((s) => (
                              <option key={s} value={s}>
                                {s}
                              </option>
                            ))}
                          </Form.Select>
                        )}
                      </Form.Group>
                    </div>
                  ))
                : null}
              {isCustomer && reservation.status === "PENDING" && (
                <div className="mt-3">
                  <p className="fw-bold text-center mb-2">
                    Chọn phương thức thanh toán:
                  </p>
                  <div className="d-grid gap-2">
                    <Button
                      variant="success"
                      onClick={handlePayVNPay}
                      disabled={payLoading}
                    >
                      {payLoading ? (
                        <Spinner animation="border" size="sm" />
                      ) : (
                        "💳 Thanh toán qua VNPay"
                      )}
                    </Button>
                    <Button
                      variant="danger"
                      onClick={handlePayMoMo}
                      disabled={payLoading}
                    >
                      {payLoading ? (
                        <Spinner animation="border" size="sm" />
                      ) : (
                        "💰 Thanh toán qua MoMo"
                      )}
                    </Button>
                  </div>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>

        <Col lg={8}>
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-primary text-white fw-bold">
              Quản lý Dịch vụ gọi thêm
            </Card.Header>
            <Card.Body>
              {canEdit && (
                <Form
                  onSubmit={handleAddService}
                  className="d-flex gap-2 align-items-end mb-3"
                >
                  <Form.Group style={{ minWidth: 200 }}>
                    <Form.Label className="fw-bold small">Dịch vụ</Form.Label>
                    <Form.Select
                      size="sm"
                      value={selectedServiceId}
                      onChange={(e) => setSelectedServiceId(e.target.value)}
                    >
                      {services.map((s) => (
                        <option key={s.id} value={s.id}>
                          {s.name} — {Number(s.price).toLocaleString()}đ
                        </option>
                      ))}
                    </Form.Select>
                  </Form.Group>
                  <Form.Group style={{ width: 80 }}>
                    <Form.Label className="fw-bold small">Số lượng</Form.Label>
                    <Form.Control
                      size="sm"
                      type="number"
                      min={1}
                      value={qty}
                      onChange={(e) => setQty(Number(e.target.value))}
                    />
                  </Form.Group>
                  <Button
                    type="submit"
                    size="sm"
                    variant="success"
                    disabled={adding}
                  >
                    {adding ? <Spinner animation="border" size="sm" /> : "Thêm"}
                  </Button>
                </Form>
              )}
              {serviceOrders.length === 0 ? (
                <Alert variant="info" className="text-center">
                  Khách chưa gọi dịch vụ nào.
                </Alert>
              ) : (
                <Table hover responsive className="align-middle text-center">
                  <thead className="table-light">
                    <tr>
                      <th>Mã DV</th>
                      <th>Tên dịch vụ</th>
                      <th>Số lượng</th>
                      <th>Thành tiền</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    {serviceOrders.map((so) => (
                      <tr key={so.id}>
                        <td>#{so.id}</td>
                        <td className="fw-bold text-start">{so.serviceName}</td>
                        <td>{so.qty}</td>
                        <td className="text-danger">
                          {Number(so.amount).toLocaleString()}đ
                        </td>
                        <td>
                          {isCustomer ? (
                            <Badge
                              bg={
                                so.status === "COMPLETED"
                                  ? "success"
                                  : so.status === "PROCESSING"
                                    ? "info"
                                    : so.status === "CANCELED"
                                      ? "danger"
                                      : "warning"
                              }
                              text={
                                so.status === "PENDING" ? "dark" : undefined
                              }
                            >
                              {so.status}
                            </Badge>
                          ) : (
                            <Form.Select
                              size="sm"
                              value={so.status}
                              onChange={(e) =>
                                handleUpdateServiceStatus(so.id, e.target.value)
                              }
                              className={
                                so.status === "COMPLETED"
                                  ? "text-success fw-bold border-success"
                                  : ""
                              }
                            >
                              {soStatuses.map((s) => (
                                <option key={s} value={s}>
                                  {s}
                                </option>
                              ))}
                            </Form.Select>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default ReservationDetail;
