import { BrowserRouter } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import "bootstrap/dist/css/bootstrap.min.css";
import SlideWindow from "./components/SlideWindow";

const App = () => {
  return (
    <BrowserRouter>
      <Header />
      <SlideWindow />
w
      <Footer />
    </BrowserRouter>
  );
};

export default App;
