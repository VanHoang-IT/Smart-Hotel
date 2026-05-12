import { useContext } from "react";
import { Alert, Button, Card, Form, Table } from "react-bootstrap";
import { MyBookingContext, MyUserContext } from "../../configs/Contexts";

const Checkout = () => {
  const [booking] = useContext(MyBookingContext);

  const [user] = useContext(MyUserContext);

  if (!booking) {
    return (
      <Alert variant="warning" className="mt-3">
        Không có dữ liệu đặt phòng!
      </Alert>
    );
  }

  const getNights = () => {
    const checkIn = new Date(booking.checkIn);

    const checkOut = new Date(booking.checkOut);

    return (checkOut - checkIn) / 86400000;
  };

  const nights = getNights();

  return (
    <>
      <h1 className="text-center text-success mt-3">CHECKOUT</h1>

      <Card className="p-3 mt-3" lg={8}>
        <h4 className="mb-3">Thông tin đặt phòng</h4>

        <Table striped bordered hover>
          <tbody>
            <tr>
              <th>Check-in</th>
              <td>{booking.checkIn}</td>
            </tr>

            <tr>
              <th>Check-out</th>
              <td>{booking.checkOut}</td>
            </tr>

            <tr>
              <th>Số đêm</th>
              <td>{nights}</td>
            </tr>

            <tr>
              <th>Người lớn</th>
              <td>{booking.adults}</td>
            </tr>

            <tr>
              <th>Trẻ em</th>
              <td>{booking.children}</td>
            </tr>

            <tr>
              <th>Tiền phòng</th>
              <td>${booking.roomTotal}</td>
            </tr>

            <tr>
              <th>Tiền dịch vụ</th>
              <td>${booking.serviceTotal}</td>
            </tr>

            <tr>
              <th>Tổng tiền</th>
              <td className="text-danger fw-bold">${booking.totalAmount}</td>
            </tr>
          </tbody>
        </Table>
      </Card>

      <Card className="p-3 mt-3">
        <h4 className="mb-3">Thông tin khách hàng</h4>

        <Form>
          <Form.Group className="mb-3 d-flex align-items-center">
            <Form.Label style={{ width: "80px" }}>Họ tên</Form.Label>

            <Form.Control type="text" value={user?.fullName || ""} readOnly />
          </Form.Group>

          <Form.Group className="mb-3 d-flex align-items-center">
            <Form.Label style={{ width: "80px" }}>Email</Form.Label>

            <Form.Control type="email" value={user?.email || ""} readOnly />
          </Form.Group>

          <Form.Group className="mb-3 d-flex align-items-center">
            <Form.Label style={{ width: "80px" }}>Số điện thoại</Form.Label>

            <Form.Control type="text" value={user?.phone || ""} readOnly />
          </Form.Group>
        </Form>
      </Card>

      <div className="d-flex m-3 text-center justify-content-center">
        <Button variant="success" size="lg">
          Thanh toán
        </Button>
      </div>
    </>
  );
};

export default Checkout;
