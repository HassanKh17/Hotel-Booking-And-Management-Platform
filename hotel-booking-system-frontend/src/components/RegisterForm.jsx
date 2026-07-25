import { useState } from "react";
import { PropTypes } from 'prop-types'

import { validateEmail, validateUsername, validatePassword } from "../utils/Input";
import { register as registerCustomer } from "../services/customerService";

const RegisterForm = ({ onSwitchToLogin, user }) => {
    const [email, setEmail] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }
        try {
            validateEmail(email);
            validateUsername(username);
            validatePassword(password);
        } catch (err) {
            setError(err.message);
            return;
        }
        // Determine registration call based on user type
        const register = user === 'customer' ? registerCustomer : null;
        if (register === null) {
            setError("Unsupported user type for registration");
            return;
        }
        try {
            await register(username, email, password);
            setSuccess("Account created! Swapping to sign in...");
            setTimeout(() => onSwitchToLogin(), 1500);
        } catch (err) {
            setError(err.message ?? "Registration failed. Please try again.");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="auth-form">
            <div className="auth-field">
                <label htmlFor="registerEmail">Email address</label>
                <input
                    type="email"
                    id="registerEmail"
                    name="email"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
            </div>

            <div className="auth-field">
                <label htmlFor="registerUsername">Username</label>
                <input
                    type="text"
                    id="registerUsername"
                    name="username"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
            </div>

            <div className="auth-field">
                <label htmlFor="registerPassword">Password</label>
                <input
                    type="password"
                    id="registerPassword"
                    name="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
            </div>

            <div className="auth-field">
                <label htmlFor="registerConfirmPassword">Confirm password</label>
                <input
                    type="password"
                    id="registerConfirmPassword"
                    name="confirmPassword"
                    placeholder="Repeat password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                />
            </div>

            {error && (
                <div className="auth-error" role="alert">
                    {error}
                </div>
            )}

            {success && (
                <div className="auth-success" role="status">
                    {success}
                </div>
            )}

            <button className="auth-submit" type="submit">
                Create account
            </button>

            <p className="auth-switch">
                Already have an account?{" "}
                <button type="button" className="auth-link" onClick={onSwitchToLogin}>
                    Sign in
                </button>
            </p>
        </form>
    );
};

export default RegisterForm;

RegisterForm.propTypes = {
    onSwitchToLogin: PropTypes.func.isRequired,
    user: PropTypes.string,
};
