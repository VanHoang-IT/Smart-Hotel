import { authApis, endpoints } from "./Apis";

export const getOrCreateRoom = async () => {
  const res = await authApis().post(endpoints.chatRoom);
  return res.data;
};

export const sendMessage = async (roomId, text) => {
  const res = await authApis().post(endpoints.chatSend, {
    room_id: roomId,
    text,
  });
  return res.data;
};

export const getMessages = async (roomId, limit = 50) => {
  const res = await authApis().get(endpoints.chatMessages, {
    params: { room_id: roomId, limit },
  });
  return res.data;
};

export const getAllRooms = async () => {
  const res = await authApis().get(endpoints.chatRooms);
  return res.data;
};

export const markAsSeen = async (roomId) => {
  await authApis().patch(endpoints.chatSeen(roomId));
};
