import { useEffect } from "react";
import { useState } from "react";
import "../styles/SlideWindow.css";
import banner_1 from "../assets/images/banner_1.jpg";
import banner_2 from "../assets/images/banner_2.jpg";
import banner_3 from "../assets/images/banner_3.jpg";
import banner_4 from "../assets/images/banner_4.jpg";
import banner_5 from "../assets/images/banner_5.jpg";

const images = [banner_1, banner_2, banner_3, banner_4, banner_5];

const SlideWindow = () => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      nextSlide();
    }, 5000);
    return () => clearInterval(interval);
  }, [currentIndex]);

  const nextSlide = () => {
    setCurrentIndex((prevIndex) => (prevIndex + 1) % images.length);
  };
  const prevSlide = () => {
    setCurrentIndex(
      (prevIndex) => (prevIndex - 1 + images.length) % images.length,
    );
  };

  return (
    <div className="banner-container">
      <div
        className="banner-slider"
        style={{ transform: `translateX(-${currentIndex * 100}%)` }}
      >
        {images.map((image, index) => (
          <div className="banner-item" key={index}>
            <img src={image} alt={`HotelSlide ${index + 1}`} />
            <div className="banner-content">
              <h1>SMART HOTEL LUXURY</h1>
              <p>Tận hưởng không gian sang trọng và đẳng cấp bậc nhất.</p>
              <button className="btn-book">Khám phá ngay</button>
            </div>
          </div>
        ))}
      </div>
      <button className="slider-btn prev" onClick={prevSlide}>
        &#10094;❮
      </button>
      <button className="slider-btn next" onClick={nextSlide}>
        ❯&#10095;
      </button>

      <div className="slider-dots">
        {images.map((_, index) => (
          <span
            key={index}
            className={`dot ${currentIndex === index ? "active" : ""}`}
            onClick={() => setCurrentIndex(index)}
          ></span>
        ))}
      </div>
    </div>
  );
};

export default SlideWindow;
