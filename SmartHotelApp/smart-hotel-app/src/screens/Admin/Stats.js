import { useState, useEffect } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import { Alert, Card, Col, Row, Table, Badge, Form } from "react-bootstrap";
import { Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
);

const Stats = () => {
  const [revenue, setRevenue] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedYear, setSelectedYear] = useState("");

  const loadMonthlyRevenue = async () => {
    try {
      let res = await authApis().get(endpoints["adminRevenueMonthly"]);
      setRevenue(res.data);
      if (res.data && res.data.length > 0) {
        setSelectedYear(res.data[0][1].toString());
      }
    } catch (ex) {
      console.error(ex);
      setError("Bạn không có quyền truy cập hoặc phiên làm việc đã hết hạn!");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMonthlyRevenue();
  }, []);

  if (loading) {
    return (
      <div className="text-center my-5 py-5">
        <MySpinner />
        <p className="mt-2 text-muted small">Đang tải báo cáo thống kê...</p>
      </div>
    );
  }

  if (error) {
    return (
      <Alert
        variant="danger"
        className="mt-4 text-center mx-auto shadow-sm"
        style={{ maxWidth: "600px" }}
      >
        <Alert.Heading className="fw-bold">
          🚨 TRUY CẬP BỊ TỪ CHỐI
        </Alert.Heading>
        <p className="my-3">{error}</p>
        <hr />
        <p className="mb-0 small text-muted">
          Vui lòng kiểm tra quyền tài khoản hoặc đăng nhập lại.
        </p>
      </Alert>
    );
  }

  const years = [...new Set(revenue.map((item) => item[1]))].sort(
    (a, b) => b - a,
  );
  const filteredRevenue = revenue.filter(
    (item) => item[1].toString() === selectedYear,
  );

  const sortedChartData = [...filteredRevenue].reverse();
  const chartLabels = sortedChartData.map((item) => `Tháng ${item[0]}`);
  const chartValues = sortedChartData.map((item) => item[2]);

  const chartDataConfig = {
    labels: chartLabels,
    datasets: [
      {
        label: "Doanh thu thực tế (VND)",
        data: chartValues,
        backgroundColor: "rgba(33, 37, 41, 0.8)",
        borderColor: "rgba(33, 37, 41, 1)",
        borderWidth: 1.5,
        borderRadius: 5,
        barThickness: 40,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            return Number(value).toLocaleString("vi-VN") + " đ";
          },
        },
      },
    },
    plugins: {
      legend: {
        position: "top",
      },
      tooltip: {
        callbacks: {
          label: function (context) {
            let label = context.dataset.label || "";
            if (label) label += ": ";
            if (context.parsed.y !== null) {
              label +=
                Number(context.parsed.y).toLocaleString("vi-VN") + " VND";
            }
            return label;
          },
        },
      },
    },
  };

  return (
    <>
      <Row className="mb-4 mt-2 align-items-center">
        <Col md={8}>
          <h2 className="text-uppercase fw-bold text-dark">
            Báo Cáo Thống Kê Doanh Thu
          </h2>
          <p className="text-muted small">
            Tổng hợp số tiền từ các hóa đơn theo từng tháng
          </p>
        </Col>
        <Col md={4}>
          <Form.Group>
            <Form.Label className="fw-bold text-secondary">
              Chọn năm thống kê:
            </Form.Label>
            <Form.Select
              value={selectedYear}
              onChange={(e) => setSelectedYear(e.target.value)}
            >
              {years.map((y) => (
                <option key={y} value={y}>
                  Năm {y}
                </option>
              ))}
            </Form.Select>
          </Form.Group>
        </Col>
      </Row>

      {revenue.length === 0 && (
        <Alert variant="info" className="mt-2">
          Hệ thống chưa ghi nhận dữ liệu thanh toán thành công nào!
        </Alert>
      )}

      {filteredRevenue.length === 0 && revenue.length > 0 && (
        <Alert variant="warning" className="mt-2">
          Không có dữ liệu thanh toán nào trong năm {selectedYear}!
        </Alert>
      )}

      {filteredRevenue.length > 0 && (
        <Row>
          <Col lg={8} md={12} className="p-2">
            <Card className="shadow-sm border-2 h-100">
              <Card.Body>
                <Card.Title className="fw-bold text-secondary mb-4">
                  Biểu đồ doanh thu tài chính năm {selectedYear}
                </Card.Title>
                <div style={{ height: "400px", position: "relative" }}>
                  <Bar data={chartDataConfig} options={chartOptions} />
                </div>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={4} md={12} className="p-2">
            <Card className="shadow-sm border-2 h-100">
              <Card.Body className="p-0">
                <div className="p-3">
                  <Card.Title className="fw-bold text-secondary mb-0">
                    Chi tiết số liệu năm {selectedYear}
                  </Card.Title>
                </div>
                <Table hover responsive align="middle" className="mb-0">
                  <thead className="table-dark">
                    <tr>
                      <th className="ps-3">Thời gian</th>
                      <th className="text-end pe-3">Doanh Thu</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredRevenue.map((item, index) => {
                      const month = item[0];
                      const amount = item[2];

                      return (
                        <tr key={index}>
                          <td className="ps-3 py-3">
                            <Badge bg="dark" className="px-2 py-2">
                              Tháng {month}
                            </Badge>
                          </td>
                          <td className="text-end pe-3 fw-bold text-success">
                            {Number(amount).toLocaleString("vi-VN")} VND
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </Table>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      )}
    </>
  );
};

export default Stats;
