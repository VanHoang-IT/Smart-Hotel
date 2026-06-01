import React, { useState, useEffect } from "react";
import {
  Card,
  Table,
  Form,
  Button,
  Row,
  Col,
  Spinner,
  Modal,
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const RoomTypesTab = ({ notify }) => {
  const [roomTypes, setRoomTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [rtForm, setRtForm] = useState({
    name: "",
    capacity: "",
    description: "",
  });
  const [editShow, setEditShow] = useState(false);
  const [editForm, setEditForm] = useState({});

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await authApis().get(endpoints.roomTypes);
      setRoomTypes(res.data);
    } catch (e) {
      notify("danger", "Lỗi tải loại phòng: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().post(endpoints.adminAddRoomType, {
        ...rtForm,
        capacity: parseInt(rtForm.capacity),
      });
      notify("success", "Thêm loại phòng thành công!");
      setRtForm({ name: "", capacity: "", description: "" });
      loadData();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const openEdit = (rt) => {
    setEditForm({
      id: rt.id,
      name: rt.name || "",
      capacity: rt.capacity || "",
      description: rt.description || "",
    });
    setEditShow(true);
  };

  const handleEdit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().put(endpoints.adminUpdateRoomType(editForm.id), {
        ...editForm,
        capacity: parseInt(editForm.capacity),
      });
      notify("success", "Cập nhật loại phòng thành công!");
      setEditShow(false);
      loadData();
    } catch (err) {
      notify("danger", "Lỗi cập nhật: " + (err.response?.data || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (
      !window.confirm(
        "Xóa loại phòng này? Sẽ không xóa được nếu vẫn còn phòng thuộc loại này.",
      )
    )
      return;
    try {
      await authApis().delete(endpoints.adminDeleteRoomType(id));
      notify("success", "Đã xóa loại phòng.");
      loadData();
    } catch (err) {
      notify("danger", "Lỗi xóa: " + (err.response?.data || err.message));
    }
  };

  if (loading)
    return (
      <div className="text-center py-4">
        <Spinner /> <span className="ms-2">Đang tải...</span>
      </div>
    );

  return (
    <>
      <Row>
        <Col md={5}>
          <Card className="shadow-sm border-0 mb-3">
            <Card.Header className="fw-bold bg-secondary text-white">
              Thêm loại phòng mới
            </Card.Header>
            <Card.Body>
              <Form onSubmit={handleAdd}>
                <Form.Group className="mb-3">
                  <Form.Label>Tên loại phòng *</Form.Label>
                  <Form.Control
                    required
                    value={rtForm.name}
                    onChange={(e) =>
                      setRtForm({ ...rtForm, name: e.target.value })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-3">
                  <Form.Label>Sức chứa (người) *</Form.Label>
                  <Form.Control
                    type="number"
                    required
                    value={rtForm.capacity}
                    onChange={(e) =>
                      setRtForm({ ...rtForm, capacity: e.target.value })
                    }
                  />
                </Form.Group>
                <Form.Group className="mb-3">
                  <Form.Label>Mô tả</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    value={rtForm.description}
                    onChange={(e) =>
                      setRtForm({ ...rtForm, description: e.target.value })
                    }
                  />
                </Form.Group>
                <Button type="submit" variant="secondary" disabled={submitting}>
                  {submitting ? <Spinner size="sm" /> : "Thêm loại phòng"}
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
                  <tr>
                    <th>#</th>
                    <th>Tên</th>
                    <th>Sức chứa</th>
                    <th>Mô tả</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {roomTypes.map((rt) => (
                    <tr key={rt.id}>
                      <td>{rt.id}</td>
                      <td className="fw-bold">{rt.name}</td>
                      <td>{rt.capacity} người</td>
                      <td className="text-muted small">
                        {rt.description || "—"}
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="me-1"
                          onClick={() => openEdit(rt)}
                        >
                          Sửa
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(rt.id)}
                        >
                          Xóa
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Modal show={editShow} onHide={() => setEditShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Sửa loại phòng #{editForm.id}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleEdit}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Tên loại phòng *</Form.Label>
              <Form.Control
                required
                value={editForm.name || ""}
                onChange={(e) =>
                  setEditForm({ ...editForm, name: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Sức chứa (người) *</Form.Label>
              <Form.Control
                type="number"
                required
                value={editForm.capacity || ""}
                onChange={(e) =>
                  setEditForm({ ...editForm, capacity: e.target.value })
                }
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Mô tả</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={editForm.description || ""}
                onChange={(e) =>
                  setEditForm({ ...editForm, description: e.target.value })
                }
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setEditShow(false)}>
              Hủy
            </Button>
            <Button type="submit" variant="primary" disabled={submitting}>
              {submitting ? <Spinner size="sm" /> : "Lưu thay đổi"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
};

export default RoomTypesTab;
