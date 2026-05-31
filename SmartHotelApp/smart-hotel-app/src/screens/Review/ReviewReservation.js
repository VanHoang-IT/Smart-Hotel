import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { authApis, endpoints } from "../../configs/Apis";

const ReviewReservation = () => {
  const { reservationId } = useParams();
  const navigate = useNavigate();

  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [roomName, setRoomName] = useState("");
  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const loadReservation = async () => {
      try {
        const res = await authApis().get(
          endpoints.reservationDetail(reservationId),
        );

        const room = res.data.rooms?.[0];

        if (room) {
          setRoomName(room.roomName);
        }
      } catch (ex) {
        console.error(ex);
      } finally {
        setLoading(false);
      }
    };

    loadReservation();
  }, [reservationId]);

  const submit = async (e) => {
    e.preventDefault();

    try {
      setSubmitting(true);
      setError("");

      await authApis().post(endpoints.createReview(reservationId), {
        reservationId: Number(reservationId),
        rating,
        comment,
      });

      setSuccess("Đánh giá thành công!");

      setTimeout(() => {
        navigate("/my-reservations");
      }, 1500);
    } catch (ex) {
      console.error("Review Error:", ex);

      setError(
        ex.response?.data?.message ||
          ex.response?.data ||
          ex.message ||
          "Không thể gửi đánh giá.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <Container className="mt-5 text-center">
        <Spinner animation="border" />
      </Container>
    );
  }

  return (
    <Container className="mt-5 mb-5">
      <Card className="shadow-sm border-0">
        <Card.Header className="bg-primary text-white">
          <h4 className="mb-0">
            Đánh giá {roomName ? roomName : `đơn #${reservationId}`}
          </h4>
        </Card.Header>

        <Card.Body>
          {roomName && (
            <Alert variant="info">
              Phòng: <strong>{roomName}</strong>
            </Alert>
          )}

          {error && <Alert variant="danger">{error}</Alert>}

          {success && <Alert variant="success">{success}</Alert>}

          <Form onSubmit={submit}>
            <Form.Group className="mb-3">
              <Form.Label>Số sao</Form.Label>

              <Form.Select
                value={rating}
                onChange={(e) => setRating(Number(e.target.value))}
              >
                <option value={5}>★★★★★ - 5 sao</option>
                <option value={4}>★★★★☆ - 4 sao</option>
                <option value={3}>★★★☆☆ - 3 sao</option>
                <option value={2}>★★☆☆☆ - 2 sao</option>
                <option value={1}>★☆☆☆☆ - 1 sao</option>
              </Form.Select>
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Nhận xét</Form.Label>

              <Form.Control
                as="textarea"
                rows={4}
                value={comment}
                maxLength={255}
                onChange={(e) => setComment(e.target.value)}
                placeholder="Chia sẻ trải nghiệm của bạn..."
              />
            </Form.Group>

            <Button type="submit" disabled={submitting}>
              {submitting ? "Đang gửi..." : "Gửi đánh giá"}
            </Button>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ReviewReservation;
