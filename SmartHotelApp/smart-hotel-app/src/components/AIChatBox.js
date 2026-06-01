import { useState, useRef, useEffect, useContext } from "react";
import Apis, { endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/Contexts";

const AIChatBox = () => {
  const [user] = useContext(MyUserContext);
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      content:
        "Xin chào! Tôi là AI tư vấn của Smart Hotel. Bạn muốn đặt phòng loại nào? Hãy cho tôi biết ngân sách, số người và yêu cầu của bạn nhé! 😊",
    },
  ]);
  const [text, setText] = useState("");
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState([]);
  const messagesEndRef = useRef();

  useEffect(() => {
    const loadRooms = async () => {
      try {
        const res = await Apis.get(endpoints.rooms);
        setRooms(res.data);
      } catch (ex) {}
    };
    loadRooms();
  }, []);

  useEffect(() => {
    if (isOpen) messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isOpen]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!text.trim() || loading) return;

    const userMsg = { role: "user", content: text.trim() };
    const newMessages = [...messages, userMsg];
    setMessages(newMessages);
    setText("");
    setLoading(true);

    try {
      const roomsInfo = rooms
        .map(
          (r) =>
            `Phòng ${r.id}: ${r.name}, loại ${r.roomType?.name || ""}, giá ${r.price?.toLocaleString()}đ/đêm, trạng thái: ${r.status}`,
        )
        .join("\n");

      const systemPrompt = `Bạn là AI tư vấn đặt phòng của Smart Hotel. Hãy tư vấn phòng phù hợp dựa trên nhu cầu khách hàng.
Danh sách phòng hiện có:
${roomsInfo}

Hãy tư vấn ngắn gọn, thân thiện bằng tiếng Việt. Gợi ý phòng cụ thể dựa trên ngân sách và mô tả của khách.`;

      const response = await Apis.post(endpoints.aiChat, {
        system: systemPrompt,
        messages: newMessages.map((m) => ({
          role: m.role,
          content: m.content,
        })),
      });

      const aiReply =
        response.data?.text || "Xin lỗi, tôi không thể trả lời lúc này.";
      setMessages((prev) => [...prev, { role: "assistant", content: aiReply }]);
    } catch (ex) {
      console.error("AI Chat error:", ex.response?.data || ex.message);
      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content: "Lỗi: " + (ex.response?.data || ex.message),
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const formatTime = () =>
    new Date().toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });

  if (
    user &&
    (user.role === "RECEPTIONIST" ||
      user.role === "ROLE_ADMIN" ||
      user.role === "ROLE_STAFF")
  ) {
    return null;
  }

  return (
    <div className="ai-chatbox-wrapper">
      <button
        className={`chatbox-toggle ai-toggle ${isOpen ? "open" : ""}`}
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? (
          <i className="bi bi-x-lg" />
        ) : (
          <>
            <i className="bi bi-robot" />
            <span>AI Tư vấn</span>
          </>
        )}
      </button>

      {isOpen && (
        <div className="chatbox-window">
          <div className="chatbox-header ai-header">
            <div className="chatbox-header-avatar">
              <i className="bi bi-robot" />
            </div>
            <div>
              <div className="chatbox-header-name">AI Tư vấn Smart Hotel</div>
              <div className="chatbox-header-status">
                <span className="status-dot" /> Sẵn sàng tư vấn
              </div>
            </div>
          </div>

          <div className="chatbox-messages">
            {messages.map((msg, idx) => (
              <div
                key={idx}
                className={`chatbox-msg ${msg.role === "user" ? "mine" : "theirs"}`}
              >
                <div
                  className="chatbox-bubble"
                  style={{ whiteSpace: "pre-wrap" }}
                >
                  {msg.content}
                </div>
                <div className="chatbox-time">{formatTime()}</div>
              </div>
            ))}
            {loading && (
              <div className="chatbox-msg theirs">
                <div className="chatbox-bubble ai-typing">
                  <span />
                  <span />
                  <span />
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <form className="chatbox-input-row" onSubmit={handleSend}>
            <input
              type="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Nhập yêu cầu của bạn..."
              className="chatbox-input"
              autoFocus
            />
            <button
              type="submit"
              className="chatbox-send ai-send"
              disabled={!text.trim() || loading}
            >
              <i className="bi bi-send-fill" />
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default AIChatBox;
