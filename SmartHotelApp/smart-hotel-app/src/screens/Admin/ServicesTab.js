import React, { useState, useEffect } from "react";
import { Card, Table, Form, Button, Row, Col, Spinner } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const ServicesTab = ({ notify }) => {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [svcForm, setSvcForm] = useState({
    name: "",
    price: "",
    description: "",
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const res = await authApis().get(endpoints.extraServices);
      setServices(res.data);
    } catch (e) {
      notify("danger", "Lỗi tải dịch vụ: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().post(endpoints.adminAddService, svcForm);
      notify("success", "Thêm dịch vụ thành công!");
      setSvcForm({ name: "", price: "", description: "" });
      loadData();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Xóa dịch vụ này?")) return;
    try {
      await authApis().delete(`/secure/services/${id}`);
      notify("success", "Đã xóa dịch vụ.");
      loadData();
    } catch {
      notify("danger", "Lỗi xóa dịch vụ.");
    }
  };

  if (loading)
    return (
      <div className="text-center py-4">
        <Spinner /> <span className="ms-2">Đang tải...</span>
      </div>
    );

  return (
    <Row>
      <Col md={5}>
        <Card className="shadow-sm border-0 mb-3">
          <Card.Header className="fw-bold bg-warning">
            Thêm dịch vụ mới
          </Card.Header>
          <Card.Body>
            <Form onSubmit={handleAdd}>
              <Form.Group className="mb-3">
                <Form.Label>Tên dịch vụ *</Form.Label>
                <Form.Control
                  required
                  value={svcForm.name}
                  onChange={(e) =>
                    setSvcForm({ ...svcForm, name: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Giá (VNĐ) *</Form.Label>
                <Form.Control
                  type="number"
                  required
                  value={svcForm.price}
                  onChange={(e) =>
                    setSvcForm({ ...svcForm, price: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Mô tả</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={svcForm.description}
                  onChange={(e) =>
                    setSvcForm({ ...svcForm, description: e.target.value })
                  }
                />
              </Form.Group>
              <Button type="submit" variant="warning" disabled={submitting}>
                {submitting ? <Spinner size="sm" /> : "Thêm dịch vụ"}
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
                <tr>
                  <th>#</th>
                  <th>Tên</th>
                  <th>Giá</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {services.map((s) => (
                  <tr key={s.id}>
                    <td>{s.id}</td>
                    <td>{s.name}</td>
                    <td>{Number(s.price).toLocaleString()}đ</td>
                    <td>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleDelete(s.id)}
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
  );
};

export default ServicesTab;
