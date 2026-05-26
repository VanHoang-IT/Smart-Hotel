import { useSelector } from "react-redux";

const Checkout = () => {
  const booking = useSelector((state) => state.booking);

  const getNights = () => {
    const checkIn = new Date(booking.checkIn);
    const checkOut = new Date(booking.checkOut);
    const diff = checkOut - checkIn;
    return diff / (1000 * 60 * 60 * 24);
  };

  const nights = getNights();

  const pricePerNight = 50;
  const totalPrice = nights > 0 ? nights * pricePerNight : 0;

  return (
    <div className="container mt-4">
      <h2>Checkout</h2>

      <div className="row mt-3">
        <div className="col-md-7">
          <div className="card p-3">
            <h5>Booking Information</h5>

            <p>
              <b>Check-in:</b> {booking.checkIn}
            </p>

            <p>
              <b>Check-out:</b> {booking.checkOut}
            </p>

            <p>
              <b>Nights:</b> {nights}
            </p>

            <p>
              <b>Adults:</b> {booking.adults}
            </p>

            <p>
              <b>Children:</b> {booking.children}
            </p>
          </div>

          <div className="card p-3 mt-3">
            <h5>Guest Details</h5>

            <input className="form-control mb-2" placeholder="Full name" />
            <input className="form-control mb-2" placeholder="Email" />
            <input className="form-control mb-2" placeholder="Phone" />
          </div>
        </div>

        <div className="col-md-5">
          <div className="card p-3">
            <h5>Price Summary</h5>

            <p>Price per night: ${pricePerNight}</p>
            <p>Nights: {nights}</p>

            <hr />

            <h5>Total: ${totalPrice}</h5>

            <button className="btn btn-primary w-100 mt-3">
              Proceed to Payment
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
