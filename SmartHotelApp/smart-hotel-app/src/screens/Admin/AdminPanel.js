import React, { useEffect, useState, useContext } from "react";
import {
  Container, Nav, Tab, Card, Table, Form, Button,
  Badge, Alert, Row, Col, Spinner, Modal
} from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Contexts";

const AdminPanel = () => {
  const [user] = useContext(MyUserContext);
  const navigate = useNavigate();

  const [users, setUsers] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [roomForm, setRoomForm] = useState({ name: "", price: "", floor: "", note: "", mainImage: "", status: "AVAILABLE", roomTypeId: "" });
  const [imagePreview, setImagePreview] = useState(null);
  const [imageUploading, setImageUploading] = useState(false);
  const [editRoomShow, setEditRoomShow] = useState(false);
  const [editRoomForm, setEditRoomForm] = useState({});
  const [editRoomPreview, setEditRoomPreview] = useState(null);
  const [editRoomUploading, setEditRoomUploading] = useState(false);
  const [rtForm, setRtForm] = useState({ name: "", capacity: "", description: "" });
  const [editRtShow, setEditRtShow] = useState(false);
  const [editRtForm, setEditRtForm] = useState({});
  const [imagesShow, setImagesShow] = useState(false);
  const [imagesRoomId, setImagesRoomId] = useState(null);
  const [imagesRoomName, setImagesRoomName] = useState("");
  const [roomImagesList, setRoomImagesList] = useState([]);
  const [imgUploading, setImgUploading] = useState(false);
  const [services, setServices] = useState([]);
  const [svcForm, setSvcForm] = useState({ name: "", price: "", description: "" });
  const [tasks, setTasks] = useState([]);
  const [staff, setStaff] = useState([]);
  const [taskForm, setTaskForm] = useState({ task: "", roomId: "", assigneeId: "", dueTime: "", notes: "", status: "TODO" });
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    if (!user) { navigate("/login"); return; }
    if (user.role !== "ROLE_ADMIN") { navigate("/"); return; }
    loadAll();
  }, [user]);

  const loadAll = async () => {
    try {
      const [uRes, rtRes, sRes, tRes, staffRes, rRes] = await Promise.all([
        authApis().get(endpoints.adminUsers),
        authApis().get(endpoints.roomTypes),
        authApis().get(endpoints.extraServices),
        authApis().get(endpoints.adminHousekeeping),
        authApis().get(endpoints.adminStaff),
        authApis().get(endpoints.rooms),
      ]);
      setUsers(uRes.data);
      setRoomTypes(rtRes.data);
      setServices(sRes.data);
      setTasks(tRes.data);
      setStaff(staffRes.data);
      setRooms(rRes.data);
      if (rtRes.data.length > 0) setRoomForm(f => ({ ...f, roomTypeId: rtRes.data[0].id }));
      if (rRes.data.length > 0) setTaskForm(f => ({ ...f, roomId: rRes.data[0].id }));
    } catch (e) {
      setMsg({ type: "danger", text: "Lỗi tải dữ liệu: " + e.message });
    }
  };

  const notify = (type, text) => {
    setMsg({ type, text });
    setTimeout(() => setMsg(null), 3000);
  };

  const handleRoleChange = async (userId, role) => {
    try {
      await authApis().patch(endpoints.adminUpdateRole(userId), { role });
      setUsers(users.map(u => u.id === userId ? { ...u, role } : u));
      notify("success", "Cập nhật role thành công!");
    } catch { notify("danger", "Lỗi cập nhật role."); }
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setImagePreview(URL.createObjectURL(file));
    setImageUploading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await authApis().post(endpoints.adminUploadImage, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setRoomForm(f => ({ ...f, mainImage: res.data.url }));
      notify("success", "Tải ảnh lên thành công!");
    } catch (err) {
      notify("danger", "Lỗi upload ảnh: " + (err.response?.data || err.message));
      setImagePreview(null);
    } finally { setImageUploading(false); }
  };

  const handleEditImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setEditRoomPreview(URL.createObjectURL(file));
    setEditRoomUploading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await authApis().post(endpoints.adminUploadImage, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setEditRoomForm(f => ({ ...f, mainImage: res.data.url }));
      notify("success", "Tải ảnh lên thành công!");
    } catch (err) {
      notify("danger", "Lỗi upload ảnh: " + (err.response?.data || err.message));
    } finally { setEditRoomUploading(false); }
  };

  const handleAddRoom = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().post(endpoints.adminAddRoom, roomForm);
      notify("success", "Thêm phòng thành công!");
      setRoomForm({ name: "", price: "", floor: "", note: "", mainImage: "", status: "AVAILABLE", roomTypeId: roomTypes[0]?.id || "" });
      setImagePreview(null);
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi thêm phòng: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const openEditRoom = (room) => {
    setEditRoomForm({
      id: room.id,
      name: room.name || "",
      price: room.price || "",
      floor: room.floor || "",
      note: room.note || "",
      mainImage: room.mainImage || "",
      status: room.status || "AVAILABLE",
      roomTypeId: room.roomType?.id || "",
    });
    setEditRoomPreview(room.mainImage || null);
    setEditRoomShow(true);
  };

  const handleEditRoom = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().put(endpoints.adminUpdateRoom(editRoomForm.id), editRoomForm);
      notify("success", "Cập nhật phòng thành công!");
      setEditRoomShow(false);
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi cập nhật phòng: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const handleDeleteRoom = async (id) => {
    if (!window.confirm("Xóa phòng này? Thao tác không thể hoàn tác.")) return;
    try {
      await authApis().delete(endpoints.adminDeleteRoom(id));
      notify("success", "Đã xóa phòng.");
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi xóa phòng: " + (err.response?.data || err.message));
    }
  };

  const openImagesModal = async (room) => {
    setImagesRoomId(room.id);
    setImagesRoomName(room.name);
    setImagesShow(true);
    try {
      const res = await authApis().get(endpoints.adminRoomImages(room.id));
      setRoomImagesList(res.data);
    } catch { setRoomImagesList([]); }
  };

  const handleUploadRoomImages = async (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;
    setImgUploading(true);
    try {
      for (const file of files) {
        const formData = new FormData();
        formData.append("file", file);
        await authApis().post(endpoints.adminRoomImages(imagesRoomId), formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }
      const fresh = await authApis().get(endpoints.adminRoomImages(imagesRoomId));
      setRoomImagesList(fresh.data);
      notify("success", `Đã tải lên ${files.length} ảnh!`);
    } catch (err) {
      notify("danger", "Lỗi upload: " + (err.response?.data || err.message));
    } finally {
      setImgUploading(false);
      e.target.value = "";
    }
  };

  const handleDeleteRoomImage = async (imageId) => {
    if (!window.confirm("Xóa ảnh này?")) return;
    try {
      await authApis().delete(endpoints.adminDeleteRoomImage(imageId));
      setRoomImagesList(prev => prev.filter(img => img.id !== imageId));
      notify("success", "Đã xóa ảnh.");
    } catch { notify("danger", "Lỗi xóa ảnh."); }
  };

  const handleAddRoomType = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().post(endpoints.adminAddRoomType, { ...rtForm, capacity: parseInt(rtForm.capacity) });
      notify("success", "Thêm loại phòng thành công!");
      setRtForm({ name: "", capacity: "", description: "" });
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const openEditRt = (rt) => {
    setEditRtForm({ id: rt.id, name: rt.name || "", capacity: rt.capacity || "", description: rt.description || "" });
    setEditRtShow(true);
  };

  const handleEditRoomType = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().put(endpoints.adminUpdateRoomType(editRtForm.id), {
        ...editRtForm,
        capacity: parseInt(editRtForm.capacity),
      });
      notify("success", "Cập nhật loại phòng thành công!");
      setEditRtShow(false);
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi cập nhật loại phòng: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const handleDeleteRoomType = async (id) => {
    if (!window.confirm("Xóa loại phòng này? Sẽ không xóa được nếu vẫn còn phòng thuộc loại này.")) return;
    try {
      await authApis().delete(endpoints.adminDeleteRoomType(id));
      notify("success", "Đã xóa loại phòng.");
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi xóa loại phòng: " + (err.response?.data || err.message));
    }
  };

  const handleAddService = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().post(endpoints.extraServices.replace("/services", "/secure/services"), svcForm);
      notify("success", "Thêm dịch vụ thành công!");
      setSvcForm({ name: "", price: "", description: "" });
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const handleDeleteService = async (id) => {
    if (!window.confirm("Xóa dịch vụ này?")) return;
    try {
      await authApis().delete(`/secure/services/${id}`);
      notify("success", "Đã xóa dịch vụ.");
      loadAll();
    } catch { notify("danger", "Lỗi xóa dịch vụ."); }
  };

  const handleAddTask = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authApis().post(endpoints.adminHousekeeping, taskForm);
      notify("success", "Giao việc thành công!");
      setTaskForm({ task: "", roomId: rooms[0]?.id || "", assigneeId: "", dueTime: "", notes: "", status: "TODO" });
      loadAll();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally { setLoading(false); }
  };

  const handleTaskStatus = async (id, status) => {
    try {
      await authApis().patch(endpoints.adminUpdateTaskStatus(id), { status });
      setTasks(tasks.map(t => t.id === id ? { ...t, status } : t));
      notify("success", "Cập nhật trạng thái thành công!");
    } catch { notify("danger", "Lỗi cập nhật."); }
  };

  const handleDeleteTask = async (id) => {
    if (!window.confirm("Xóa task này?")) return;
    try {
      await authApis().delete(endpoints.adminDeleteTask(id));
      notify("success", "Đã xóa task.");
      loadAll();
    } catch { notify("danger", "Lỗi xóa task."); }
  };

  if (!user || user.role !== "ROLE_ADMIN") return null;

  const ROLES = ["ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER", "RECEPTIONIST"];
  const TASK_STATUSES = ["TODO", "IN_PROGRESS", "DONE"];
  const ROOM_STATUSES = ["AVAILABLE", "OCCUPIED", "MAINTENANCE"];

  return (
    <Container fluid className="mt-4 mb-5 px-4">
      <h2 className="fw-bold text-danger mb-4">TRANG QUẢN TRỊ ADMIN</h2>

      {msg && <Alert variant={msg.type} dismissible onClose={() => setMsg(null)}>{msg.text}</Alert>}

      <Tab.Container defaultActiveKey="users">
        <Nav variant="tabs" className="mb-3">
          <Nav.Item><Nav.Link eventKey="users">Người dùng</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link eventKey="rooms">Phòng</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link eventKey="roomtypes">Loại phòng</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link eventKey="services">Dịch vụ</Nav.Link></Nav.Item>
          <Nav.Item><Nav.Link eventKey="housekeeping">Housekeeping</Nav.Link></Nav.Item>
        </Nav>

        <Tab.Content>
          <Tab.Pane eventKey="users">
            <Card className="shadow-sm border-0">
              <Card.Header className="fw-bold bg-dark text-white">Danh sách người dùng & Phân quyền</Card.Header>
              <Card.Body>
                <Table hover responsive className="align-middle">
                  <thead className="table-light">
                    <tr><th>#</th><th>Username</th><th>Họ tên</th><th>Email</th><th>Role</th></tr>
                  </thead>
                  <tbody>
                    {users.map(u => (
                      <tr key={u.id}>
                        <td>{u.id}</td>
                        <td className="fw-bold">{u.username}</td>
                        <td>{u.fullName}</td>
                        <td>{u.email}</td>
                        <td>
                          <Form.Select size="sm" value={u.role || ""} style={{ width: 160 }}
                            onChange={e => handleRoleChange(u.id, e.target.value)}>
                            {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
                          </Form.Select>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Card.Body>
            </Card>
          </Tab.Pane>

          <Tab.Pane eventKey="rooms">
            <Row className="mb-3">
              <Col>
                <Card className="shadow-sm border-0">
                  <Card.Header className="fw-bold bg-info text-white">Thêm phòng mới</Card.Header>
                  <Card.Body>
                    <Form onSubmit={handleAddRoom}>
                      <Row>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Tên phòng *</Form.Label>
                            <Form.Control required value={roomForm.name}
                              onChange={e => setRoomForm({ ...roomForm, name: e.target.value })} />
                          </Form.Group>
                        </Col>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Giá/đêm (VNĐ) *</Form.Label>
                            <Form.Control type="number" required value={roomForm.price}
                              onChange={e => setRoomForm({ ...roomForm, price: e.target.value })} />
                          </Form.Group>
                        </Col>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Tầng</Form.Label>
                            <Form.Control type="number" value={roomForm.floor}
                              onChange={e => setRoomForm({ ...roomForm, floor: e.target.value })} />
                          </Form.Group>
                        </Col>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Loại phòng *</Form.Label>
                            <Form.Select required value={roomForm.roomTypeId}
                              onChange={e => setRoomForm({ ...roomForm, roomTypeId: e.target.value })}>
                              {roomTypes.map(rt => <option key={rt.id} value={rt.id}>{rt.name}</option>)}
                            </Form.Select>
                          </Form.Group>
                        </Col>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Trạng thái</Form.Label>
                            <Form.Select value={roomForm.status}
                              onChange={e => setRoomForm({ ...roomForm, status: e.target.value })}>
                              {ROOM_STATUSES.map(s => <option key={s}>{s}</option>)}
                            </Form.Select>
                          </Form.Group>
                        </Col>
                        <Col md={4}>
                          <Form.Group className="mb-3">
                            <Form.Label>Ảnh chính</Form.Label>
                            <Form.Control type="file" accept="image/*" onChange={handleImageChange} disabled={imageUploading} />
                            {imageUploading && <div className="mt-1 text-muted small"><Spinner size="sm" /> Đang tải ảnh lên...</div>}
                            {imagePreview && !imageUploading && (
                              <img src={imagePreview} alt="preview"
                                style={{ width: "100%", maxHeight: 120, objectFit: "cover", marginTop: 8, borderRadius: 4 }} />
                            )}
                          </Form.Group>
                        </Col>
                        <Col md={12}>
                          <Form.Group className="mb-3">
                            <Form.Label>Ghi chú</Form.Label>
                            <Form.Control as="textarea" rows={2} value={roomForm.note}
                              onChange={e => setRoomForm({ ...roomForm, note: e.target.value })} />
                          </Form.Group>
                        </Col>
                      </Row>
                      <Button type="submit" variant="info" disabled={loading}>
                        {loading ? <Spinner size="sm" /> : "Thêm phòng"}
                      </Button>
                    </Form>
                  </Card.Body>
                </Card>
              </Col>
            </Row>

            <Card className="shadow-sm border-0">
              <Card.Header className="fw-bold">Danh sách phòng</Card.Header>
              <Card.Body>
                <Table hover responsive size="sm" className="align-middle">
                  <thead className="table-light">
                    <tr><th>#</th><th>Tên</th><th>Loại</th><th>Tầng</th><th>Giá</th><th>Trạng thái</th><th>Ảnh</th><th></th></tr>
                  </thead>
                  <tbody>
                    {rooms.map(r => (
                      <tr key={r.id}>
                        <td>{r.id}</td>
                        <td className="fw-bold">{r.name}</td>
                        <td>{r.roomType?.name || "—"}</td>
                        <td>{r.floor ?? "—"}</td>
                        <td>{Number(r.price).toLocaleString()}đ</td>
                        <td>
                          <Badge bg={r.status === "AVAILABLE" ? "success" : r.status === "OCCUPIED" ? "danger" : "warning"}>
                            {r.status}
                          </Badge>
                        </td>
                        <td>
                          {r.mainImage
                            ? <img src={r.mainImage} alt="" style={{ width: 50, height: 35, objectFit: "cover", borderRadius: 3 }} />
                            : <span className="text-muted">—</span>}
                        </td>
                        <td>
                          <Button variant="outline-secondary" size="sm" className="me-1" onClick={() => openImagesModal(r)}>Ảnh</Button>
                          <Button variant="outline-primary" size="sm" className="me-1" onClick={() => openEditRoom(r)}>Sửa</Button>
                          <Button variant="outline-danger" size="sm" onClick={() => handleDeleteRoom(r.id)}>Xóa</Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Card.Body>
            </Card>
          </Tab.Pane>

          <Tab.Pane eventKey="roomtypes">
            <Row>
              <Col md={5}>
                <Card className="shadow-sm border-0 mb-3">
                  <Card.Header className="fw-bold bg-secondary text-white">Thêm loại phòng mới</Card.Header>
                  <Card.Body>
                    <Form onSubmit={handleAddRoomType}>
                      <Form.Group className="mb-3">
                        <Form.Label>Tên loại phòng *</Form.Label>
                        <Form.Control required value={rtForm.name}
                          onChange={e => setRtForm({ ...rtForm, name: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Sức chứa (người) *</Form.Label>
                        <Form.Control type="number" required value={rtForm.capacity}
                          onChange={e => setRtForm({ ...rtForm, capacity: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Mô tả</Form.Label>
                        <Form.Control as="textarea" rows={3} value={rtForm.description}
                          onChange={e => setRtForm({ ...rtForm, description: e.target.value })} />
                      </Form.Group>
                      <Button type="submit" variant="secondary" disabled={loading}>
                        {loading ? <Spinner size="sm" /> : "Thêm loại phòng"}
                      </Button>
                    </Form>
                  </Card.Body>
                </Card>
              </Col>
              <Col md={7}>
                <Card className="shadow-sm border-0">
                  <Card.Header className="fw-bold">Danh sách loại phòng</Card.Header>
                  <Card.Body>
                    <Table hover responsive size="sm" className="align-middle">
                      <thead className="table-light">
                        <tr><th>#</th><th>Tên</th><th>Sức chứa</th><th>Mô tả</th><th></th></tr>
                      </thead>
                      <tbody>
                        {roomTypes.map(rt => (
                          <tr key={rt.id}>
                            <td>{rt.id}</td>
                            <td className="fw-bold">{rt.name}</td>
                            <td>{rt.capacity} người</td>
                            <td className="text-muted small">{rt.description || "—"}</td>
                            <td>
                              <Button variant="outline-primary" size="sm" className="me-1" onClick={() => openEditRt(rt)}>Sửa</Button>
                              <Button variant="outline-danger" size="sm" onClick={() => handleDeleteRoomType(rt.id)}>Xóa</Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          </Tab.Pane>

          <Tab.Pane eventKey="services">
            <Row>
              <Col md={5}>
                <Card className="shadow-sm border-0 mb-3">
                  <Card.Header className="fw-bold bg-warning">Thêm dịch vụ mới</Card.Header>
                  <Card.Body>
                    <Form onSubmit={handleAddService}>
                      <Form.Group className="mb-3">
                        <Form.Label>Tên dịch vụ *</Form.Label>
                        <Form.Control required value={svcForm.name}
                          onChange={e => setSvcForm({ ...svcForm, name: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Giá (VNĐ) *</Form.Label>
                        <Form.Control type="number" required value={svcForm.price}
                          onChange={e => setSvcForm({ ...svcForm, price: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Mô tả</Form.Label>
                        <Form.Control as="textarea" rows={2} value={svcForm.description}
                          onChange={e => setSvcForm({ ...svcForm, description: e.target.value })} />
                      </Form.Group>
                      <Button type="submit" variant="warning" disabled={loading}>
                        {loading ? <Spinner size="sm" /> : "Thêm dịch vụ"}
                      </Button>
                    </Form>
                  </Card.Body>
                </Card>
              </Col>
              <Col md={7}>
                <Card className="shadow-sm border-0">
                  <Card.Header className="fw-bold">Danh sách dịch vụ</Card.Header>
                  <Card.Body>
                    <Table hover responsive size="sm" className="align-middle">
                      <thead className="table-light">
                        <tr><th>#</th><th>Tên</th><th>Giá</th><th></th></tr>
                      </thead>
                      <tbody>
                        {services.map(s => (
                          <tr key={s.id}>
                            <td>{s.id}</td>
                            <td>{s.name}</td>
                            <td>{Number(s.price).toLocaleString()}đ</td>
                            <td>
                              <Button variant="outline-danger" size="sm"
                                onClick={() => handleDeleteService(s.id)}>Xóa</Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          </Tab.Pane>

          <Tab.Pane eventKey="housekeeping">
            <Row>
              <Col md={5}>
                <Card className="shadow-sm border-0 mb-3">
                  <Card.Header className="fw-bold bg-success text-white">Giao việc Housekeeping</Card.Header>
                  <Card.Body>
                    <Form onSubmit={handleAddTask}>
                      <Form.Group className="mb-3">
                        <Form.Label>Mô tả công việc *</Form.Label>
                        <Form.Control required value={taskForm.task}
                          onChange={e => setTaskForm({ ...taskForm, task: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Phòng *</Form.Label>
                        <Form.Select required value={taskForm.roomId}
                          onChange={e => setTaskForm({ ...taskForm, roomId: e.target.value })}>
                          {rooms.map(r => <option key={r.id} value={r.id}>Phòng {r.name}</option>)}
                        </Form.Select>
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Giao cho nhân viên (STAFF)</Form.Label>
                        <Form.Select value={taskForm.assigneeId}
                          onChange={e => setTaskForm({ ...taskForm, assigneeId: e.target.value })}>
                          <option value="">-- Chưa giao --</option>
                          {staff.map(s => (
                            <option key={s.id} value={s.id}>{s.fullName || s.username}</option>
                          ))}
                        </Form.Select>
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Hạn thực hiện</Form.Label>
                        <Form.Control type="datetime-local" value={taskForm.dueTime}
                          onChange={e => setTaskForm({ ...taskForm, dueTime: e.target.value })} />
                      </Form.Group>
                      <Form.Group className="mb-3">
                        <Form.Label>Ghi chú</Form.Label>
                        <Form.Control as="textarea" rows={2} value={taskForm.notes}
                          onChange={e => setTaskForm({ ...taskForm, notes: e.target.value })} />
                      </Form.Group>
                      <Button type="submit" variant="success" disabled={loading}>
                        {loading ? <Spinner size="sm" /> : "Giao việc"}
                      </Button>
                    </Form>
                  </Card.Body>
                </Card>
              </Col>
              <Col md={7}>
                <Card className="shadow-sm border-0">
                  <Card.Header className="fw-bold">Danh sách công việc</Card.Header>
                  <Card.Body>
                    <Table hover responsive size="sm" className="align-middle">
                      <thead className="table-light">
                        <tr><th>#</th><th>Công việc</th><th>Phòng</th><th>Nhân viên</th><th>Trạng thái</th><th></th></tr>
                      </thead>
                      <tbody>
                        {tasks.map(t => (
                          <tr key={t.id}>
                            <td>{t.id}</td>
                            <td>{t.task}</td>
                            <td>{t.room?.name}</td>
                            <td>{t.assignee?.fullName || t.assignee?.username || <span className="text-muted">Chưa giao</span>}</td>
                            <td>
                              <Form.Select size="sm" value={t.status || "PENDING"} style={{ width: 130 }}
                                onChange={e => handleTaskStatus(t.id, e.target.value)}>
                                {TASK_STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
                              </Form.Select>
                            </td>
                            <td>
                              <Button variant="outline-danger" size="sm"
                                onClick={() => handleDeleteTask(t.id)}>Xóa</Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  </Card.Body>
                </Card>
              </Col>
            </Row>
          </Tab.Pane>

        </Tab.Content>
      </Tab.Container>

      <Modal show={imagesShow} onHide={() => setImagesShow(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Album ảnh — Phòng {imagesRoomName}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label className="fw-bold">Tải ảnh mới lên</Form.Label>
            <Form.Control
              type="file"
              accept="image/*"
              multiple
              onChange={handleUploadRoomImages}
              disabled={imgUploading}
            />
            {imgUploading && (
              <div className="mt-2 text-muted small">
                <Spinner size="sm" /> Đang tải ảnh lên
              </div>
            )}
          </Form.Group>

          <hr />

          {roomImagesList.length === 0 ? (
            <p className="text-muted text-center py-3">Chưa có ảnh nào cho phòng này.</p>
          ) : (
            <Row className="g-2">
              {roomImagesList.map(img => (
                <Col xs={6} md={4} key={img.id}>
                  <div className="position-relative">
                    <img
                      src={img.imageUrl}
                      alt=""
                      style={{ width: "100%", height: 140, objectFit: "cover", borderRadius: 6 }}
                    />
                    <Button
                      variant="danger"
                      size="sm"
                      className="position-absolute top-0 end-0 m-1"
                      style={{ padding: "2px 6px", fontSize: 12 }}
                      onClick={() => handleDeleteRoomImage(img.id)}
                    >✕</Button>
                  </div>
                </Col>
              ))}
            </Row>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setImagesShow(false)}>Đóng</Button>
        </Modal.Footer>
      </Modal>

      <Modal show={editRoomShow} onHide={() => setEditRoomShow(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Sửa thông tin phòng #{editRoomForm.id}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleEditRoom}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Tên phòng *</Form.Label>
                  <Form.Control required value={editRoomForm.name || ""}
                    onChange={e => setEditRoomForm({ ...editRoomForm, name: e.target.value })} />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Giá/đêm (VNĐ) *</Form.Label>
                  <Form.Control type="number" required value={editRoomForm.price || ""}
                    onChange={e => setEditRoomForm({ ...editRoomForm, price: e.target.value })} />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Tầng</Form.Label>
                  <Form.Control type="number" value={editRoomForm.floor || ""}
                    onChange={e => setEditRoomForm({ ...editRoomForm, floor: e.target.value })} />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Loại phòng *</Form.Label>
                  <Form.Select required value={editRoomForm.roomTypeId || ""}
                    onChange={e => setEditRoomForm({ ...editRoomForm, roomTypeId: e.target.value })}>
                    {roomTypes.map(rt => <option key={rt.id} value={rt.id}>{rt.name}</option>)}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Trạng thái</Form.Label>
                  <Form.Select value={editRoomForm.status || "AVAILABLE"}
                    onChange={e => setEditRoomForm({ ...editRoomForm, status: e.target.value })}>
                    {ROOM_STATUSES.map(s => <option key={s}>{s}</option>)}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>Ảnh chính</Form.Label>
                  <Form.Control type="file" accept="image/*" onChange={handleEditImageChange} disabled={editRoomUploading} />
                  {editRoomUploading && <div className="mt-1 text-muted small"><Spinner size="sm" /> Đang tải ảnh lên...</div>}
                  {editRoomPreview && !editRoomUploading && (
                    <img src={editRoomPreview} alt="preview"
                      style={{ width: "100%", maxHeight: 150, objectFit: "cover", marginTop: 8, borderRadius: 4 }} />
                  )}
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>Ghi chú</Form.Label>
                  <Form.Control as="textarea" rows={2} value={editRoomForm.note || ""}
                    onChange={e => setEditRoomForm({ ...editRoomForm, note: e.target.value })} />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setEditRoomShow(false)}>Hủy</Button>
            <Button type="submit" variant="primary" disabled={loading || editRoomUploading}>
              {loading ? <Spinner size="sm" /> : "Lưu thay đổi"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <Modal show={editRtShow} onHide={() => setEditRtShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Sửa loại phòng #{editRtForm.id}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleEditRoomType}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên loại phòng *</Form.Label>
              <Form.Control required value={editRtForm.name || ""}
                onChange={e => setEditRtForm({ ...editRtForm, name: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Sức chứa (người) *</Form.Label>
              <Form.Control type="number" required value={editRtForm.capacity || ""}
                onChange={e => setEditRtForm({ ...editRtForm, capacity: e.target.value })} />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control as="textarea" rows={3} value={editRtForm.description || ""}
                onChange={e => setEditRtForm({ ...editRtForm, description: e.target.value })} />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setEditRtShow(false)}>Hủy</Button>
            <Button type="submit" variant="primary" disabled={loading}>
              {loading ? <Spinner size="sm" /> : "Lưu thay đổi"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

    </Container>
  );
};

export default AdminPanel;
