import React, { useContext, useEffect, useState } from "react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";
import Apis, { endpoints } from "../configs/Apis";
import { MyBookingContext } from "../configs/Contexts";

const expandDateRange = (checkIn, checkOut) => {
  const dates = [];
  const start = new Date(checkIn);
  const end = new Date(checkOut);
  start.setHours(0, 0, 0, 0);
  end.setHours(0, 0, 0, 0);
  let current = new Date(start);
  while (current <= end) {
    dates.push(new Date(current));
    current.setDate(current.getDate() + 1);
  }
  return dates;
};

const Calendar = ({ roomId }) => {
  const [booking, dispatch] = useContext(MyBookingContext);
  const [bookedDates, setBookedDates] = useState([]);

  useEffect(() => {
    if (!roomId) return;
    const fetchBookings = async () => {
      try {
        const res = await Apis.get(endpoints.roomBookings(roomId));
        const dates = [];
        res.data.forEach((b) => {
          if (b.checkIn && b.checkOut) {
            dates.push(...expandDateRange(b.checkIn, b.checkOut));
          }
        });
        setBookedDates(dates);
        dispatch({ type: "UPDATE_BOOKING", payload: { bookedDates: dates } });
      } catch (err) {
        console.error("Lỗi tải lịch đặt phòng:", err);
      }
    };
    fetchBookings();
  }, [roomId]);

  return (
    <div className="mt-6 pt-6 border-t">
      <h3 className="fs-3 mb-4">Lịch phòng trống</h3>

      <div className="p-4 border fs-5">
        <style>{`
          .rdp-day_booked {
            background-color: #dc3545 !important;
            color: white !important;
            border-radius: 0 !important;
            opacity: 1 !important;
          }
          .rdp-day_booked:hover {
            background-color: #bb2d3b !important;
          }
        `}</style>

        <DayPicker
          mode="single"
          numberOfMonths={2}
          disabled={bookedDates}
          modifiers={{
            booked: bookedDates,
          }}
          modifiersClassNames={{
            booked: "rdp-day_booked",
          }}
          classNames={{ table: "table table-bordered", day: "border" }}
        />

        <div className="mt-5 d-flex fs-6">
          <div className="m-2 fs-6">
            <span className="border d-inline-block p-2"></span>
            {" "}Ngày còn trống
          </div>
          <div className="m-2 fs-6">
            <span className="bg-danger d-inline-block p-2"></span>
            {" "}Ngày đã được đặt
          </div>
        </div>
      </div>
    </div>
  );
};

export default Calendar;
