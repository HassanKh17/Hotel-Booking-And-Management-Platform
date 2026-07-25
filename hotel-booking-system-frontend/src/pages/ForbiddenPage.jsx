import { useNavigate, useLocation } from "react-router-dom";

import '../styles/AuthPage.css';

export default function ForbiddenPage() {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <div className="auth-wrapper">
            <div className="auth-card" style={{ textAlign: 'center' }}>
                <h1 style={{ fontSize: '4rem', marginBottom: '8px' }}>403</h1>
                <p style={{ color: '#6b7280', marginBottom: '24px' }}>
                    You don't have permission to view this page.<br />
                    If you think this is a mistake, please contact your administrator.
                </p>
                <button className="auth-submit" onClick={() => {
                    // Navigate back if possible, otherwise to a default page
                    navigate(location.state?.from ?? "/", { replace: true });
                }}>
                    Go To Safety
                </button>
            </div>
        </div>
    );
}
