import { useNavigate } from "react-router-dom";
import '../styles/AuthPage.css';

export default function NotFoundPage() {
    const navigate = useNavigate();

    const handleGoBack = () => navigate(-1);

    return (
        <div className="auth-wrapper">
            <div className="auth-card auth-card--centered">
                <h1 className="auth-error-code">404</h1>
                <p className="auth-error-message">
                    The page you&apos;re looking for doesn&apos;t exist.
                </p>
                <button className="auth-submit" onClick={handleGoBack}>
                    Go Back
                </button>
            </div>
        </div>
    );
}
