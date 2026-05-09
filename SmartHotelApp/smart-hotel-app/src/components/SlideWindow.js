import ImageCarousel from "./ImageCarousel";
import banner_1 from "../assets/images/banner_1.jpg";
import banner_2 from "../assets/images/banner_2.jpg";
import banner_3 from "../assets/images/banner_3.jpg";
import banner_4 from "../assets/images/banner_4.jpg";

function SlideWindow() {
  const banners = [banner_1, banner_2, banner_3, banner_4];
  return (
    <>
      <ImageCarousel images={banners} text="Smart Hotel" />
    </>
  );
}

export default SlideWindow;
