import { BrowserRouter, Routes, Route } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import "bootstrap/dist/css/bootstrap.min.css";
import SlideWindow from "./components/SlideWindow";
import Home from "./screens/Home/Home";
import "bootstrap-icons/font/bootstrap-icons.css";
import MyUserReducer from "./reducers/MyUserReducer";
import cookies from "react-cookies";
import { useReducer } from "react";
import { MyUserContext, MyBookingContext } from "./configs/Contexts";
import RoomDetails from "./screens/RoomDetails/RoomDetails";
import Register from "./screens/User/Register";
import Login from "./screens/User/Login";
import RoomTypes from "./screens/RoomType/RoomTypes";
import BookingBar from "./components/BookingBar";
import AvailableRooms from "./screens/Available/AvailableRooms";
import MyBookingReducer, { initialState } from "./reducers/MyBookingReducer";
import Cart from "./screens/Payment/Cart";
import Reservation from "./screens/Payment/Reservation"
import ReservationDetail from "./screens/Payment/ReservationDetails";

const App = () => {
  const [user, dispatch] = useReducer(
    MyUserReducer,
    cookies.load("user") || null,
  );

  const [booking, bookingDispatch] = useReducer(MyBookingReducer, initialState);
  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <MyBookingContext.Provider value={[booking, bookingDispatch]}>
        <BrowserRouter>
          <Header />

          <Routes>
            <Route
              path="/"
              element={
                <>
                  <SlideWindow />
                  <BookingBar />
                  <Home />
                </>
              }
            />
            <Route path="/rooms/:id" element={<RoomDetails />} />
            <Route path="/room-types/:typeId" element={<RoomTypes />} />
            <Route path="/available" element={<AvailableRooms />} />
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/reservation" element={<Reservation />} />
            <Route path="/reservation-detail/:id" element={<ReservationDetail />} />
          </Routes>

          <Footer />
        </BrowserRouter>
      </MyBookingContext.Provider>
    </MyUserContext.Provider>
  );
};

export default App;
