import React, { useEffect, useState } from "react";
import Apis, { endpoints } from "../configs/Apis";

const Review = ({ roomId }) => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadReviews = async () => {
    try {
      setLoading(true);
      const res = await Apis.get(endpoints.roomReviews(roomId));
      setReviews(res.data);
    } catch (ex) {
      console.error("Lỗi tải review:", ex);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (roomId) loadReviews();
  }, [roomId]);

  const renderStars = (count) =>
    Array.from({ length: 5 }, (_, i) => (
      <span
        key={i}
        style={{
          color: i < count ? "#f5a623" : "#ddd",
          fontSize: 16,
        }}
      >
        ★
      </span>
    ));

  const avgRating =
    reviews.length > 0
      ? (
          reviews.reduce((sum, review) => sum + review.rating, 0) /
          reviews.length
        ).toFixed(1)
      : null;

  return (
    <div style={{ marginTop: 48 }}>
      <hr style={{ marginBottom: 24 }} />

      <h4
        style={{
          marginBottom: 16,
          display: "flex",
          alignItems: "center",
          gap: 10,
        }}
      >
        Đánh giá của khách
        {avgRating && (
          <span
            style={{
              backgroundColor: "#fff8e1",
              color: "#f5a623",
              borderRadius: 20,
              padding: "2px 12px",
              fontSize: 14,
              fontWeight: "bold",
            }}
          >
            ★ {avgRating}
            <span
              style={{
                color: "#999",
                fontWeight: "normal",
                marginLeft: 6,
              }}
            >
              ({reviews.length} đánh giá)
            </span>
          </span>
        )}
      </h4>

      {loading ? (
        <p style={{ color: "#999" }}>Đang tải đánh giá...</p>
      ) : reviews.length === 0 ? (
        <p
          style={{
            color: "#999",
            fontStyle: "italic",
          }}
        >
          Chưa có đánh giá nào.
        </p>
      ) : (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            gap: 12,
          }}
        >
          {reviews.map((r) => (
            <div
              key={r.id}
              style={{
                border: "1px solid #eee",
                borderRadius: 10,
                padding: "14px 18px",
                backgroundColor: "#fafafa",
              }}
            >
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "flex-start",
                }}
              >
                <div
                  style={{
                    display: "flex",
                    gap: 12,
                    alignItems: "flex-start",
                  }}
                >
                  <div
                    style={{
                      width: 42,
                      height: 42,
                      borderRadius: "50%",
                      backgroundColor: "#1a73a7",
                      color: "#fff",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontWeight: "bold",
                      flexShrink: 0,
                    }}
                  >
                    {(r.customerName || "K").charAt(0).toUpperCase()}
                  </div>

                  <div>
                    <strong
                      style={{
                        fontSize: 15,
                      }}
                    >
                      {r.customerName || "Khách hàng"}
                    </strong>

                    <div style={{ marginTop: 4 }}>{renderStars(r.rating)}</div>

                    <div
                      style={{
                        fontSize: 13,
                        color: "#666",
                        marginTop: 2,
                      }}
                    >
                      {r.rating}/5 sao
                    </div>
                  </div>
                </div>

                <small
                  style={{
                    color: "#aaa",
                    fontSize: 12,
                    whiteSpace: "nowrap",
                  }}
                >
                  {new Date(r.createdAt).toLocaleString("vi-VN")}
                </small>
              </div>

              {r.comment && (
                <p
                  style={{
                    marginTop: 12,
                    marginBottom: 0,
                    color: "#444",
                    lineHeight: 1.6,
                  }}
                >
                  {r.comment}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Review;
