import React, { useState, useEffect } from "react";
import { Container, Table, Alert, Badge, Spinner, Card, Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";

const Reservation = () => {
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const loadReservations = async () => {
            try {
                let res = await authApis().get(endpoints.reservations);
                console.log("API Response:", res.data);
                setReservations(res.data);
            } catch (err) {
                console.error("Lỗi khi tải danh sách đặt phòng:", err);
                setError("Không thể tải dữ liệu đặt phòng. Vui lòng kiểm tra lại quyền truy cập hoặc đăng nhập lại.");
            } finally {
                setLoading(false);
            }
        };

        loadReservations();
    }, []);

    const renderStatusBadge = (status) => {
        switch (status) {
            case "PENDING":
                return <Badge bg="warning" text="dark">Chờ xử lý / Chưa thanh toán</Badge>;
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

    const handleRowClick = (id) => {
        navigate(`/reservation-detail/${id}`);
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (!window.confirm(`Bạn có chắc muốn xóa đơn #${id}? Hành động này không thể hoàn tác!`)) return;
        try {
            await authApis().delete(endpoints.deleteReservation(id));
            setReservations((prev) => prev.filter((r) => r.id !== id));
        } catch (err) {
            console.error(err);
            alert("Xóa thất bại!");
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return "N/A";

        const safeString = dateString.replace(" ", "T");
        const date = new Date(safeString);

        return `${date.toLocaleTimeString("vi-VN")} - ${date.toLocaleDateString("vi-VN")}`;
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
                    {reservations.length === 0 ? (
                        <Alert variant="info" className="text-center">
                            Hiện tại chưa có đơn đặt phòng nào trong hệ thống.
                        </Alert>
                    ) : (
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
                                {reservations.map((res) => (
                                    <tr
                                        key={res.id}
                                        onClick={() => handleRowClick(res.id)}
                                        style={{ cursor: "pointer" }}
                                        title="Click để xem chi tiết"
                                    >
                                        <td className="fw-bold text-primary">#{res.id}</td>
                                        <td>{res.checkIn ? new Date(res.checkIn).toLocaleDateString("vi-VN") : "N/A"}</td>
                                        <td>{res.checkOut ? new Date(res.checkOut).toLocaleDateString("vi-VN") : "N/A"}</td>
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
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>
        </Container>
    );
};

export default Reservation;
