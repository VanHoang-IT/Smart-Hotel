import axios from "axios";

export const endpoints = {
  rooms: "/rooms",
  roomTypes: "/roomTypes",
};

export default axios.create({
  baseURL: "http://localhost:8080/SmartHotel/api",
});
