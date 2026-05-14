import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Card, Row, Col, Table, Form, Button, Badge, Spinner, Alert } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const ReservationDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    
    const [reservation, setReservation] = useState(null);
    const [serviceOrders, setServiceOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    const resStatuses = ['PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'];
    const soStatuses = ['PENDING', 'PROCESSING', 'COMPLETED', 'CANCELED'];
    const paymentStatuses = ['PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'];

    useEffect(() => {
        const loadData = async () => {
            try {
                let resDetail = await authApis().get(endpoints.reservationDetail(id));
                setReservation(resDetail.data);
                setServiceOrders(resDetail.data.serviceOrders || []);
            } catch (ex) {
                console.error("Lỗi tải chi tiết:", ex);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [id]);

    const handleUpdateResStatus = async (newStatus) => {
        try {
            await authApis().patch(endpoints.updateReservationStatus(id), { status: newStatus });
            setReservation({ ...reservation, status: newStatus });
            alert("Cập nhật trạng thái phòng thành công!");
        } catch (ex) {
            alert("Lỗi: Không thể cập nhật trạng thái phòng.");
        }
    };

    const handleUpdateServiceStatus = async (orderId, newStatus) => {
        try {
            await authApis().patch(endpoints.updateServiceOrderStatus(orderId), { status: newStatus });
            setServiceOrders(serviceOrders.map(so => 
                so.id === orderId ? { ...so, status: newStatus } : so
            ));
            alert("Cập nhật dịch vụ thành công!");
        } catch (ex) {
            alert("Lỗi: Không thể cập nhật trạng thái dịch vụ.");
        }
    };

    const handleUpdatePaymentStatus = async (paymentId, newStatus) => {
        try {
            await authApis().patch(endpoints.updatePaymentStatus(paymentId), { status: newStatus });
            setReservation({
                ...reservation,
                payments: reservation.payments.map(p => 
                    p.id === paymentId ? { ...p, status: newStatus } : p
                )
            });
            alert("Cập nhật thanh toán thành công!");
        } catch (ex) {
            alert("Lỗi: Không thể cập nhật thanh toán.");
        }
    };

    const formatDate = (timestamp) => {
        if (!timestamp) return "N/A";
        return new Date(timestamp).toLocaleDateString('vi-VN');
    };

    if (loading) return <Container className="mt-5 text-center"><Spinner animation="border" /></Container>;
    if (!reservation) return <Container className="mt-5"><Alert variant="danger">Không tìm thấy thông tin đơn đặt phòng!</Alert></Container>;

    return (
        <Container className="mt-4 mb-5">
            <Button variant="outline-secondary" className="mb-4" onClick={() => navigate(-1)}>
                ← Quay lại danh sách
            </Button>
            
            <h2 className="mb-4 fw-bold text-primary">Chi tiết Đơn Đặt Phòng #{id}</h2>

            <Row>
                <Col lg={4}>
                    <Card className="shadow-sm border-0 mb-4">
                        <Card.Header className="bg-dark text-white fw-bold">Thông tin Đơn đặt</Card.Header>
                        <Card.Body>
                            <p><strong>Khách hàng:</strong> {reservation.customerName}</p>
                            <p><strong>Người tạo đơn:</strong> {reservation.createdByName || "Khách tự đặt"}</p>
                            <p><strong>Ngày Check-in:</strong> {formatDate(reservation.checkIn)}</p>
                            <p><strong>Ngày Check-out:</strong> {formatDate(reservation.checkOut)}</p>
                            <hr />
                            <Form.Group>
                                <Form.Label className="fw-bold">Trạng thái đặt phòng:</Form.Label>
                                <Form.Select 
                                    value={reservation.status} 
                                    onChange={(e) => handleUpdateResStatus(e.target.value)}
                                    className="border-primary text-primary fw-bold"
                                >
                                    {resStatuses.map(s => <option key={s} value={s}>{s}</option>)}
                                </Form.Select>
                            </Form.Group>
                        </Card.Body>
                    </Card>

                    <Card className="shadow-sm border-0 mb-4">
                        <Card.Header className="bg-info text-white fw-bold">Phòng Đã Đặt</Card.Header>
                        <Card.Body>
                            {reservation.rooms && reservation.rooms.length > 0 ? (
                                <ul>
                                    {reservation.rooms.map(r => (
                                        <li key={r.id}>
                                            Phòng <strong>{r.roomName}</strong> <br/>
                                            <span className="text-muted">{Number(r.pricePerNight).toLocaleString()}đ / đêm</span>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <Alert variant="warning">Chưa có phòng nào</Alert>
                            )}
                        </Card.Body>
                    </Card>

                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-success text-white fw-bold">Thanh Toán</Card.Header>
                        <Card.Body>
                            {reservation.payments && reservation.payments.length > 0 ? (
                                reservation.payments.map(payment => (
                                    <div key={payment.id} className="mb-3 border-bottom pb-2">
                                        <p><strong>Tổng tiền:</strong> <span className="text-danger fw-bold">{Number(payment.amount).toLocaleString()} VNĐ</span></p>
                                        <p><strong>Phương thức:</strong> <Badge bg="info">{payment.method}</Badge></p>
                                        <Form.Group>
                                            <Form.Label className="small fw-bold">Trạng thái thanh toán:</Form.Label>
                                            <Form.Select 
                                                size="sm"
                                                value={payment.status} 
                                                onChange={(e) => handleUpdatePaymentStatus(payment.id, e.target.value)}
                                            >
                                                {paymentStatuses.map(s => <option key={s} value={s}>{s}</option>)}
                                            </Form.Select>
                                        </Form.Group>
                                    </div>
                                ))
                            ) : <Alert variant="warning">Chưa có dữ liệu thanh toán</Alert>}
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={8}>
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-primary text-white fw-bold">Quản lý Dịch vụ gọi thêm (Service Orders)</Card.Header>
                        <Card.Body>
                            {serviceOrders.length === 0 ? (
                                <Alert variant="info" className="text-center">Khách chưa gọi dịch vụ nào.</Alert>
                            ) : (
                                <Table hover responsive className="align-middle text-center">
                                    <thead className="table-light">
                                        <tr>
                                            <th>Mã DV</th>
                                            <th>Tên dịch vụ</th>
                                            <th>Số lượng</th>
                                            <th>Thành tiền</th>
                                            <th>Trạng thái (Status)</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {serviceOrders.map((so) => (
                                            <tr key={so.id}>
                                                <td>#{so.id}</td>
                                                <td className="fw-bold text-start">{so.serviceName}</td>
                                                <td>{so.qty}</td>
                                                <td className="text-danger">{Number(so.amount).toLocaleString()}đ</td>
                                                <td>
                                                    <Form.Select 
                                                        size="sm" 
                                                        value={so.status}
                                                        onChange={(e) => handleUpdateServiceStatus(so.id, e.target.value)}
                                                        className={so.status === 'COMPLETED' ? 'text-success fw-bold border-success' : ''}
                                                    >
                                                        {soStatuses.map(s => <option key={s} value={s}>{s}</option>)}
                                                    </Form.Select>
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