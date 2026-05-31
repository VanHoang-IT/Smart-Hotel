import axios from "axios";
import cookies from "react-cookies";

export const endpoints = {
  rooms: "/rooms",
  roomTypes: "/roomTypes",
  availableRooms: "/rooms/available",
  roomDetails: (id) => `/rooms/${id}`,
  extraServices: "/services",
  register: "/users",
  createReservation: "/secure/reservations",
  login: "/login",
  profile: "/secure/profile",
  customerProfileMe: "/secure/customer-profile/me",
  createCustomerProfile: "/secure/customer-profile",
  reservations: "/secure/reservations",
  reservationDetail: (id) => `/secure/reservations/${id}`,
  updateReservationStatus: (id) => `/secure/reservations/${id}/status`,
  payments: "/secure/payments",
  momoLink: "/secure/payments/momo-link",
  vnpayLink: "/secure/payments/vnpay-link",
  cancelReservation: (id) => `/secure/reservations/${id}/cancel`,
  deleteReservation: (id) => `/secure/reservations/${id}`,
  serviceOrders: (id) => `/secure/reservations/${id}/service-orders`,
  updateServiceOrderStatus: (id) => `/secure/service-orders/${id}/status`,
  serviceTotal: (id) => `/secure/reservations/${id}/service-total`,
  updatePaymentStatus: (id) => `/secure/payments/${id}/status`,
  roomBookings: (id) => `/rooms/${id}/bookings`,
  roomImages: (id) => `/rooms/${id}/images`,
  myReservations: "/secure/reservations/my",
  roomReviews: (id) => `/rooms/${id}/reviews`,
  createReview: (reservationId) =>
    `/secure/reservations/${reservationId}/review`,
  myHousekeepingTasks: "/secure/staff/housekeeping/my-tasks",
  updateHousekeepingStatus: (id) => `/secure/staff/housekeeping/${id}/status`,
  vnpayConfirm: "/secure/payments/vnpay-confirm",

  adminUsers: "/secure/admin/users",
  adminUpdateRole: (id) => `/secure/admin/users/${id}/role`,
  adminAddRoom: "/secure/admin/rooms",
  adminUpdateRoom: (id) => `/secure/admin/rooms/${id}`,
  adminDeleteRoom: (id) => `/secure/admin/rooms/${id}`,
  adminAddRoomType: "/secure/admin/room-types",
  adminUpdateRoomType: (id) => `/secure/admin/room-types/${id}`,
  adminDeleteRoomType: (id) => `/secure/admin/room-types/${id}`,
  adminHousekeeping: "/secure/admin/housekeeping",
  adminUpdateTaskStatus: (id) => `/secure/admin/housekeeping/${id}/status`,
  adminDeleteTask: (id) => `/secure/admin/housekeeping/${id}`,
  adminStaff: "/secure/admin/staff",
  updateRoomStatus: (id) => `/secure/rooms/${id}/status`,
  adminAddService: "/secure/services",
  adminUploadImage: "/secure/admin/upload-image",
  adminRoomImages: (id) => `/secure/admin/rooms/${id}/images`,
  adminDeleteRoomImage: (id) => `/secure/admin/room-images/${id}`,
  adminRevenueMonthly: "/secure/admin/revenue/monthly",
  chatRoom: "/secure/chat/room",
  chatSend: "/secure/chat/send",
  chatMessages: "/secure/chat/messages",
  chatRooms: "/secure/chat/rooms",
  chatSeen: (roomId) => `/secure/chat/rooms/${roomId}/seen`,
};

export const authApis = () => {
  console.info(cookies.load("token"));
  return axios.create({
    baseURL: process.env.REACT_APP_API_BASE_URL,
    headers: {
      Authorization: `Bearer ${cookies.load("token")}`,
      "ngrok-skip-browser-warning": "true",
    },
  });
};

export default axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
  headers: {
    "ngrok-skip-browser-warning": "true",
  },
});
