import { Nav, Navbar, Container, NavDropdown, Button } from "react-bootstrap";
import { useEffect, useState, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const Header = () => {
  const [roomTypes, setRoomTypes] = useState([]);
  const [user, dispatch] = useContext(MyUserContext);
  const navigate = useNavigate();

  useEffect(() => {
    const loadRoomTypes = async () => {
      try {
        let res = await Apis.get(endpoints["roomTypes"]);
        setRoomTypes(res.data);
      } catch (ex) {
        console.error(ex);
      }
    };
    loadRoomTypes();
  }, []);

  return (
    <Navbar expand="lg" className="border-b">
      <Container>
        <Navbar.Brand as={Link} to="/" className="font-medium">
          Smart-Hotel
        </Navbar.Brand>

        <Navbar.Toggle />

        <Navbar.Collapse>
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/">Trang chủ</Nav.Link>

            <NavDropdown title="Danh mục phòng" id="room-dropdown">
              {roomTypes.map((rt) => (
                <NavDropdown.Item key={rt.id} as={Link} to={`/room-types/${rt.id}`}>
                  {rt.name}
                </NavDropdown.Item>
              ))}
            </NavDropdown>

            <Nav.Link as={Link} to="/cart">Giỏ hàng</Nav.Link>

            <Nav.Link as={Link} to="/my-reservations">Lịch sử đặt phòng</Nav.Link>

            {user && user.role === "RECEPTIONIST" && (
              <Nav.Link as={Link} to="/reservation">Đơn đặt phòng</Nav.Link>
            )}
            {user && user.role === "RECEPTIONIST" && (
              <Nav.Link as={Link} to="/room-status">Trạng thái phòng</Nav.Link>
            )}

            {user && user.role === "ROLE_ADMIN" && (
              <Nav.Link as={Link} to="/admin">Quản trị</Nav.Link>
            )}
          </Nav>

          {user === null ? (
            <>
              <Link to="/register" className="nav-link text-danger m-2">Đăng ký</Link>
              <Link to="/login" className="nav-link text-danger m-2">Đăng nhập</Link>
            </>
          ) : (
            <>
              <Link to="/" className="nav-link text-danger">
                <img
                  src={user.avatar}
                  width={40}
                  className="rounded-circle p-1"
                />{" "}
                Chào {user.username}!
              </Link>
              <Button variant="info" onClick={() => dispatch({ type: "LOGOUT" })}>
                Đăng xuất
              </Button>
            </>
          )}
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Header;
