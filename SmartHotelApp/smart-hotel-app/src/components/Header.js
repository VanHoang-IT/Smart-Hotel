import {
  Nav,
  Navbar,
  Container,
  NavDropdown,
  Button,
  Modal,
  Spinner,
  Alert,
} from "react-bootstrap";
import { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import Apis, { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const formatDateDisplay = (value) => {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "-";

  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
};

const Header = () => {
  const [roomTypes, setRoomTypes] = useState([]);
  const [user, dispatch] = useContext(MyUserContext);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState("");
  const [profileData, setProfileData] = useState(null);

  useEffect(() => {
    const loadRoomTypes = async () => {
      try {
        const res = await Apis.get(endpoints.roomTypes);
        setRoomTypes(res.data);
      } catch (ex) {
        console.error(ex);
      }
    };

    loadRoomTypes();
  }, []);

  const handleOpenProfile = async () => {
    try {
      setShowProfileModal(true);
      setProfileLoading(true);
      setProfileError("");
      const res = await authApis().get(endpoints.customerProfileMe);
      setProfileData(res.data || null);
    } catch (ex) {
      console.error(ex);
      setProfileData(null);
      setProfileError("Không thể tải thông tin hồ sơ khách hàng.");
    } finally {
      setProfileLoading(false);
    }
  };

  return (
    <>
      <Navbar expand="lg" className="border-b">
        <Container>
          <Navbar.Brand as={Link} to="/" className="font-medium">
            Smart-Hotel
          </Navbar.Brand>

          <Navbar.Toggle />

          <Navbar.Collapse>
            <Nav className="me-auto">
              <Nav.Link as={Link} to="/">
                Trang chủ
              </Nav.Link>

              <NavDropdown title="Danh mục phòng" id="room-dropdown">
                {roomTypes.map((rt) => (
                  <NavDropdown.Item
                    key={rt.id}
                    as={Link}
                    to={`/room-types/${rt.id}`}
                  >
                    {rt.name}
                  </NavDropdown.Item>
                ))}
              </NavDropdown>

              <Nav.Link as={Link} to="/cart">
                Giỏ hàng
              </Nav.Link>
              <Nav.Link as={Link} to="/my-reservations">
                Lịch sử đặt phòng
              </Nav.Link>

              {user && user.role === "RECEPTIONIST" && (
                <Nav.Link as={Link} to="/reservation">
                  Đơn đặt phòng
                </Nav.Link>
              )}
              {user && user.role === "RECEPTIONIST" && (
                <Nav.Link as={Link} to="/room-status">
                  Trạng thái phòng
                </Nav.Link>
              )}

              {user && user.role === "ROLE_ADMIN" && (
                <Nav.Link as={Link} to="/admin">
                  Quản trị
                </Nav.Link>
              )}
              {user && user.role === "ROLE_ADMIN" && (
                <Nav.Link as={Link} to="/stats">
                  Thống kê
                </Nav.Link>
              )}

              {/* 👇 Thêm link cho STAFF — đổi "STAFF" nếu role thực tế khác */}
              {user &&
                (user.role === "STAFF" || user.role === "ROLE_STAFF") && (
                  <Nav.Link as={Link} to="/my-tasks">
                    Công việc của tôi
                  </Nav.Link>
                )}
            </Nav>

            {user === null ? (
              <>
                <Link to="/register" className="nav-link text-danger m-2">
                  Đăng ký
                </Link>
                <Link to="/login" className="nav-link text-danger m-2">
                  Đăng nhập
                </Link>
              </>
            ) : (
              <>
                <Button
                  variant="link"
                  className="nav-link text-danger p-0 me-3 text-decoration-none"
                  onClick={handleOpenProfile}
                >
                  <img
                    src={user.avatar}
                    width={40}
                    className="rounded-circle p-1"
                    alt="avatar"
                  />{" "}
                  Chào {user.username}!
                </Button>
                <Button
                  variant="info"
                  onClick={() => dispatch({ type: "LOGOUT" })}
                >
                  Đăng xuất
                </Button>
              </>
            )}
          </Navbar.Collapse>
        </Container>
      </Navbar>

      <Modal
        show={showProfileModal}
        onHide={() => setShowProfileModal(false)}
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title>Thông tin khách hàng</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {profileLoading && (
            <div className="text-center py-3">
              <Spinner animation="border" size="sm" />
            </div>
          )}

          {!profileLoading && profileError && (
            <Alert variant="danger" className="mb-0">
              {profileError}
            </Alert>
          )}

          {!profileLoading && !profileError && profileData && (
            <>
              <p>
                <strong>Username:</strong> {profileData.user?.username || "-"}
              </p>
              <p>
                <strong>Họ tên:</strong> {profileData.user?.fullName || "-"}
              </p>
              <p>
                <strong>Email:</strong> {profileData.user?.email || "-"}
              </p>
              <p>
                <strong>Số điện thoại:</strong> {profileData.user?.phone || "-"}
              </p>
              <hr />
              {profileData.customerProfile ? (
                <>
                  <p>
                    <strong>Ngày sinh:</strong>{" "}
                    {formatDateDisplay(profileData.customerProfile?.dob)}
                  </p>
                  <p>
                    <strong>Địa chỉ:</strong>{" "}
                    {profileData.customerProfile?.address || "-"}
                  </p>
                  <p>
                    <strong>Điểm tích lũy:</strong>{" "}
                    {profileData.customerProfile?.loyaltyPoint ?? 0}
                  </p>
                </>
              ) : (
                <Alert variant="warning" className="mb-0">
                  Bạn chưa có hồ sơ khách hàng.
                </Alert>
              )}
            </>
          )}
        </Modal.Body>
      </Modal>
    </>
  );
};

export default Header;
