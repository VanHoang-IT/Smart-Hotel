import { useContext, useEffect, useState, useRef } from "react";
import { Container, Table, Button, Card, Row, Col, Alert, Modal, Form } from "react-bootstrap";
import { useNavigate, useLocation } from "react-router-dom";
import { MyBookingContext, MyUserContext } from "../../configs/Contexts";
import Apis, { endpoints, authApis } from "../../configs/Apis";
import cookies from 'react-cookies';

const getNights = (checkIn, checkOut) => {
    if (!checkIn || !checkOut) return 1;

    const start = new Date(checkIn);
    const end = new Date(checkOut);
    const diff = end.getTime() - start.getTime();
    const nights = Math.ceil(diff / (1000 * 60 * 60 * 24));

    return Math.max(nights, 1);
};

const Cart = () => {
    const [, dispatch] = useContext(MyBookingContext);
    const [user] = useContext(MyUserContext);
    const [cart, setCart] = useState(cookies.load('cart') || {});
    const [allServices, setAllServices] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();
    const hasProcessedPayment = useRef(false);
    const [showModal, setShowModal] = useState(false);
    const [showProfileModal, setShowProfileModal] = useState(false);
    const [profileLoading, setProfileLoading] = useState(false);
    const [profileForm, setProfileForm] = useState({
        dob: "",
        address: "",
    });

    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const resultCode = queryParams.get("resultCode");
        const partnerCode = queryParams.get("partnerCode");

        if (partnerCode && resultCode !== null && !hasProcessedPayment.current) {
            hasProcessedPayment.current = true;
            if (resultCode === "0") {
                alert("Thanh toán MoMo thành công!");
                finishPayment();
            } else {
                alert("Giao dịch bị hủy hoặc thanh toán thất bại. Vui lòng thử lại!");
                navigate("/cart", { replace: true }); 
            }
        }
    }, [location.search]);

    useEffect(() => {
        const loadServices = async () => {
            try {
                let res = await Apis.get(endpoints["extraServices"]);
                setAllServices(res.data);
            } catch (ex) {}
        };
        loadServices();
    }, []);

    const cartItems = Object.values(cart);

    const calculateTotal = () => {
        let total = 0;
        cartItems.forEach(item => {
            const nights = getNights(item.checkIn, item.checkOut);
            total += Number(item.price) * nights;
            if (item.services && item.services.length > 0) {
                item.services.forEach(sId => {
                    const s = allServices.find(service => service.id === sId);
                    if (s) total += Number(s.price);
                });
            }
        });
        return total;
    };

    const getServiceQuantities = () => {
        return cartItems
            .flatMap(item => item.services || [])
            .reduce((quantities, serviceId) => {
                quantities[serviceId] = (quantities[serviceId] || 0) + 1;
                return quantities;
            }, {});
    };

    const removeItem = (id) => {
        let updatedCart = { ...cart };
        delete updatedCart[id];
        cookies.save('cart', updatedCart, { path: '/' });
        setCart(updatedCart);
        dispatch({
            type: "UPDATE_BOOKING",
            payload: { roomsCount: Object.keys(updatedCart).length }
        });
    };

    const loadCustomerProfile = async () => {
        const res = await authApis().get(endpoints.customerProfileMe);
        return res.data?.customerProfile || null;
    };

    const handlePaymentClick = async () => {
        if (!user) {
            navigate("/login?next=/cart"); 
            return;
        }

        try {
            const customerProfile = await loadCustomerProfile();
            if (!customerProfile) {
                setShowProfileModal(true);
                return;
            }
            setShowModal(true);
        } catch (ex) {
            console.error(ex);
            alert("Khong the kiem tra thong tin khach hang.");
        }
    };

    const createCustomerProfile = async () => {
        if (!profileForm.dob || !profileForm.address.trim()) {
            alert("Vui long nhap ngay sinh va dia chi.");
            return;
        }

        try {
            setProfileLoading(true);
            await authApis().post(endpoints.createCustomerProfile, {
                dob: profileForm.dob,
                address: profileForm.address.trim(),
            });
            setShowProfileModal(false);
            setShowModal(true);
        } catch (ex) {
            const message = ex.response?.data?.message || "Khong tao duoc customer profile.";
            alert(message);
        } finally {
            setProfileLoading(false);
        }
    };

    const processPayment = async (methodType) => {
        setShowModal(false);
        setLoading(true);

        try {
            const roomsPayload = cartItems.map(item => ({
                roomId: item.id,
                pricePerNight: item.price,
                notes: ""
            }));

            const payload = {
                customerId: user.id,
                checkIn: cartItems[0]?.checkIn,
                checkOut: cartItems[0]?.checkOut,
                rooms: roomsPayload
            };

            let res = await authApis().post(endpoints.createReservation, payload);
            
            if (res.status === 201 || res.status === 200) {
                const newReservationId = res.data.id;

                const serviceQuantities = getServiceQuantities();
                const serviceEntries = Object.entries(serviceQuantities);
                if (serviceEntries.length > 0) {
                    const servicePromises = serviceEntries.map(([sId, qty]) => 
                        authApis().post(endpoints.serviceOrders(newReservationId), {
                            serviceId: Number(sId),
                            qty: qty
                        })
                    );
                    await Promise.all(servicePromises);
                }

                if (methodType === "CASH") {
                    await authApis().post(endpoints.payments, {
                        reservationId: newReservationId,
                        amount: calculateTotal(),
                        method: "CASH"
                    });
                    alert("Đặt trước thành công, vui lòng đem tiền vào ngày checkIn để thanh toán");
                    finishPayment();
                } 
                else if (methodType === "MOMO") {
                    const momoRes = await authApis().post(endpoints.momoLink, {
                        reservationId: newReservationId,
                        amount: calculateTotal()
                    });

                    if (momoRes.data && momoRes.data.payUrl) {
                        window.location.href = momoRes.data.payUrl;
                    } else {
                        alert("Không khởi tạo được link MoMo. Thử lại sau!");
                        setLoading(false);
                    }
                }
            }
        } catch (ex) {
            const errorCode = ex.response?.data?.code;
            if (errorCode === "CUSTOMER_PROFILE_REQUIRED") {
                setShowModal(false);
                setShowProfileModal(true);
                setLoading(false);
                return;
            }
            let errorMsg = ex.response && ex.response.data ? ex.response.data : ex.message;
            console.error("Chi tiết lỗi API:", errorMsg);
            alert("Lỗi thanh toán: " + errorMsg);
            setLoading(false);
        }
    };

    const finishPayment = () => {
        cookies.remove('cart', { path: '/' });
        dispatch({ type: "RESET_BOOKING" });
        navigate("/");
    };

    if (cartItems.length === 0) {
        return (
            <Container className="mt-5 text-center">
                <Alert variant="info">Danh sách phòng trống</Alert>
                <Button onClick={() => navigate("/")}>Quay lại chọn phòng</Button>
            </Container>
        );
    }

    return (
        <Container className="mt-5 mb-5">
            <h2 className="text-center mb-4 text-primary fw-bold">Danh sách phòng cần thanh toán</h2>
            <Row>
                <Col lg={8}>
                    <Card className="shadow-sm border-0 mb-4">
                        <Card.Header className="bg-white py-3">
                            <h5 className="mb-0 fw-bold">Chi tiết giá phòng</h5>
                        </Card.Header>
                        <Card.Body>
                            <Table hover responsive className="align-middle">
                                <thead className="table-light">
                                    <tr>
                                        <th>Mã phòng</th>
                                        <th>Thời gian</th>
                                        <th>Danh sách dịch vụ kèm theo</th>
                                        <th className="text-end">Tổng tiền phòng</th>
                                        <th className="text-center">Hủy</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {cartItems.map((item) => {
                                        const nights = getNights(item.checkIn, item.checkOut);
                                        const roomTotal = Number(item.price) * nights;

                                        return (
                                        <tr key={item.id}>
                                            <td className="fw-bold text-primary">Phòng {item.id}</td>
                                            <td>
                                                <small className="d-block">{item.checkIn}</small>
                                                <small className="d-block text-muted">đến {item.checkOut}</small>
                                            </td>
                                            <td>
                                                {item.services && item.services.length > 0 ? (
                                                    <div className="bg-light p-2 rounded small">
                                                        {item.services.map(sId => {
                                                            const s = allServices.find(service => service.id === sId);
                                                            return s ? <div key={sId} className="d-flex justify-content-between">
                                                                <span>+ {s.name}</span>
                                                                <span className="text-muted">{s.price.toLocaleString()}đ</span>
                                                            </div> : null;
                                                        })}
                                                    </div>
                                                ) : <span className="text-muted small">Không có</span>}
                                            </td>
                                            <td className="text-end fw-bold">
                                                {roomTotal.toLocaleString()} VND
                                                <small className="d-block text-muted">
                                                    {Number(item.price).toLocaleString()} x {nights} đêm
                                                </small>
                                            </td>
                                            <td className="text-center">
                                                <Button variant="outline-danger" size="sm" className="border-0" onClick={() => removeItem(item.id)}>X</Button>
                                            </td>
                                        </tr>
                                        );
                                    })}
                                </tbody>
                            </Table>
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={4}>
                    <Card className="shadow-sm border-0 sticky-top" style={{top: '20px'}}>
                        <Card.Header className="bg-primary text-white py-3">
                            <h5 className="mb-0 fw-bold text-center">Tổng tiền thanh toán</h5>
                        </Card.Header>
                        <Card.Body className="p-4">
                            <h3 className="text-center text-danger fw-bold mb-4">
                                {calculateTotal().toLocaleString()} VNĐ
                            </h3>
                            <Button variant={user ? "success" : "warning"} className="w-100 mb-3 py-3 fw-bold" onClick={handlePaymentClick} disabled={loading}>
                                {loading ? "ĐANG XỬ LÝ..." : (user ? "Tiến hành thanh toán" : "Đăng nhập trước khi thanh toán")}
                            </Button>
                            <Button variant="outline-secondary" className="w-100 py-2" onClick={() => navigate("/")}>
                                Chọn thêm phòng
                            </Button>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Modal show={showProfileModal} onHide={() => setShowProfileModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title className="fw-bold text-primary">Thông tin khách hàng</Modal.Title>
                </Modal.Header>
                <Modal.Body className="p-4">
                    <Form.Group className="mb-3">
                        <Form.Label>Ngày sinh</Form.Label>
                        <Form.Control
                            type="date"
                            value={profileForm.dob}
                            onChange={(e) => setProfileForm({ ...profileForm, dob: e.target.value })}
                        />
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <Form.Label>Địa chỉ</Form.Label>
                        <Form.Control
                            type="text"
                            value={profileForm.address}
                            onChange={(e) => setProfileForm({ ...profileForm, address: e.target.value })}
                            placeholder="Nhap dia chi"
                        />
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowProfileModal(false)} disabled={profileLoading}>
                        Đóng
                    </Button>
                    <Button variant="primary" onClick={createCustomerProfile} disabled={profileLoading}>
                        {profileLoading ? "Dang luu..." : "Luu thong tin va tiep tuc"}
                    </Button>
                </Modal.Footer>
            </Modal>

            <Modal show={showModal} onHide={() => setShowModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title className="fw-bold text-primary">Phương thức thanh toán</Modal.Title>
                </Modal.Header>
                <Modal.Body className="text-center p-4">
                    <p className="text-muted mb-4">Vui lòng chọn một hình thức thanh toán thuận tiện nhất cho bạn.</p>
                    <div className="d-grid gap-3">
                        <Button variant="outline-primary" size="lg" className="py-3 fw-bold" onClick={() => processPayment("CASH")}>
                            THANH TOÁN TIỀN MẶT (TẠI QUẦY)
                        </Button>
                        <Button variant="outline-danger" size="lg" className="py-3 fw-bold" onClick={() => processPayment("MOMO")}>
                            THANH TOÁN QUA VÍ MOMO
                        </Button>
                    </div>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="link" className="text-muted text-decoration-none" onClick={() => setShowModal(false)}>
                        Quay lại giỏ hàng
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default Cart;
