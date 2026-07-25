import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle";
import "react-datepicker/dist/react-datepicker.css";

import { Route, Routes, Navigate, Outlet } from "react-router-dom";

import ProtectedRoute from "./components/ProtectedRoute.jsx";
import AuthWrapper from "./pages/AuthWrapper";
import LoginRolePage from "./pages/LoginRolePage";
import ForbiddenPage from "./pages/ForbiddenPage";
import AdminPage from "./pages/AdminPage";
import GlobalChargesPage from "./pages/GlobalChargesPage";
import Header from "./components/Header";

import HotelManagementPage from "./pages/HotelManagementPage";
import HotelsPage from "./pages/HotelsPage";
import HotelDetailPage from "./pages/HotelDetailPage";

import { BasketProvider } from "./components/BasketContext.jsx";
import BasketPage from "./pages/BasketPage";

import CheckoutPage from "./pages/CheckoutPage.jsx";
import ConfirmationPage from "./pages/ConfirmationPage.jsx";

import OwnerOverviewPage from "./pages/OwnerOverviewPage";
import AdminOwnerStatementPage from "./pages/AdminOwnerStatementPage";
import OwnerStatementPage from "./pages/OwnerStatementPage";
import OwnerOccupancyDashboard from "./pages/OwnerOccupanyDashboard";
import HotelDetailOwnerPage from "./pages/HotelDetailOwnerPage.jsx";
import CustomerBookingsPage from "./pages/CustomerBookingsPage.jsx";
import CustomerAccountPage from "./pages/CustomerAccountPage.jsx";


function Layout() {
  return (
    <>
      <Header />
      <Outlet />
    </>
  );
}

function App() {
  return (
    <div className="App">
      <BasketProvider>
        <Routes>
          <Route element={<Layout />}>

            {/* Public routes */}
            <Route path="/" element={<Navigate to="/hotels" replace />} />
            <Route path="/login" element={<LoginRolePage />} />
            <Route path="/login/:user" element={<AuthWrapper />} />
            <Route path="/basket" element={<BasketPage />} />
            <Route path="/hotels" element={<HotelsPage />} />
            <Route path="/hotels/:hotelId" element={<HotelDetailPage />} />
            <Route path="/403" element={<ForbiddenPage />} />

            <Route element={<ProtectedRoute role="ROLE_HOTEL_OWNER" />}>
              <Route path="/hotel-owner/hotels" element={<HotelManagementPage />} />
              <Route path="/hotel-owner/statement" element={<OwnerStatementPage />} />
              <Route path="/hotel-owner/hotels/:hotelId" element={<HotelDetailOwnerPage />} />
              <Route path="/hotel-owner/occupancy-dashboard" element={<OwnerOccupancyDashboard />} />
            </Route>

            <Route element={<ProtectedRoute role="ROLE_ADMIN" />}>
              <Route path="/admin" element={<AdminPage />} />
              <Route path="/admin/global-charges" element={<GlobalChargesPage />} />
              <Route path="/admin/owners/:ownerId/overview" element={<OwnerOverviewPage />} />
              <Route path="/admin/owners/:ownerId/statement" element={<AdminOwnerStatementPage />} />
            </Route>

            <Route element={<ProtectedRoute role="ROLE_CUSTOMER" />}>
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/confirmation" element={<ConfirmationPage />} />
              <Route path="/bookings" element={<CustomerBookingsPage />} />
              <Route path="/account" element={<CustomerAccountPage />} />
            </Route>

          </Route>
        </Routes>
      </BasketProvider>
    </div>
  );
}
export default App;
