import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import NavDropdown from "react-bootstrap/NavDropdown";
import Apis from "../configs/Apis";
import { useEffect, useState } from "react";
import { endpoints } from "../configs/Apis";

const Header = () => {
  const [roomTypes, setRoomTypes] = useState([]);

  const loadRoomTypes = async () => {
    let res = await Apis.get(endpoints["roomTypes"]);
    setRoomTypes(res.data);
  };
  useEffect(() => {
    loadRoomTypes();
  }, []);

  return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Smart-Hotel</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto mb-2 mb-lg-0 ">
            <Nav.Link href="#home">Trang chủ</Nav.Link>
            <NavDropdown title="Danh mục phòng" id="basic-nav-dropdown">
              {roomTypes.map((rt) => (
                <NavDropdown.Item key={rt.id} href={`#roomType/${rt.id}`}>
                  {rt.name}
                </NavDropdown.Item>
              ))}
            </NavDropdown>
            <NavDropdown title="Ưu đãi" id="basic-nav-dropdown-services">
              <NavDropdown.Item href="#services">
                Ưu đãi về phòng nghỉ
              </NavDropdown.Item>
              <NavDropdown.Item href="#services">
                Ưu đãi về ẩm thực
              </NavDropdown.Item>
              <NavDropdown.Item href="#services">
                Ưu đãi về Spa
              </NavDropdown.Item>
              <NavDropdown.Item href="#services">
                Ưu đãi về hội nghị & sự kiện
              </NavDropdown.Item>
            </NavDropdown>
            <NavDropdown title="Ẩm thực" id="basic-nav-dropdown-services">
              <NavDropdown.Item href="#services">Thực đơn</NavDropdown.Item>
            </NavDropdown>
            <NavDropdown title="Khám phá" id="basic-nav-dropdown-services">
              <NavDropdown.Item href="#services">Liên hệ</NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
export default Header;
