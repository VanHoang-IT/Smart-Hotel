import React, { useState } from "react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";

const Calendar = () => {
  const [range, setRange] = useState();

  const noAvailability = [
    new Date(2026, 3, 10),
    new Date(2026, 3, 11),
    new Date(2026, 3, 12),
    new Date(2026, 3, 13),
    new Date(2026, 3, 14),
  ];

  return (
    <div className="mt-6 pt-6 border-t">
      <h3 className="fs-3 mb-4">Lịch phòng trống</h3>

      <div className="p-4 border fs-5">
        <DayPicker
          mode="range"
          selected={range}
          onSelect={setRange}
          numberOfMonths={2}
          modifiers={{
            noAvailability: noAvailability,
          }}
          modifiersClassNames={{
            noAvailability: "bg-danger",
          }}
          classNames={{ table: "table table-bordered", day: "border" }}
        />

        <div className="mt-5 d-flex fs-6">
          <div className="m-2">
            <span className="bg-primary d-inline-block p-2"></span>
            Ngày đã chọn
          </div>

          <div className="m-2 fs-6">
            <span className="border d-inline-block p-2"></span>
            Ngày còn trống
          </div>

          <div className="m-2 fs-6">
            <span className="bg-danger d-inline-block p-2"></span>
            Ngày đã được đặt
          </div>
        </div>
      </div>
    </div>
  );
};

export default Calendar;
