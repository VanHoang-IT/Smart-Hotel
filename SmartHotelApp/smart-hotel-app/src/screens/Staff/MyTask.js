import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Container,
  Card,
  Badge,
  Button,
  Spinner,
  Alert,
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const statusMap = {
  TODO: { label: "Chờ làm", bg: "secondary" },
  IN_PROGRESS: { label: "Đang làm", bg: "info" },
  DONE: { label: "Hoàn thành", bg: "success" },
};

const nextStatusMap = {
  TODO: { next: "IN_PROGRESS", label: "Bắt đầu" },
  IN_PROGRESS: { next: "DONE", label: "Hoàn thành" },
};

const MyTasks = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");
  const [updating, setUpdating] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const loadTasks = async () => {
    try {
      setLoading(true);
      const res = await authApis().get(endpoints.myHousekeepingTasks);
      setTasks(res.data);
      setError(null);
    } catch {
      setError("Không thể tải công việc.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const init = async () => {
      try {
        const profileRes = await authApis().get(endpoints.profile);
        const userRole = profileRes.data.role || "";

        if (!userRole.includes("STAFF")) {
          setLoading(false);
          navigate("/");
          return;
        }

        loadTasks();
      } catch {
        setError("Không thể xác thực người dùng.");
        setLoading(false);
      }
    };

    init();
  }, [navigate]);

  const handleUpdateStatus = async (taskId, newStatus) => {
    setUpdating(taskId);
    try {
      await authApis().patch(endpoints.updateHousekeepingStatus(taskId), {
        status: newStatus,
      });
      setTasks((prev) =>
        prev.map((t) => (t.id === taskId ? { ...t, status: newStatus } : t)),
      );
    } catch {
      alert("Cập nhật thất bại.");
    } finally {
      setUpdating(null);
    }
  };

  const filtered =
    filter === "ALL" ? tasks : tasks.filter((t) => t.status === filter);

  const counts = {
    all: tasks.length,
    inProg: tasks.filter((t) => t.status === "IN_PROGRESS").length,
    done: tasks.filter((t) => t.status === "DONE").length,
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" variant="primary" />
        <p className="mt-2">Đang tải công việc...</p>
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
          <h4 className="mb-0 fw-bold text-center">CÔNG VIỆC CỦA TÔI</h4>
        </Card.Header>

        <Card.Body>
          <div className="d-flex gap-3 mb-4">
            {[
              { label: "Tất cả", val: counts.all },
              { label: "Đang làm", val: counts.inProg },
              { label: "Hoàn thành", val: counts.done },
            ].map((s) => (
              <div
                key={s.label}
                className="text-center flex-fill p-3 rounded"
                style={{ background: "#f8f9fa" }}
              >
                <div className="fs-4 fw-bold">{s.val}</div>
                <div className="text-muted small">{s.label}</div>
              </div>
            ))}
          </div>

          <div className="d-flex gap-2 mb-3 flex-wrap">
            {["ALL", "TODO", "IN_PROGRESS", "DONE"].map((f) => (
              <Button
                key={f}
                size="sm"
                variant={filter === f ? "primary" : "outline-secondary"}
                onClick={() => setFilter(f)}
              >
                {f === "ALL" ? "Tất cả" : statusMap[f].label}
              </Button>
            ))}
            <Button
              size="sm"
              variant="outline-secondary"
              className="ms-auto"
              onClick={loadTasks}
            >
              ↻ Làm mới
            </Button>
          </div>

          {filtered.length === 0 ? (
            <Alert variant="info" className="text-center">
              Không có công việc nào.
            </Alert>
          ) : (
            <div className="d-flex flex-column gap-3">
              {filtered.map((t) => {
                const s = statusMap[t.status] || {
                  label: t.status,
                  bg: "secondary",
                };
                const nx = nextStatusMap[t.status];

                return (
                  <div
                    key={t.id}
                    className="p-3 rounded border"
                    style={{ borderLeft: `4px solid var(--bs-${s.bg})` }}
                  >
                    <div className="d-flex justify-content-between align-items-start">
                      <div className="flex-grow-1">
                        <div className="d-flex align-items-center gap-2 mb-1">
                          <strong>{t.task}</strong>
                          <Badge bg="light" text="dark" className="border">
                            Phòng {t.roomId}
                          </Badge>
                        </div>

                        <div className="d-flex align-items-center gap-3 text-muted small">
                          <Badge bg={s.bg}>{s.label}</Badge>

                          {t.dueTime && (
                            <span>
                              🕐 {new Date(t.dueTime).toLocaleString("vi-VN")}
                            </span>
                          )}

                          {t.notes && (
                            <span
                              className="text-truncate"
                              style={{ maxWidth: 200 }}
                            >
                              📝 {t.notes}
                            </span>
                          )}
                        </div>
                      </div>

                      <div>
                        {nx ? (
                          <Button
                            size="sm"
                            variant="outline-primary"
                            disabled={updating === t.id}
                            onClick={() => handleUpdateStatus(t.id, nx.next)}
                          >
                            {updating === t.id ? "..." : nx.label}
                          </Button>
                        ) : (
                          <Badge bg="success">✓ Xong</Badge>
                        )}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </Card.Body>
      </Card>
    </Container>
  );
};

export default MyTasks;
