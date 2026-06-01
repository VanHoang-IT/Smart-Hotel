import React, { useEffect, useState, useContext } from "react";
import { Container, Nav, Tab, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { MyUserContext } from "../../configs/Contexts";

import UsersTab from "./UsersTab";
import RoomsTab from "./RoomsTab";
import RoomTypesTab from "./RoomTypesTab";
import ServicesTab from "./ServicesTab";
import HousekeepingTab from "./HousekeepingTab";

const AdminPanel = () => {
  const [user] = useContext(MyUserContext);
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("users");
  const [msg, setMsg] = useState(null);

  const [mountedTabs, setMountedTabs] = useState({ users: true });

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (user.role !== "ROLE_ADMIN") {
      navigate("/");
      return;
    }
  }, [user]);

  const handleTabSelect = (tab) => {
    setActiveTab(tab);
    setMountedTabs((prev) => ({ ...prev, [tab]: true }));
  };

  const notify = (type, text) => {
    setMsg({ type, text });
    setTimeout(() => setMsg(null), 3000);
  };

  if (!user || user.role !== "ROLE_ADMIN") return null;

  return (
    <Container fluid className="mt-4 mb-5 px-4">
      <h2 className="fw-bold text-danger mb-4">TRANG QUẢN TRỊ ADMIN</h2>

      {msg && (
        <Alert variant={msg.type} dismissible onClose={() => setMsg(null)}>
          {msg.text}
        </Alert>
      )}

      <Tab.Container activeKey={activeTab} onSelect={handleTabSelect}>
        <Nav variant="tabs" className="mb-3">
          <Nav.Item>
            <Nav.Link eventKey="users">Người dùng</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey="rooms">Phòng</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey="roomtypes">Loại phòng</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey="services">Dịch vụ</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey="housekeeping">Giao việc</Nav.Link>
          </Nav.Item>
        </Nav>

        <Tab.Content>
          <Tab.Pane eventKey="users">
            {mountedTabs.users && <UsersTab notify={notify} />}
          </Tab.Pane>
          <Tab.Pane eventKey="rooms">
            {mountedTabs.rooms && <RoomsTab notify={notify} />}
          </Tab.Pane>
          <Tab.Pane eventKey="roomtypes">
            {mountedTabs.roomtypes && <RoomTypesTab notify={notify} />}
          </Tab.Pane>
          <Tab.Pane eventKey="services">
            {mountedTabs.services && <ServicesTab notify={notify} />}
          </Tab.Pane>
          <Tab.Pane eventKey="housekeeping">
            {mountedTabs.housekeeping && <HousekeepingTab notify={notify} />}
          </Tab.Pane>
        </Tab.Content>
      </Tab.Container>
    </Container>
  );
};

export default AdminPanel;
