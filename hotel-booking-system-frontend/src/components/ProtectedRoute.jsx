import { useEffect, useState } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { getMe } from "../services/authService";

const ROLE_REDIRECTS = {
    "ROLE_ADMIN": "/admin",
    "ROLE_HOTEL_OWNER": "/hotel-owner",
    "ROLE_CUSTOMER": "/hotels",
};

export default function ProtectedRoute({ role }) {
    const [status, setStatus] = useState("loading");
    const [roleRedirect, setRoleRedirect] = useState(null);
    const location = useLocation();

    useEffect(() => {
        getMe().then(user => {
            if (!user)
                setStatus("unauth");
            else if (user.role !== role) {
                setRoleRedirect(ROLE_REDIRECTS[user.role]);
                setStatus("forbidden");
            }
            else
                setStatus("ok");
        })
        .catch(() => setStatus("unauth"));
    }, [role]);

    if (status === "loading")
        return <div style={{ background: '#f4f6f8', minHeight: '100vh' }} />;
    if (status === "unauth") {
        return <Navigate to="/login/customer" state={{ from: location }}
            replace />;
    }
    if (status === "forbidden") {
        // 
        const redirect = roleRedirect ?? location;
        return <Navigate to="/403" state={{ from: redirect }} replace />;
    }
    return <Outlet />;
}