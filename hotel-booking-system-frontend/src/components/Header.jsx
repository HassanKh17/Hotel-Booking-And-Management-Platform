import { useEffect, useState } from "react";
import { useLocation, useNavigate, Link } from "react-router-dom";
import { logout, getMe } from "../services/authService";
import "../styles/Header.css";
import { useBasket } from "./BasketContext";
import {
  BarChart3,
  Building2,
  CalendarDays,
  CreditCard,
  Hotel,
  LogIn,
  LogOut,
  Menu,
  Settings,
  ShoppingCart,
  User,
  X,
} from "lucide-react";

const PATH_CONFIG = {
  "/admin": { brand: "Hotel Management", subtitle: "Admin Portal" },
  "/hotel-owner": { brand: "Hotel Management", subtitle: "Owner Portal" },
  "/login": { brand: "Stay", subtitle: "Sign In" },
  "/login/admin": { brand: "Hotel Management", subtitle: "Admin Portal" },
  "/login/hotel-owner": { brand: "Hotel Management", subtitle: "Owner Portal" },
  "/login/customer": { brand: "Stay", subtitle: "" },
};

const ROLE_HOME = {
  ROLE_ADMIN: "/admin",
  ROLE_HOTEL_OWNER: "/hotel-owner/hotels",
  ROLE_CUSTOMER: "/hotels",
};

function Header() {
  const location = useLocation();
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState(null);
  const [menuOpen, setMenuOpen] = useState(false);
  const { basket } = useBasket();

  const match = Object.keys(PATH_CONFIG)
    .sort((a, b) => b.length - a.length)
    .find((path) => location.pathname.startsWith(path));
  const config = match ? PATH_CONFIG[match] : { brand: "Stay", subtitle: "" };

  useEffect(() => {
    getMe()
      .then((user) => {
        setIsLoggedIn(true);
        setUserRole(user.role);
      })
      .catch(() => {
        setIsLoggedIn(false);
        setUserRole(null);
      });
  }, [location.pathname]);

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  const handleLogout = async () => {
    const roleToPath = {
      ROLE_ADMIN: "/login/admin",
      ROLE_HOTEL_OWNER: "/login/hotel-owner",
      ROLE_CUSTOMER: "/hotels",
    };
    const redirectPath = roleToPath[userRole] ?? "/hotels";
    try {
      await logout();
    } finally {
      setIsLoggedIn(false);
      setUserRole(null);
      navigate(redirectPath);
    }
  };

  const navItems = (() => {
    if (userRole === "ROLE_ADMIN") {
      return [
        { to: "/admin", label: "Dashboard", icon: <Settings size={17} /> },
        { to: "/admin/global-charges", label: "Global Charges", icon: <CreditCard size={17} /> },
      ];
    }

    if (userRole === "ROLE_HOTEL_OWNER") {
      return [
        { to: "/hotel-owner/hotels", label: "Hotels", icon: <Building2 size={17} /> },
        {
          to: "/hotel-owner/occupancy-dashboard",
          label: "Occupancy",
          icon: <BarChart3 size={17} />,
        },
        { to: "/hotel-owner/statement", label: "Statement", icon: <CreditCard size={17} /> },
      ];
    }

    return [
      { to: "/hotels", label: "Hotels", icon: <Hotel size={17} /> },
      ...(userRole === "ROLE_CUSTOMER"
        ? [
            { to: "/bookings", label: "Bookings", icon: <CalendarDays size={17} /> },
            { to: "/account", label: "Account", icon: <User size={17} /> },
          ]
        : []),
    ];
  })();

  const isActive = (path) =>
    location.pathname === path ||
    (path !== "/hotels" && location.pathname.startsWith(`${path}/`));

  const homePath = ROLE_HOME[userRole] ?? "/hotels";
  const showBasket = !userRole || userRole === "ROLE_CUSTOMER";

  return (
    <header className="app-header">
      <div className="header-container">
        <Link to={homePath} className="header-brand" aria-label={`${config.brand} home`}>
          {config.brand}
        </Link>
        {config.subtitle && (
          <>
            <div className="header-divider" />
            <span className="header-subtitle">{config.subtitle}</span>
          </>
        )}

        <nav className={`header-nav ${menuOpen ? "is-open" : ""}`} aria-label="Primary navigation">
          {navItems.map((item) => (
            <Link
              key={item.to}
              to={item.to}
              className={`header-nav__link ${isActive(item.to) ? "active" : ""}`}
            >
              {item.icon}
              {item.label}
            </Link>
          ))}
        </nav>

        <div className="header-actions">
          {showBasket && (
            <Link
              to="/basket"
              className={`basket-btn ${location.pathname === "/basket" ? "active" : ""}`}
              aria-label={`Basket with ${basket.length} item${basket.length === 1 ? "" : "s"}`}
            >
              <ShoppingCart size={21} />
              <span className="basket-btn__label">Basket</span>
              {basket.length > 0 && <span className="basket-count">{basket.length}</span>}
            </Link>
          )}
          {!isLoggedIn && !location.pathname.startsWith("/login") && (
            <Link to="/login" className="login-btn">
              <LogIn size={17} /> Sign in
            </Link>
          )}
          {isLoggedIn && !location.pathname.startsWith("/login") && (
            <button className="logout-btn" onClick={handleLogout}>
              <LogOut size={17} /> Sign out
            </button>
          )}
          <button
            className="header-menu-btn"
            type="button"
            aria-expanded={menuOpen}
            aria-label={menuOpen ? "Close navigation menu" : "Open navigation menu"}
            onClick={() => setMenuOpen((open) => !open)}
          >
            {menuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>
    </header>
  );
}

export default Header;
