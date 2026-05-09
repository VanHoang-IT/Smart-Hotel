import React from "react";

const Location = () => {
  const mapSrc =
    "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d377.8731267118284!2d106.7050227347023!3d10.70927738856339!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752f8e29fee363%3A0xe8d070ac3a5f9204!2sTHAI%20SON%20HAUSE%20CO.%2C%20LTD!5e0!3m2!1svi!2s!4v1777290503407!5m2!1svi!2s";

  return (
    <div className="location-section mt-5 pt-5 border-top">
      <h3 className="playfair-title mb-4">Địa chỉ </h3>
      <div className="map-container shadow-sm">
        <iframe
          src={mapSrc}
          width="100%"
          height="450"
          style={{ border: 0 }}
          allowFullScreen=""
          loading="lazy"
          referrerPolicy="no-referrer-when-downgrade"
          title="Hotel Location"
        ></iframe>
      </div>
      <div className="address-info mt-3">
        <p className="text-muted">
          <i className="bi bi-geo-alt-fill me-2 color-gold"></i>
          1017 Lê Văn Lương, Huyện Nhà Bè, Thành phố Hồ Chí Minh, Việt Nam
        </p>
      </div>
    </div>
  );
};

export default Location;
