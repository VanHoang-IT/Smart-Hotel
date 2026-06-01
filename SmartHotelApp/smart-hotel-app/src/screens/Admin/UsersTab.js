import React, { useState, useEffect } from "react";
import { Card, Table, Form, Spinner, Button } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const PAGE_SIZE = 10;
const ROLES = ["ROLE_ADMIN", "ROLE_STAFF", "ROLE_CUSTOMER", "RECEPTIONIST"];

const UsersTab = ({ notify }) => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);

  useEffect(() => {
    loadInitial();
  }, []);

  const loadInitial = async () => {
    setLoading(true);
    try {
      const res = await authApis().get(endpoints.adminUsers, {
        params: { page: 1 },
      });
      setUsers(res.data);
      setPage(1);
      setHasMore(res.data.length >= PAGE_SIZE);
    } catch (e) {
      notify("danger", "Lỗi tải danh sách người dùng: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  const loadMore = async () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    try {
      const nextPage = page + 1;
      const res = await authApis().get(endpoints.adminUsers, {
        params: { page: nextPage },
      });
      if (res.data.length === 0) {
        setHasMore(false);
      } else {
        setUsers((prev) => [...prev, ...res.data]);
        setPage(nextPage);
        if (res.data.length < PAGE_SIZE) setHasMore(false);
      }
    } catch (e) {
      notify("danger", "Lỗi tải thêm: " + e.message);
    } finally {
      setLoadingMore(false);
    }
  };

  const handleRoleChange = async (userId, role) => {
    try {
      await authApis().patch(endpoints.adminUpdateRole(userId), { role });
      setUsers(users.map((u) => (u.id === userId ? { ...u, role } : u)));
      notify("success", "Cập nhật role thành công!");
    } catch {
      notify("danger", "Lỗi cập nhật role.");
    }
  };

  return (
    <Card className="shadow-sm border-0">
      <Card.Header className="fw-bold bg-dark text-white">
        Danh sách người dùng
      </Card.Header>
      <Card.Body>
        {loading ? (
          <div className="text-center py-4">
            <Spinner /> <span className="ms-2">Đang tải...</span>
          </div>
        ) : (
          <>
            <Table hover responsive className="align-middle">
              <thead className="table-light">
                <tr>
                  <th>#</th>
                  <th>Username</th>
                  <th>Họ tên</th>
                  <th>Email</th>
                  <th>Role</th>
                </tr>
              </thead>
              <tbody>
                {users.map((u) => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td className="fw-bold">{u.username}</td>
                    <td>{u.fullName}</td>
                    <td>{u.email}</td>
                    <td>
                      <Form.Select
                        size="sm"
                        value={u.role || ""}
                        style={{ width: 160 }}
                        onChange={(e) => handleRoleChange(u.id, e.target.value)}
                      >
                        {ROLES.map((r) => (
                          <option key={r} value={r}>
                            {r}
                          </option>
                        ))}
                      </Form.Select>
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
                users.length > 0 && (
                  <span className="text-muted small">
                    Đã hiển thị tất cả {users.length} người dùng
                  </span>
                )
              )}
            </div>
          </>
        )}
      </Card.Body>
    </Card>
  );
};

export default UsersTab;
