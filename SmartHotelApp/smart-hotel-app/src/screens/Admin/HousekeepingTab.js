import React, { useState, useEffect } from "react";
import { Card, Table, Form, Button, Row, Col, Spinner } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const PAGE_SIZE = 10;
const TASK_STATUSES = ["TODO", "IN_PROGRESS", "DONE"];

const HousekeepingTab = ({ notify }) => {
  const [tasks, setTasks] = useState([]);
  const [staff, setStaff] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [taskForm, setTaskForm] = useState({
    task: "",
    roomId: "",
    assigneeId: "",
    dueTime: "",
    notes: "",
    status: "TODO",
  });

  useEffect(() => {
    loadInitial();
  }, []);

  const loadInitial = async () => {
    setLoading(true);
    try {
      const [tRes, staffRes, rRes] = await Promise.all([
        authApis().get(endpoints.adminHousekeeping, { params: { page: 1 } }),
        authApis().get(endpoints.adminStaff),
        authApis().get(endpoints.rooms),
      ]);
      setTasks(tRes.data);
      setStaff(staffRes.data);
      setRooms(rRes.data);
      setPage(1);
      setHasMore(tRes.data.length >= PAGE_SIZE);
      if (rRes.data.length > 0)
        setTaskForm((f) => ({ ...f, roomId: rRes.data[0].id }));
    } catch (e) {
      notify("danger", "Lỗi tải housekeeping: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  const reloadFromStart = async () => {
    try {
      const res = await authApis().get(endpoints.adminHousekeeping, {
        params: { page: 1 },
      });
      setTasks(res.data);
      setPage(1);
      setHasMore(res.data.length >= PAGE_SIZE);
    } catch (e) {
      notify("danger", "Lỗi tải lại: " + e.message);
    }
  };

  const loadMore = async () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    try {
      const nextPage = page + 1;
      const res = await authApis().get(endpoints.adminHousekeeping, {
        params: { page: nextPage },
      });
      if (res.data.length === 0) {
        setHasMore(false);
      } else {
        setTasks((prev) => [...prev, ...res.data]);
        setPage(nextPage);
        if (res.data.length < PAGE_SIZE) setHasMore(false);
      }
    } catch (e) {
      notify("danger", "Lỗi tải thêm: " + e.message);
    } finally {
      setLoadingMore(false);
    }
  };

  const handleAdd = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().post(endpoints.adminHousekeeping, taskForm);
      notify("success", "Giao việc thành công!");
      setTaskForm({
        task: "",
        roomId: rooms[0]?.id || "",
        assigneeId: "",
        dueTime: "",
        notes: "",
        status: "TODO",
      });
      reloadFromStart();
    } catch (err) {
      notify("danger", "Lỗi: " + (err.response?.data || err.message));
    } finally {
      setSubmitting(false);
    }
  };

  const handleTaskStatus = async (id, status) => {
    try {
      await authApis().patch(endpoints.adminUpdateTaskStatus(id), { status });
      setTasks(tasks.map((t) => (t.id === id ? { ...t, status } : t)));
      notify("success", "Cập nhật trạng thái thành công!");
    } catch {
      notify("danger", "Lỗi cập nhật.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Xóa task này?")) return;
    try {
      await authApis().delete(endpoints.adminDeleteTask(id));
      notify("success", "Đã xóa task.");
      reloadFromStart();
    } catch {
      notify("danger", "Lỗi xóa task.");
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
          <Card.Header className="fw-bold bg-success text-white">
            Giao việc cho staff
          </Card.Header>
          <Card.Body>
            <Form onSubmit={handleAdd}>
              <Form.Group className="mb-3">
                <Form.Label>Mô tả công việc *</Form.Label>
                <Form.Control
                  required
                  value={taskForm.task}
                  onChange={(e) =>
                    setTaskForm({ ...taskForm, task: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Phòng *</Form.Label>
                <Form.Select
                  required
                  value={taskForm.roomId}
                  onChange={(e) =>
                    setTaskForm({ ...taskForm, roomId: e.target.value })
                  }
                >
                  {rooms.map((r) => (
                    <option key={r.id} value={r.id}>
                      Phòng {r.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Giao cho nhân viên (STAFF)</Form.Label>
                <Form.Select
                  value={taskForm.assigneeId}
                  onChange={(e) =>
                    setTaskForm({ ...taskForm, assigneeId: e.target.value })
                  }
                >
                  <option value="">-- Chưa giao --</option>
                  {staff.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.fullName || s.username}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Hạn thực hiện</Form.Label>
                <Form.Control
                  type="datetime-local"
                  value={taskForm.dueTime}
                  onChange={(e) =>
                    setTaskForm({ ...taskForm, dueTime: e.target.value })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Ghi chú</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  value={taskForm.notes}
                  onChange={(e) =>
                    setTaskForm({ ...taskForm, notes: e.target.value })
                  }
                />
              </Form.Group>
              <Button type="submit" variant="success" disabled={submitting}>
                {submitting ? <Spinner size="sm" /> : "Giao việc"}
              </Button>
            </Form>
          </Card.Body>
        </Card>
      </Col>
      <Col md={7}>
        <Card className="shadow-sm border-0">
          <Card.Header className="fw-bold">
            Danh sách công việc
            <span
              className="text-muted fw-normal ms-2"
              style={{ fontSize: 13 }}
            >
              ({tasks.length} đã tải)
            </span>
          </Card.Header>
          <Card.Body>
            <Table hover responsive size="sm" className="align-middle">
              <thead className="table-light">
                <tr>
                  <th>#</th>
                  <th>Công việc</th>
                  <th>Phòng</th>
                  <th>Nhân viên</th>
                  <th>Trạng thái</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {tasks.map((t) => (
                  <tr key={t.id}>
                    <td>{t.id}</td>
                    <td>{t.task}</td>
                    <td>{t.room?.name}</td>
                    <td>
                      {t.assignee?.fullName || t.assignee?.username || (
                        <span className="text-muted">Chưa giao</span>
                      )}
                    </td>
                    <td>
                      <Form.Select
                        size="sm"
                        value={t.status || "TODO"}
                        style={{ width: 130 }}
                        onChange={(e) => handleTaskStatus(t.id, e.target.value)}
                      >
                        {TASK_STATUSES.map((s) => (
                          <option key={s} value={s}>
                            {s}
                          </option>
                        ))}
                      </Form.Select>
                    </td>
                    <td>
                      <Button
                        variant="outline-danger"
                        size="sm"
                        onClick={() => handleDelete(t.id)}
                      >
                        Xóa
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
            <div className="text-center mt-3">
              {hasMore ? (
                <Button
                  variant="outline-secondary"
                  onClick={loadMore}
                  disabled={loadingMore}
                >
                  {loadingMore ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      Đang tải...
                    </>
                  ) : (
                    "Xem thêm"
                  )}
                </Button>
              ) : (
                tasks.length > 0 && (
                  <span className="text-muted small">
                    Đã hiển thị tất cả {tasks.length} công việc
                  </span>
                )
              )}
            </div>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default HousekeepingTab;
