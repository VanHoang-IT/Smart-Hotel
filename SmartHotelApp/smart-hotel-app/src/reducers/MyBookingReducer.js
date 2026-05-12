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

  rooms: [],

  services: [],

  roomTotal: 0,

  serviceTotal: 0,

  totalAmount: 0,
};

const calculateTotals = (state) => {
  const checkIn = new Date(state.checkIn);
  const checkOut = new Date(state.checkOut);

  const nights = Math.max(1, (checkOut - checkIn) / (1000 * 60 * 60 * 24));

  const roomTotal = state.rooms.reduce((sum, r) => {
    return sum + r.price * nights;
  }, 0);

  const serviceTotal = state.serviceTotal || 0;

  return {
    ...state,

    roomTotal,

    serviceTotal,

    totalAmount: roomTotal + serviceTotal,
  };
};

const MyBookingReducer = (current = initialState, action) => {
  switch (action.type) {
    case "UPDATE_BOOKING":
      return calculateTotals({
        ...current,
        ...action.payload,
      });

    case "ADD_ROOM":
      const exists = current.rooms.find((r) => r.id === action.payload.id);

      if (exists) return current;

      return calculateTotals({
        ...current,
        rooms: [...current.rooms, action.payload],
      });

    case "REMOVE_ROOM":
      return calculateTotals({
        ...current,
        rooms: current.rooms.filter((r) => r.id !== action.payload),
      });

    case "ADD_SERVICE":
      return {
        ...current,
        services: [...current.services, action.payload],
      };

    case "REMOVE_SERVICE":
      return {
        ...current,
        services: current.services.filter(
          (s) => s.serviceId !== action.payload,
        ),
      };

    case "SET_SERVICE_TOTAL":
      return calculateTotals({
        ...current,
        serviceTotal: action.payload,
      });

    case "RESET_BOOKING":
      return initialState;

    default:
      return current;
  }
};

export default MyBookingReducer;
