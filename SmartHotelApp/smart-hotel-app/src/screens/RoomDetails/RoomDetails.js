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

const RoomDetails = () => {
  const { id } = useParams();
  const [rooms, setRooms] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRooms = async () => {
      try {
        setLoading(true);
        let res = await Apis.get(endpoints.roomDetails(id));
        setRooms(res.data);
      } catch (ex) {
      } finally {
        setLoading(false);
      }
    };
    loadRooms();
  }, [id]);

  const images = rooms?.roomImagesSet?.map((img) => img.imageUrl) || [];
  if (loading || !rooms) {
    return <p>Loading...</p>;
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
                <AvailabilityCalendar />
              </div>

              <div className="m-5">
                <Location />
              </div>
            </div>
            <div className="col-lg-4">
              <RentingBarSide room={rooms} />
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
export default RoomDetails;
