import React from "react";

const policiesList = [
  {
    title: "Quy định lưu trú",
    description: `- Cấm hút thuốc trong phòng.
                  - Check-in: 14:00 - 22:00.
                  - Check-out: trước 12:00.
                  - Không mang thú cưng.
                  - Giữ yên tĩnh sau 22:00.`,
  },
  {
    title: "Quy định hủy phòng",
    description: `- Khách hàng có thể hủy phòng miễn phí nếu hủy trước 48 giờ so với ngày nhận phòng.
      - Nếu hủy trong vòng 48 giờ, sẽ bị tính phí 50% tổng giá trị đặt phòng.`,
  },
];

const Policy = () => {
  return (
    <div className="m-2 ">
      {policiesList.map((item, index) => (
        <div key={index} className="mb-4">
          <h4 className="fs-4">{item.title}</h4>
          <p className="fs-5" style={{ whiteSpace: "pre-line" }}>
            {item.description}
          </p>
          <div className="border-bottom my-3"></div>
        </div>
      ))}
    </div>
  );
};

export default Policy;
