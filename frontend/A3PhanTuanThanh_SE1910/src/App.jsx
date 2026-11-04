// Author: Phan Tuan Thanh - CE190595
import {
  BrowserRouter,
  Link,
  Navigate,
  NavLink,
  Route,
  Routes,
  useLocation,
} from 'react-router-dom';
import { AuthProvider, useAuth } from './AuthContext.jsx';
import RoomsPage from './pages/RoomsPage.jsx';
import { LoginPage, RegisterPage } from './pages/AuthPages.jsx';
import StaffCustomers from './pages/StaffCustomers.jsx';
import StaffRooms from './pages/StaffRooms.jsx';
import StaffBookings from './pages/StaffBookings.jsx';
import ProfilePage from './pages/ProfilePage.jsx';
import CustomerBooking from './pages/CustomerBooking.jsx';
import HistoryPage from './pages/HistoryPage.jsx';
function Protected({ role, children }) {
  const { session } = useAuth();
  const location = useLocation();
  if (!session)
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location.pathname, message: 'Please sign in to continue.' }}
      />
    );
  if (session.role !== role)
    return (
      <section>
        <h1>Access restricted</h1>
        <p>You do not have permission to view this page.</p>
        <Link to="/">Back to rooms</Link>
      </section>
    );
  return children;
}
function Layout() {
  const { session, logout } = useAuth();
  return (
    <>
      <nav aria-label="Main navigation">
        <Link className="brand" to="/">
          FU Mini Hotel
        </Link>
        <div className="nav-links">
          <NavLink to="/">Rooms</NavLink>
          {session?.role === 'STAFF' && (
            <>
              <NavLink to="/staff/customers">Customers</NavLink>
              <NavLink to="/staff/rooms">Manage rooms</NavLink>
              <NavLink to="/staff/bookings">Bookings</NavLink>
            </>
          )}
          {session?.role === 'CUSTOMER' && (
            <>
              <NavLink to="/profile">Profile</NavLink>
              <NavLink to="/book">Book rooms</NavLink>
              <NavLink to="/history">History</NavLink>
            </>
          )}
          {session ? (
            <>
              <span>{session.name}</span>
              <button className="secondary" onClick={logout}>
                Sign out
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login">Sign in</NavLink>
              <NavLink to="/register">Register</NavLink>
            </>
          )}
        </div>
      </nav>
      <main>
        <Routes>
          <Route path="/" element={<RoomsPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/staff/customers"
            element={
              <Protected role="STAFF">
                <StaffCustomers />
              </Protected>
            }
          />
          <Route
            path="/staff/rooms"
            element={
              <Protected role="STAFF">
                <StaffRooms />
              </Protected>
            }
          />
          <Route
            path="/staff/bookings"
            element={
              <Protected role="STAFF">
                <StaffBookings />
              </Protected>
            }
          />
          <Route
            path="/profile"
            element={
              <Protected role="CUSTOMER">
                <ProfilePage />
              </Protected>
            }
          />
          <Route
            path="/book"
            element={
              <Protected role="CUSTOMER">
                <CustomerBooking />
              </Protected>
            }
          />
          <Route
            path="/history"
            element={
              <Protected role="CUSTOMER">
                <HistoryPage />
              </Protected>
            }
          />
          <Route
            path="*"
            element={
              <section>
                <h1>Page not found</h1>
                <Link to="/">Back to rooms</Link>
              </section>
            }
          />
        </Routes>
        <footer>FUMiniHotelSystem / A comfortable stay starts here.</footer>
      </main>
    </>
  );
}
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Layout />
      </AuthProvider>
    </BrowserRouter>
  );
}
