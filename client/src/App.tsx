import { Route, Routes } from 'react-router-dom';
import ArrowBackToTop from './components/arrow-back-to-top/ArrowBackToTop';
import Footer from './components/footer/Footer';
import Navbar from './components/navbar/Navbar';
import Register from './pages/auth/register/Register';
import ContactUs from './pages/contact-us/ContactUs';
import Events from './pages/events/Events';
import Home from './pages/home/Home';
import NotFound from './pages/not-found/NotFound';
import './scss/styles.scss';
import { PageEnum } from './types/enums';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path={PageEnum.Home} element={<Home />} />
        <Route path={PageEnum.Contact} element={<ContactUs />} />
        <Route path={PageEnum.Events} element={<Events />} />
        <Route path={PageEnum.Register} element={<Register />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
      <Footer />

      <ArrowBackToTop />
    </>
  );
}

export default App;
