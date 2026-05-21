const getToday = () => {
  const d = new Date();
  return d.toISOString().split("T")[0];
};

const getTomorrow = () => {
  const d = new Date();
  d.setDate(d.getDate() + 1);
  return d.toISOString().split("T")[0];
};

export const initialState = {
  checkIn: getToday(),
  checkOut: getTomorrow(),
  adults: 2,
  children: 0,
  bookedDates: [],
};

const MyBookingReducer = (current = initialState, action) => {
  switch (action.type) {
    case "UPDATE_BOOKING":
      return {
        ...current,
        ...action.payload,
      };

    case "RESET_BOOKING":
      return initialState;

    default:
      return current;
  }
};

export default MyBookingReducer;
