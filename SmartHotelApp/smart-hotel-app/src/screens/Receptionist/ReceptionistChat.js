import { useContext, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getDatabase,
  ref,
  query,
  orderByChild,
  limitToLast,
  onChildAdded,
  onValue,
  off,
  set,
  remove,
} from "firebase/database";
import app from "../../configs/Firebase";
import { sendMessage, markAsSeen } from "../../configs/Chat";
import { MyUserContext } from "../../configs/Contexts";
import "./ReceptionistChat.css";

const ChatRoom = ({ roomId, guestName }) => {
  const [user] = useContext(MyUserContext);
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const [sending, setSending] = useState(false);
  const [otherTyping, setOtherTyping] = useState(null);
  const messagesEndRef = useRef();
  const typingTimeoutRef = useRef();

  const myName = user?.fullName || user?.username || "Lễ tân";
  const myId = user?.id || user?.username;

  useEffect(() => {
    setMessages([]);
    if (!roomId) return;
    markAsSeen(roomId).catch(console.error);

    const db = getDatabase(app);

    const msgRef = query(
      ref(db, `chats/${roomId}/messages`),
      orderByChild("timestamp"),
      limitToLast(50),
    );
    const msgHandler = onChildAdded(msgRef, (snap) => {
      const msg = { id: snap.key, ...snap.val() };
      setMessages((prev) =>
        prev.find((m) => m.id === msg.id) ? prev : [...prev, msg],
      );
    });

    const typingRef = ref(db, `chats/${roomId}/typing`);
    const typingHandler = onValue(typingRef, (snap) => {
      const data = snap.val();
      if (!data) {
        setOtherTyping(null);
        return;
      }
      const others = Object.entries(data).filter(
        ([id]) => String(id) !== String(myId),
      );
      if (others.length > 0) {
        setOtherTyping(others[0][1].name);
      } else {
        setOtherTyping(null);
      }
    });

    return () => {
      off(msgRef, "child_added", msgHandler);
      off(typingRef, "value", typingHandler);
      remove(ref(db, `chats/${roomId}/typing/${myId}`));
    };
  }, [roomId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, otherTyping]);

  const handleTyping = (value) => {
    setText(value);
    if (!roomId) return;
    const db = getDatabase(app);
    const typingRef = ref(db, `chats/${roomId}/typing/${myId}`);

    if (value.trim()) {
      set(typingRef, { name: myName, at: Date.now() });
      clearTimeout(typingTimeoutRef.current);
      typingTimeoutRef.current = setTimeout(() => {
        remove(typingRef);
      }, 3000);
    } else {
      clearTimeout(typingTimeoutRef.current);
      remove(typingRef);
    }
  };

  const handleSend = async (e) => {
    e.preventDefault();
    if (!text.trim() || !roomId || sending) return;
    const toSend = text.trim();
    setText("");
    setSending(true);
    if (roomId) {
      const db = getDatabase(app);
      remove(ref(db, `chats/${roomId}/typing/${myId}`));
      clearTimeout(typingTimeoutRef.current);
    }
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

  const isMe = (msg) =>
    msg.sender_role === "RECEPTIONIST" &&
    String(msg.sender_id) === String(myId);

  return (
    <div className="rchat-room">
      <div className="rchat-room-header">
        <div className="rchat-room-avatar">
          <i className="bi bi-person-fill" />
        </div>
        <div>
          <div className="rchat-room-name">{guestName || "Khách"}</div>
          {otherTyping ? (
            <small className="rchat-typing-indicator">
              <span className="typing-dots">
                <span />
                <span />
                <span />
              </span>
              {otherTyping} đang trả lời...
            </small>
          ) : (
            <small className="text-muted">Đang trò chuyện</small>
          )}
        </div>
      </div>

      <div className="rchat-messages">
        {messages.length === 0 && (
          <div className="rchat-empty">Chưa có tin nhắn</div>
        )}
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`rchat-msg ${isMe(msg) ? "mine" : "theirs"}`}
          >
            <div
              className="rchat-sender"
              style={{ textAlign: isMe(msg) ? "right" : "left" }}
            >
              {msg.sender_role === "RECEPTIONIST"
                ? isMe(msg)
                  ? "Bạn"
                  : msg.sender_name || "Lễ tân"
                : msg.sender_name || guestName}
            </div>
            <div className="rchat-bubble">{msg.text}</div>
            <div className="rchat-time">{formatTime(msg.timestamp)}</div>
          </div>
        ))}
        {otherTyping && (
          <div className="rchat-msg theirs">
            <div className="rchat-sender">{otherTyping}</div>
            <div className="rchat-bubble rchat-bubble-typing">
              <span className="typing-dots">
                <span />
                <span />
                <span />
              </span>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <form className="rchat-input-row" onSubmit={handleSend}>
        <input
          type="text"
          value={text}
          onChange={(e) => handleTyping(e.target.value)}
          placeholder="Trả lời khách..."
          className="rchat-input"
          autoFocus
        />
        <button
          type="submit"
          className="rchat-send"
          disabled={!text.trim() || sending}
        >
          <i className="bi bi-send-fill" /> Gửi
        </button>
      </form>
    </div>
  );
};

