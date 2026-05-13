import axios from "axios";
import cookies from "react-cookies";

export const endpoints = {
  rooms: "/rooms",
  roomTypes: "/roomTypes",
  availableRooms: "/rooms/available",
  roomDetails: (id) => `/rooms/${id}`,
  extraServices: "/services",
  register: "/users",
  createReservation:'/secure/reservations',
  login: "/login",
  profile: "/secure/profile",
  reservations: "/secure/reservations",
  payments: "/secure/payments",
  momoLink: "/secure/payments/momo-link",
  cancelReservation: (id) => `/secure/reservations/${id}/cancel`,
  serviceOrders: (id) => `/secure/reservations/${id}/service-orders`,
  serviceTotal: (id) => `/secure/reservations/${id}/service-total`,
};

export const authApis = () => {
  console.info(cookies.load("token"));
  return axios.create({
    baseURL: "http://localhost:8080/SmartHotel/api/",
    headers: {
      Authorization: `Bearer ${cookies.load("token")}`,
    },
  });
};

export default axios.create({
  baseURL: "http://localhost:8080/SmartHotel/api/",
});
