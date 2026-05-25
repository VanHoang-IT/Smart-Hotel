import { useEffect, useState } from "react";
import Apis from "../../configs/Apis";
import { endpoints } from "../../configs/Apis";
import { useParams } from "react-router-dom";
import { Alert } from "react-bootstrap";
import Amenities from "../../components/Amenity";
import Policy from "../../components/Policy";
import AvailabilityCalendar from "../../components/Calender";
import Location from "../../components/Location";
import ImageCarousel from "../../components/ImageCarousel";
import BookingBarSide from "../../components/BookingBarSide";
import RentingBarSide from "../../components/RentingBarSide";
import MySpinner from "../../components/MySpinner";

const RoomDetails = () => {
  const { id } = useParams();
  const [rooms, setRooms] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);
        const [roomRes, imgRes] = await Promise.all([
          Apis.get(endpoints.roomDetails(id)),
          Apis.get(endpoints.roomImages(id)),
        ]);
        setRooms(roomRes.data);
        setImages(imgRes.data.map(img => img.imageUrl));
      } catch (ex) {
        console.error(ex);
      } finally {
        setLoading(false);
      }
    };
    loadRooms();
  }, [id]);
  if (loading || !rooms) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "50vh" }}>
        <MySpinner />
      </div>
    );
  }
  return (
    <>
      {!rooms && (
        <Alert variant="info" className="mt-2">
          KHÔNG có phòng nào!
        </Alert>
      )}

      <ImageCarousel images={images} text="" />

      <div className="mt-5">
        <div className="container">
          <div className="row">
            <div className="col-lg-8">
              <div className="m-5">
                <h2 className="text-bold text-black m-3 fs-1">{rooms.name}</h2>

                <p className="fs-5">{rooms.note}</p>
              </div>

              <div className="border-bottom my-3"></div>

              <div className="m-5">
                <Amenities />
              </div>

              <div className="border-bottom my-3"></div>

              <div className="m-5">
                <Policy />
              </div>

              <div className="m-5">
                <AvailabilityCalendar roomId={id} />
              </div>

              <div className="m-5">
                <Location />
              </div>
            </div>
            <div className="col-lg-4">
              <RentingBarSide roomPrice={rooms.price} roomId={rooms.id} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
export default RoomDetails;
