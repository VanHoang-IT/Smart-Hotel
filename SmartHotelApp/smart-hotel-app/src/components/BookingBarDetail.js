// import React, { useEffect, useState, useMemo } from "react";
// import { useNavigate } from "react-router-dom";
// import Apis, { endpoints } from "../configs/Apis";

// const BookingBarDetail = ({ roomPrice, roomId }) => {
//   const { checkIn, setCheckIn, checkOut, setCheckOut } = useBooking();
//   const [extraServices, setExtraServices] = useState([]);
//   const [selectedServiceIds, setSelectedServiceIds] = useState([]);

//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchServices = async () => {
//       try {
//         const res = await Apis.get(endpoints.extraServices);
//         const activeServices = res.data.filter(
//           (s) => s.active === true || s.active === 1,
//         );
//         setExtraServices(activeServices);
//       } catch (ex) {
//         console.error(ex);
//       }
//     };
//     fetchServices();
//   }, []);

//   const numberOfNights = useMemo(() => {
//     if (!checkIn || !checkOut) return 1;
//     const start = new Date(checkIn);
//     const end = new Date(checkOut);
//     const diff = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
//     return diff > 0 ? diff : 1;
//   }, [checkIn, checkOut]);

//   const handleToggleService = (id) => {
//     setSelectedServiceIds((prev) =>
//       prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id],
//     );
//   };

//   const totalPrice = useMemo(() => {
//     const base = (parseFloat(roomPrice) || 0) * numberOfNights;

//     const services = extraServices
//       .filter((s) => selectedServiceIds.includes(s.id))
//       .reduce((sum, s) => sum + parseFloat(s.price), 0);

//     return base + services;
//   }, [roomPrice, numberOfNights, selectedServiceIds, extraServices]);

//   const formatVND = (value) =>
//     new Intl.NumberFormat("vi-VN", {
//       style: "currency",
//       currency: "VND",
//     }).format(value);

//   const handleBookingClick = () => {
//     const isLogged =
//       localStorage.getItem("token") || localStorage.getItem("user");

//     if (!isLogged) {
//       alert("Vui lòng đăng nhập trước khi đặt phòng!");
//       return;
//     }

//     const bookingData = {
//       roomId,
//       roomPrice,
//       checkIn,
//       checkOut,
//       numberOfNights,
//       selectedServices: extraServices.filter((s) =>
//         selectedServiceIds.includes(s.id),
//       ),
//       totalPrice,
//     };

//     navigate("/checkout", { state: { bookingData } });
//   };

//   return (
//     <div className="border p-4">
//       <h4 className="font-medium mb-4">Your Reservation</h4>

//       <div className="mb-3">
//         <label className="block text-sm">Check-in</label>
//         <input
//           type="date"
//           className="w-full border px-2 py-1"
//           value={checkIn}
//           onChange={(e) => setCheckIn(e.target.value)}
//         />
//       </div>

//       <div className="mb-3">
//         <label className="block text-sm">Check-out</label>
//         <input
//           type="date"
//           className="w-full border px-2 py-1"
//           value={checkOut}
//           onChange={(e) => setCheckOut(e.target.value)}
//         />
//       </div>

//       <div className="mt-4">
//         <h5 className="text-sm mb-2">Extra Services</h5>

//         {extraServices.map((s) => (
//           <div key={s.id} className="flex justify-between mb-2 text-sm">
//             <label className="flex items-center gap-2">
//               <input
//                 type="checkbox"
//                 checked={selectedServiceIds.includes(s.id)}
//                 onChange={() => handleToggleService(s.id)}
//               />
//               {s.name}
//             </label>

//             <span>{formatVND(s.price)}</span>
//           </div>
//         ))}
//       </div>

//       <div className="mt-4 pt-3 border-t">
//         <div className="text-sm">Total ({numberOfNights} đêm)</div>

//         <div className="font-medium">{formatVND(totalPrice)}</div>
//       </div>

//       <button onClick={handleBookingClick} className="w-full mt-3 border py-2">
//         Đặt phòng ngay
//       </button>
//     </div>
//   );
// };

// export default BookingBarDetail;
