import { useContext, useEffect, useRef, useState } from "react";
import {
  getDatabase,
  ref,
  query,
  orderByChild,
  limitToLast,
  onChildAdded,
  off,
} from "firebase/database";
import app from "../configs/Firebase";
import { getOrCreateRoom, sendMessage } from "../configs/Chat";
import { MyUserContext } from "../configs/Contexts";
import "./ChatBox.css";

const ChatBox = () => {
  const [user] = useContext(MyUserContext);
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const [roomId, setRoomId] = useState(null);
  const [loadingRoom, setLoadingRoom] = useState(false);
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef();

  useEffect(() => {
    setRoomId(null);
    setMessages([]);
    if (
      !user ||
      !user.id ||
      user.role === "RECEPTIONIST" ||
      user.role === "ROLE_ADMIN"
    )
      return;
    setLoadingRoom(true);
    getOrCreateRoom()
      .then((data) => setRoomId(data.room_id))
      .catch(console.error)
      .finally(() => setLoadingRoom(false));
  }, [user?.id]);

  useEffect(() => {
    if (!roomId) return;
    const db = getDatabase(app);
    const msgRef = query(
      ref(db, `chats/${roomId}/messages`),
      orderByChild("timestamp"),
      limitToLast(50),
    );
    const handler = onChildAdded(msgRef, (snap) => {
      const msg = { id: snap.key, ...snap.val() };
      setMessages((prev) =>
        prev.find((m) => m.id === msg.id) ? prev : [...prev, msg],
      );
    });
    return () => {
      off(msgRef, "child_added", handler);
      setMessages([]);
    };
  }, [roomId]);

  useEffect(() => {
    if (isOpen) messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isOpen]);

  if (
    !user ||
    !user.id ||
    user.role === "RECEPTIONIST" ||
    user.role === "ROLE_ADMIN"
  )
    return null;

  const handleSend = async (e) => {
    e.preventDefault();
    if (!text.trim() || !roomId || sending) return;
    const toSend = text.trim();
    setText("");
    setSending(true);
    try {
      await sendMessage(roomId, toSend);
    } catch {
      setText(toSend);
    } finally {
      setSending(false);
    }
  };

  const formatTime = (ts) =>
    ts
      ? new Date(ts).toLocaleTimeString("vi-VN", {
          hour: "2-digit",
          minute: "2-digit",
        })
      : "";

  const isMe = (msg) => msg.sender_role !== "RECEPTIONIST";

  return (
    <div className="chatbox-wrapper">
      {/* Nút mở */}
      {!isOpen && (
        <button className="chatbox-toggle" onClick={() => setIsOpen(true)}>
          <i className="bi bi-chat-dots-fill" />
          <span>Lễ tân</span>
        </button>
      )}

      {/* Cửa sổ chat */}
      {isOpen && (
        <div className="chatbox-window">
          <div className="chatbox-header">
            <div className="chatbox-header-avatar">
              <i className="bi bi-person-badge-fill" />
            </div>
            <div className="chatbox-header-info">
              <div className="chatbox-header-name">Lễ tân Smart Hotel</div>
              <div className="chatbox-header-status">
                <span className="status-dot" /> Trực tuyến
              </div>
            </div>
            {/* Nút X trong header */}
            <button className="chatbox-close" onClick={() => setIsOpen(false)}>
              <i className="bi bi-x-lg" />
            </button>
          </div>

          <div className="chatbox-messages">
            {loadingRoom && (
              <div className="chatbox-center">
                <div className="spinner-border spinner-border-sm text-primary" />
              </div>
            )}
            {!loadingRoom && messages.length === 0 && (
              <div className="chatbox-center text-muted">
                Hãy gửi tin nhắn đầu tiên...
              </div>
            )}
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`chatbox-msg ${isMe(msg) ? "mine" : "theirs"}`}
              >
                <div className="chatbox-bubble">{msg.text}</div>
                <div className="chatbox-time">{formatTime(msg.timestamp)}</div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          <form className="chatbox-input-row" onSubmit={handleSend}>
            <input
              type="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Nhập tin nhắn..."
              className="chatbox-input"
              autoFocus
            />
            <button
              type="submit"
              className="chatbox-send"
              disabled={!text.trim() || sending}
            >
              <i className="bi bi-send-fill" />
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default ChatBox;
