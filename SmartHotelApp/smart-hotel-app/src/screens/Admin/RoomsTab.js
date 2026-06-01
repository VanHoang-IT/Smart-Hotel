import React, { useState, useEffect } from "react";
import {
  Card,
  Table,
  Form,
  Button,
  Badge,
  Row,
  Col,
  Spinner,
  Modal,
} from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const ROOM_STATUSES = ["AVAILABLE", "OCCUPIED", "MAINTENANCE"];

const RoomsTab = ({ notify }) => {
  const [rooms, setRooms] = useState([]);
  const [roomTypes, setRoomTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  const [roomForm, setRoomForm] = useState({
    name: "",
    price: "",
    floor: "",
    note: "",
    mainImage: "",
    status: "AVAILABLE",
    roomTypeId: "",
  });
  const [imagePreview, setImagePreview] = useState(null);
  const [imageUploading, setImageUploading] = useState(false);

  const [editShow, setEditShow] = useState(false);
  const [editForm, setEditForm] = useState({});
  const [editPreview, setEditPreview] = useState(null);
  const [editUploading, setEditUploading] = useState(false);

  const [imagesShow, setImagesShow] = useState(false);
  const [imagesRoomId, setImagesRoomId] = useState(null);
  const [imagesRoomName, setImagesRoomName] = useState("");
  const [roomImagesList, setRoomImagesList] = useState([]);
  const [imgUploading, setImgUploading] = useState(false);

  useEffect(() => {
    loadInitial();
  }, []);

  // Load lần đầu: roomTypes + trang 1 của rooms
  const loadInitial = async () => {
    setLoading(true);
    try {
      const [rtRes, rRes] = await Promise.all([
        authApis().get(endpoints.roomTypes),
        authApis().get(endpoints.rooms, { params: { page: 1 } }),
      ]);
      setRoomTypes(rtRes.data);
      setRooms(rRes.data);
      setPage(1);
      setHasMore(rRes.data.length > 0);
      if (rtRes.data.length > 0)
        setRoomForm((f) => ({ ...f, roomTypeId: rtRes.data[0].id }));
    } catch (e) {
      notify("danger", "Lỗi tải dữ liệu phòng: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  // Load thêm trang tiếp theo
  const loadMore = async () => {
    if (loadingMore || !hasMore) return;
    setLoadingMore(true);
    try {
      const nextPage = page + 1;
      const res = await authApis().get(endpoints.rooms, {
        params: { page: nextPage },
      });
      if (res.data.length === 0) {
        setHasMore(false);
      } else {
        setRooms((prev) => [...prev, ...res.data]);
        setPage(nextPage);
        // Nếu trả về ít hơn page_size thì hết rồi
        if (res.data.length < 6) setHasMore(false);
      }
    } catch (e) {
      notify("danger", "Lỗi tải thêm phòng: " + e.message);
    } finally {
      setLoadingMore(false);
    }
  };

  // Sau khi thêm/sửa/xóa thì reset về trang 1
  const reloadFromStart = async () => {
    try {
      const res = await authApis().get(endpoints.rooms, {
        params: { page: 1 },
      });
      setRooms(res.data);
      setPage(1);
      setHasMore(res.data.length >= 6);
    } catch (e) {
      notify("danger", "Lỗi tải lại danh sách: " + e.message);
    }
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
      setRoomForm((f) => ({ ...f, mainImage: res.data.url }));
      notify("success", "Tải ảnh lên thành công!");
    } catch (err) {
      notify(
        "danger",
        "Lỗi upload ảnh: " + (err.response?.data || err.message),
      );
      setImagePreview(null);
    } finally {
      setImageUploading(false);
    }
  };

  const handleEditImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setEditPreview(URL.createObjectURL(file));
    setEditUploading(true);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await authApis().post(endpoints.adminUploadImage, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setEditForm((f) => ({ ...f, mainImage: res.data.url }));
      notify("success", "Tải ảnh lên thành công!");
    } catch (err) {
      notify(
        "danger",
        "Lỗi upload ảnh: " + (err.response?.data || err.message),
      );
    } finally {
      setEditUploading(false);
    }
  };

  const handleAddRoom = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().post(endpoints.adminAddRoom, roomForm);
      notify("success", "Thêm phòng thành công!");
      setRoomForm({
        name: "",
        price: "",
        floor: "",
        note: "",
        mainImage: "",
        status: "AVAILABLE",
        roomTypeId: roomTypes[0]?.id || "",
      });
      setImagePreview(null);
      reloadFromStart();
    } catch (err) {
      notify(
        "danger",
        "Lỗi thêm phòng: " + (err.response?.data || err.message),
      );
    } finally {
      setSubmitting(false);
    }
  };

  const openEditRoom = (room) => {
    setEditForm({
      id: room.id,
      name: room.name || "",
      price: room.price || "",
      floor: room.floor || "",
      note: room.note || "",
      mainImage: room.mainImage || "",
      status: room.status || "AVAILABLE",
      roomTypeId: room.roomType?.id || "",
    });
    setEditPreview(room.mainImage || null);
    setEditShow(true);
  };

  const handleEditRoom = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await authApis().put(endpoints.adminUpdateRoom(editForm.id), editForm);
      notify("success", "Cập nhật phòng thành công!");
      setEditShow(false);
      reloadFromStart();
    } catch (err) {
      notify(
        "danger",
        "Lỗi cập nhật phòng: " + (err.response?.data || err.message),
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteRoom = async (id) => {
    if (!window.confirm("Xóa phòng này? Thao tác không thể hoàn tác.")) return;
    try {
      await authApis().delete(endpoints.adminDeleteRoom(id));
      notify("success", "Đã xóa phòng.");
      reloadFromStart();
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
    } catch {
      setRoomImagesList([]);
    }
  };

  const handleUploadRoomImages = async (e) => {
    const files = Array.from(e.target.files);
    if (!files.length) return;
    setImgUploading(true);
    try {
      for (const file of files) {
        const formData = new FormData();
        formData.append("file", file);
        await authApis().post(
          endpoints.adminRoomImages(imagesRoomId),
          formData,
          { headers: { "Content-Type": "multipart/form-data" } },
        );
      }
      const fresh = await authApis().get(
        endpoints.adminRoomImages(imagesRoomId),
      );
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
      setRoomImagesList((prev) => prev.filter((img) => img.id !== imageId));
      notify("success", "Đã xóa ảnh.");
    } catch {
      notify("danger", "Lỗi xóa ảnh.");
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
      {/* Form thêm phòng */}
      <Row className="mb-3">
        <Col>
          <Card className="shadow-sm border-0">
            <Card.Header className="fw-bold bg-info text-white">
              Thêm phòng mới
            </Card.Header>
            <Card.Body>
              <Form onSubmit={handleAddRoom}>
                <Row>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Tên phòng *</Form.Label>
                      <Form.Control
                        required
                        value={roomForm.name}
                        onChange={(e) =>
                          setRoomForm({ ...roomForm, name: e.target.value })
                        }
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Giá/đêm (VNĐ) *</Form.Label>
                      <Form.Control
                        type="number"
                        required
                        value={roomForm.price}
                        onChange={(e) =>
                          setRoomForm({ ...roomForm, price: e.target.value })
                        }
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Tầng</Form.Label>
                      <Form.Control
                        type="number"
                        value={roomForm.floor}
                        onChange={(e) =>
                          setRoomForm({ ...roomForm, floor: e.target.value })
                        }
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Loại phòng *</Form.Label>
                      <Form.Select
                        required
                        value={roomForm.roomTypeId}
                        onChange={(e) =>
                          setRoomForm({
                            ...roomForm,
                            roomTypeId: e.target.value,
                          })
                        }
                      >
                        {roomTypes.map((rt) => (
                          <option key={rt.id} value={rt.id}>
                            {rt.name}
                          </option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Trạng thái</Form.Label>
                      <Form.Select
                        value={roomForm.status}
                        onChange={(e) =>
                          setRoomForm({ ...roomForm, status: e.target.value })
                        }
                      >
                        {ROOM_STATUSES.map((s) => (
                          <option key={s}>{s}</option>
                        ))}
                      </Form.Select>
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>Ảnh chính</Form.Label>
                      <Form.Control
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        disabled={imageUploading}
                      />
                      {imageUploading && (
                        <div className="mt-1 text-muted small">
                          <Spinner size="sm" /> Đang tải ảnh lên...
                        </div>
                      )}
                      {imagePreview && !imageUploading && (
                        <img
                          src={imagePreview}
                          alt="preview"
                          style={{
                            width: "100%",
                            maxHeight: 120,
                            objectFit: "cover",
                            marginTop: 8,
                            borderRadius: 4,
                          }}
                        />
                      )}
                    </Form.Group>
                  </Col>
                  <Col md={12}>
                    <Form.Group className="mb-3">
                      <Form.Label>Ghi chú</Form.Label>
                      <Form.Control
                        as="textarea"
                        rows={2}
                        value={roomForm.note}
                        onChange={(e) =>
                          setRoomForm({ ...roomForm, note: e.target.value })
                        }
                      />
                    </Form.Group>
                  </Col>
                </Row>
                <Button type="submit" variant="info" disabled={submitting}>
                  {submitting ? <Spinner size="sm" /> : "Thêm phòng"}
                </Button>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Danh sách phòng */}
      <Card className="shadow-sm border-0">
        <Card.Header className="fw-bold">
          Danh sách phòng
          <span className="text-muted fw-normal ms-2" style={{ fontSize: 13 }}>
            ({rooms.length} phòng đã tải)
          </span>
        </Card.Header>
        <Card.Body>
          <Table hover responsive size="sm" className="align-middle">
            <thead className="table-light">
              <tr>
                <th>#</th>
                <th>Tên</th>
                <th>Loại</th>
                <th>Tầng</th>
                <th>Giá</th>
                <th>Trạng thái</th>
                <th>Ảnh</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rooms.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td className="fw-bold">{r.name}</td>
                  <td>{r.roomType?.name || "—"}</td>
                  <td>{r.floor ?? "—"}</td>
                  <td>{Number(r.price).toLocaleString()}đ</td>
                  <td>
                    <Badge
                      bg={
                        r.status === "AVAILABLE"
                          ? "success"
                          : r.status === "OCCUPIED"
                            ? "danger"
                            : "warning"
                      }
                    >
                      {r.status}
                    </Badge>
                  </td>
                  <td>
                    {r.mainImage ? (
                      <img
                        src={r.mainImage}
                        alt=""
                        style={{
                          width: 50,
                          height: 35,
                          objectFit: "cover",
                          borderRadius: 3,
                        }}
                      />
                    ) : (
                      <span className="text-muted">—</span>
                    )}
                  </td>
                  <td>
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      className="me-1"
                      onClick={() => openImagesModal(r)}
                    >
                      Ảnh
                    </Button>
                    <Button
                      variant="outline-primary"
                      size="sm"
                      className="me-1"
                      onClick={() => openEditRoom(r)}
                    >
                      Sửa
                    </Button>
                    <Button
                      variant="outline-danger"
                      size="sm"
                      onClick={() => handleDeleteRoom(r.id)}
                    >
                      Xóa
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>

          {/* Load more */}
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
                  "Xem thêm phòng"
                )}
              </Button>
            ) : (
              rooms.length > 0 && (
                <span className="text-muted small">
                  Đã hiển thị tất cả {rooms.length} phòng
                </span>
              )
            )}
          </div>
        </Card.Body>
      </Card>

      {/* Modal ảnh album */}
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
            <p className="text-muted text-center py-3">
              Chưa có ảnh nào cho phòng này.
            </p>
          ) : (
            <Row className="g-2">
              {roomImagesList.map((img) => (
                <Col xs={6} md={4} key={img.id}>
                  <div className="position-relative">
                    <img
                      src={img.imageUrl}
                      alt=""
                      style={{
                        width: "100%",
                        height: 140,
                        objectFit: "cover",
                        borderRadius: 6,
                      }}
                    />
                    <Button
                      variant="danger"
                      size="sm"
                      className="position-absolute top-0 end-0 m-1"
                      style={{ padding: "2px 6px", fontSize: 12 }}
                      onClick={() => handleDeleteRoomImage(img.id)}
                    >
                      ✕
                    </Button>
                  </div>
                </Col>
              ))}
            </Row>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setImagesShow(false)}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal sửa phòng */}
      <Modal show={editShow} onHide={() => setEditShow(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Sửa thông tin phòng #{editForm.id}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleEditRoom}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Tên phòng *</Form.Label>
                  <Form.Control
                    required
                    value={editForm.name || ""}
                    onChange={(e) =>
                      setEditForm({ ...editForm, name: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Giá/đêm (VNĐ) *</Form.Label>
                  <Form.Control
                    type="number"
                    required
                    value={editForm.price || ""}
                    onChange={(e) =>
                      setEditForm({ ...editForm, price: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Tầng</Form.Label>
                  <Form.Control
                    type="number"
                    value={editForm.floor || ""}
                    onChange={(e) =>
                      setEditForm({ ...editForm, floor: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Loại phòng *</Form.Label>
                  <Form.Select
                    required
                    value={editForm.roomTypeId || ""}
                    onChange={(e) =>
                      setEditForm({ ...editForm, roomTypeId: e.target.value })
                    }
                  >
                    {roomTypes.map((rt) => (
                      <option key={rt.id} value={rt.id}>
                        {rt.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Trạng thái</Form.Label>
                  <Form.Select
                    value={editForm.status || "AVAILABLE"}
                    onChange={(e) =>
                      setEditForm({ ...editForm, status: e.target.value })
                    }
                  >
                    {ROOM_STATUSES.map((s) => (
                      <option key={s}>{s}</option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>Ảnh chính</Form.Label>
                  <Form.Control
                    type="file"
                    accept="image/*"
                    onChange={handleEditImageChange}
                    disabled={editUploading}
                  />
                  {editUploading && (
                    <div className="mt-1 text-muted small">
                      <Spinner size="sm" /> Đang tải ảnh lên...
                    </div>
                  )}
                  {editPreview && !editUploading && (
                    <img
                      src={editPreview}
                      alt="preview"
                      style={{
                        width: "100%",
                        maxHeight: 150,
                        objectFit: "cover",
                        marginTop: 8,
                        borderRadius: 4,
                      }}
                    />
                  )}
                </Form.Group>
              </Col>
              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>Ghi chú</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={2}
                    value={editForm.note || ""}
                    onChange={(e) =>
                      setEditForm({ ...editForm, note: e.target.value })
                    }
                  />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setEditShow(false)}>
              Hủy
            </Button>
            <Button
              type="submit"
              variant="primary"
              disabled={submitting || editUploading}
            >
              {submitting ? <Spinner size="sm" /> : "Lưu thay đổi"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </>
  );
};

export default RoomsTab;
