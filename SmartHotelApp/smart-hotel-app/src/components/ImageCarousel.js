import Carousel from "react-bootstrap/Carousel";

const ImageCarousel = ({ images = [], text }) => {
  if (!images.length) return null;

  return (
    <div className="w-full h-300px overflow-hidden mt-10">
      <Carousel className="h-full">
        {images.map((img, index) => (
          <Carousel.Item
            key={index}
            interval={1000}
            className="h-full text-center"
          >
            <img
              src={img}
              alt={`img-${index}`}
              className="w-full h-full object-cover"
            />
            <Carousel.Caption>
              <h1 className="font-bold text-black">{text}</h1>
            </Carousel.Caption>
          </Carousel.Item>
        ))}
      </Carousel>
    </div>
  );
};

export default ImageCarousel;
