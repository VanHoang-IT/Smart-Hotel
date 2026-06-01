import { useEffect, useContext, useRef } from "react";
import { MyUserContext } from "../configs/Contexts";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const NotificationListener = () => {
  const [user] = useContext(MyUserContext);
  const clientRef = useRef(null);

  useEffect(() => {
    if (!user) return;

    const client = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/SmartHotel/ws"),
      onConnect: () => {
        console.log("WebSocket connected!");
        client.subscribe(`/topic/notifications/${user.id}`, (message) => {
          const data = JSON.parse(message.body);
          alert(`🔔 ${data.message}`);

          window.dispatchEvent(
            new CustomEvent("reservationUpdated", {
              detail: {
                reservationId: Number(data.reservationId),
                status: data.status,
              },
            }),
          );
        });
      },
      onDisconnect: () => console.log("WebSocket disconnected"),
      onStompError: (frame) => console.error("STOMP error:", frame),
    });

    client.activate();
    clientRef.current = client;

    return () => client.deactivate();
  }, [user]);

  return null;
};

export default NotificationListener;
