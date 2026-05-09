// import { useNavigate } from "react-router-dom";
// import { Form, Button } from "react-bootstrap";

// const BookingBarSide = () => {
//   const navigate = useNavigate();

//   const { checkIn, setCheckIn, checkOut, setCheckOut } = useBooking();

//   const todayStr = new Date().toISOString().split("T")[0];

//   const handleBooking = (e) => {
//     e.preventDefault();
//     navigate(`/available?checkIn=${checkIn}&checkOut=${checkOut}`);
//   };

//   return (
//     <div className="mt-6">
//       <div className="border p-4">
//         <h4 className="font-medium mb-4">Kiểm tra phòng trống</h4>

//         <Form onSubmit={handleBooking}>
//           <Form.Group className="mb-3">
//             <Form.Label>Check-in</Form.Label>
//             <Form.Control
//               type="date"
//               value={checkIn}
//               min={todayStr}
//               onChange={(e) => setCheckIn(e.target.value)}
//             />
//           </Form.Group>

//           <Form.Group className="mb-3">
//             <Form.Label>Check-out</Form.Label>
//             <Form.Control
//               type="date"
//               value={checkOut}
//               min={todayStr}
//               onChange={(e) => setCheckOut(e.target.value)}
//             />
//           </Form.Group>

//           <Form.Group className="mb-3">
//             <Form.Label>Số phòng</Form.Label>
//             <Form.Select>
//               <option value="1">1 phòng</option>
//               <option value="2">2 phòng</option>
//               <option value="3">3 phòng</option>
//             </Form.Select>
//           </Form.Group>

//           <Form.Group className="mb-4">
//             <Form.Label>Guests</Form.Label>
//             <Form.Select>
//               <option value="1">1 người lớn</option>
//               <option value="2">2 người lớn</option>
//               <option value="3">2 người lớn 2 trẻ em</option>
//               <option value="4">2 người lớn 1 trẻ em</option>
//             </Form.Select>
//           </Form.Group>

//           <Button type="submit" className="w-100">
//             KIỂM TRA TÌNH TRẠNG PHÒNG
//           </Button>
//         </Form>
//       </div>
//     </div>
//   );
// };

// export default BookingBarSide;