const ReceptionistChat = () => {
  const [user] = useContext(MyUserContext);
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user || user.role !== "RECEPTIONIST") {
      navigate("/");
    }
  }, [user]);

  useEffect(() => {
    if (!user || user.role !== "RECEPTIONIST") return;
    const db = getDatabase(app);
    const chatsRef = ref(db, "chats");
    onValue(chatsRef, (snapshot) => {
      const data = snapshot.val();
      if (!data) {
        setRooms([]);
        setLoading(false);
        return;
      }
      const list = Object.entries(data).map(([id, val]) => ({
        room_id: id,
        guest_name: val.info?.guest_name || id.replace("guest_", "Khách #"),
        last_message: val.info?.last_message || "",
        updated_at: val.info?.updated_at || 0,
      }));
      list.sort((a, b) => (b.updated_at || 0) - (a.updated_at || 0));
      setRooms(list);
      setLoading(false);
    });
    return () => off(ref(db, "chats"));
  }, [user]);

  if (!user || user.role !== "RECEPTIONIST") {
    return null;
  }

  const formatRelTime = (ts) => {
    if (!ts) return "";
    const diff = Date.now() - ts;
    if (diff < 60000) return "Vừa xong";
    if (diff < 3600000) return `${Math.floor(diff / 60000)} phút trước`;
    if (diff < 86400000) return `${Math.floor(diff / 3600000)} giờ trước`;
    return new Date(ts).toLocaleDateString("vi-VN");
  };

  return (
    <div className="rchat-container">
      <div className="rchat-sidebar">
        <div className="rchat-sidebar-header">
          <i className="bi bi-chat-square-text-fill" /> Hộp thư
          {rooms.length > 0 && (
            <span className="badge bg-light text-primary ms-auto">
              {rooms.length}
            </span>
          )}
        </div>

        {loading && (
          <div className="text-center py-4">
            <div className="spinner-border spinner-border-sm text-primary" />
          </div>
        )}

        {!loading && rooms.length === 0 && (
          <div className="rchat-sidebar-empty">Chưa có cuộc hội thoại nào</div>
        )}

        {rooms.map((room) => (
          <div
            key={room.room_id}
            className={`rchat-list-item ${selectedRoom?.room_id === room.room_id ? "active" : ""}`}
            onClick={() => setSelectedRoom(room)}
          >
            <div className="rchat-list-avatar">
              <i className="bi bi-person-circle" />
            </div>
            <div className="rchat-list-info">
              <div className="rchat-list-name">{room.guest_name}</div>
              <div className="rchat-list-sub">
                {room.last_message || "Bắt đầu cuộc trò chuyện"}
              </div>
            </div>
            <div className="rchat-list-time">
              {formatRelTime(room.updated_at)}
            </div>
          </div>
        ))}
      </div>

      <div className="rchat-main">
        {selectedRoom ? (
          <ChatRoom
            key={selectedRoom.room_id}
            roomId={selectedRoom.room_id}
            guestName={selectedRoom.guest_name}
          />
        ) : (
          <div className="rchat-placeholder">
            <i className="bi bi-chat-dots" />
            <h5>Chọn một cuộc hội thoại</h5>
            <p>Chọn khách ở danh sách bên trái để bắt đầu trả lời</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ReceptionistChat;
