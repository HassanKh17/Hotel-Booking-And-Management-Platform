import { useState, useEffect } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { getMe } from "../services/authService";
import NotFoundPage from "./NotFoundPage";
import LoginForm from "../components/LoginForm";
import RegisterForm from "../components/RegisterForm";
import "../styles/AuthPage.css";

// Defines which user types are valid, and whether they can register
const AUTH_CONFIG = {
    "admin": { canRegister: false, label: "Admin", defaultRedirect: "/admin" },
    "hotel-owner": { canRegister: false, label: "Hotel Owner",
        defaultRedirect: "/hotel-owner/hotels" },
    "customer": { canRegister: true, label: "Customer",
        defaultRedirect: "/hotels" },
};

const AuthWrapper = () => {
    const [mode, setMode] = useState("login");
    const { user } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const [checking, setChecking] = useState(true);

    useEffect(() => {
        getMe().then(loggedInUser => {
            if (!loggedInUser) { setChecking(false); return; }
            const roleMap = {
                "admin": "ROLE_ADMIN",
                "hotel-owner": "ROLE_HOTEL_OWNER",
                "customer": "ROLE_CUSTOMER",
            };
            if (loggedInUser.role === roleMap[user]) {
                // Navigate back if possible, otherwise to a default page
                const from = location.state?.from?.pathname;
                const fallback = AUTH_CONFIG[user]?.defaultRedirect ?? "/";
                navigate(from ?? fallback, { replace: true });
            } else {
                setChecking(false);
            }
        }).catch(() => setChecking(false));
    }, [navigate, user, location]);

    if (checking) return <div style={{ background: '#f4f6f8',
        minHeight: '100vh' }} />;

    const config = AUTH_CONFIG[user];
    if (!config)
        return <NotFoundPage />;

    const { canRegister, label } = config;

    return (
        <div className="auth-wrapper">
            <main className="auth-card">
                {canRegister && (
                    <div className="auth-tabs">
                        <button
                            className={`auth-tab ${mode === "login" ?
                                "active" : ""}`}
                            onClick={() => setMode("login")}
                            type="button"
                        >
                            Sign in
                        </button>
                        <button
                            className={`auth-tab ${mode === "register" ?
                                "active" : ""}`}
                            onClick={() => setMode("register")}
                            type="button"
                        >
                            Register
                        </button>
                    </div>
                )}

                {mode === "login" || !canRegister ? (
                    <LoginForm
                        userLabel={label}
                        onSwitchToRegister={canRegister ? () =>
                            setMode("register") : null}
                    />
                ) : (
                    <RegisterForm onSwitchToLogin={() => setMode("login")}
                        user={user} />
                )}
                <p className="auth-footer">&copy; {new Date().getFullYear()}</p>
            </main>
        </div>
    );
};

export default AuthWrapper;
