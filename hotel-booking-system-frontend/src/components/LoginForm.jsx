import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import PropTypes from "prop-types";

import { validateUsername, validatePassword } from "../utils/Input";
import { login } from "../services/authService";

const ROLE_REDIRECTS = {
  ROLE_ADMIN: "/admin",
  ROLE_HOTEL_OWNER: "/hotel-owner/hotels",
  ROLE_CUSTOMER: "/hotels",
};

const LoginForm = ({ userLabel, onSwitchToRegister = null }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      validateUsername(username);
      validatePassword(password);
    } catch (err) {
      setError(err.message);
      return;
    }

    try {
      const user = await login(username, password);

      const from = location.state?.from?.pathname;
      const fallback = ROLE_REDIRECTS[user.role] ?? "/";

      navigate(from ?? fallback, { replace: true });

    } catch {
      setError("Invalid username or password");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="auth-form">
      <h1 className="auth-heading">{userLabel} Sign In</h1>

      <div className="auth-field">
        <label htmlFor="loginUsername">Username</label>
        <input
          type="text"
          id="loginUsername"
          name="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
      </div>

      <div className="auth-field">
        <label htmlFor="loginPassword">Password</label>
        <input
          type="password"
          id="loginPassword"
          name="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>

      {error && (
        <div className="auth-error" role="alert">
          {error}
        </div>
      )}

      <button className="auth-submit" type="submit">
        Sign in
      </button>

      {onSwitchToRegister && (
        <p className="auth-switch">
          Don't have an account?{" "}
          <button
            type="button"
            className="auth-link"
            onClick={onSwitchToRegister}
          >
            Register
          </button>
        </p>
      )}
    </form>
  );
};

export default LoginForm;

LoginForm.propTypes = {
  userLabel: PropTypes.string.isRequired,
  onSwitchToRegister: PropTypes.func,
};