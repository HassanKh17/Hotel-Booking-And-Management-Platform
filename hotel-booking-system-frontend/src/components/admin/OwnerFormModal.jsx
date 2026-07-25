import { useEffect, useState } from "react";
import "../../styles/OwnerFormModal.css";
import {
    validateEmail,
    validateUsername,
    validatePassword,
} from "../../utils/Input";

function OwnerFormModal({ isOpen, mode, owner, onClose, onSubmit, loading }) {
    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
    });

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    useEffect(() => {
        setError("");
        setSuccess("");

        if (mode === "edit" && owner) {
            setFormData({
                username: owner.username || "",
                email: owner.email || "",
                password: "",
                confirmPassword: "",
            });
        } else {
            setFormData({
                username: "",
                email: "",
                password: "",
                confirmPassword: "",
            });
        }
    }, [mode, owner, isOpen]);

    if (!isOpen) return null;

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));

        if (error) setError("");
        if (success) setSuccess("");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        const { username, email, password, confirmPassword } = formData;

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        try {
            validateEmail(email);
            validateUsername(username);

            // In create mode, password is required.
            // In edit mode, only validate password if user entered one.
            if (mode === "create" || password.trim() !== "") {
                validatePassword(password);
            }
        } catch (err) {
            setError(err.message);
            return;
        }

        try {
            const payload =
                mode === "edit" && password.trim() === ""
                    ? { username, email }
                    : { username, email, password };

            await onSubmit(payload);

            setSuccess(
                mode === "create"
                    ? "Owner account created successfully."
                    : "Owner details updated successfully."
            );

            if (mode === "create") {
                setFormData({
                    username: "",
                    email: "",
                    password: "",
                    confirmPassword: "",
                });
            }
        } catch (err) {
            setError(err.message ?? "Operation failed. Please try again.");
        }
    };

    return (
        <div className="modal-backdrop">
            <div className="owner-modal">
                <div className="owner-modal__header">
                    <h2>{mode === "create" ? "Add Owner" : "Edit Owner"}</h2>
                    <button
                        type="button"
                        className="owner-modal__close"
                        onClick={onClose}
                        aria-label="Close modal"
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="owner-modal__form">
                    <div className="owner-modal__field">
                        <label htmlFor="username">Username</label>
                        <input
                            id="username"
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="owner-modal__field">
                        <label htmlFor="email">Email address</label>
                        <input
                            id="email"
                            type="email"
                            name="email"
                            placeholder="name@example.com"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="owner-modal__field">
                        <label htmlFor="password">
                            {mode === "create" ? "Password" : "New password"}
                        </label>
                        <input
                            id="password"
                            type="password"
                            name="password"
                            placeholder={
                                mode === "create"
                                    ? "Password"
                                    : "Leave blank to keep current password"
                            }
                            value={formData.password}
                            onChange={handleChange}
                            required={mode === "create"}
                        />
                    </div>

                    <div className="owner-modal__field">
                        <label htmlFor="confirmPassword">Confirm password</label>
                        <input
                            id="confirmPassword"
                            type="password"
                            name="confirmPassword"
                            placeholder={
                                mode === "create"
                                    ? "Repeat password"
                                    : "Repeat new password"
                            }
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required={mode === "create" || formData.password.trim() !== ""}
                        />
                    </div>

                    {error && (
                        <div className="owner-modal__error" role="alert">
                            {error}
                        </div>
                    )}

                    {success && (
                        <div className="owner-modal__success" role="status">
                            {success}
                        </div>
                    )}

                    <div className="owner-modal__actions">
                        <button
                            type="button"
                            className="owner-modal__btn owner-modal__btn--secondary"
                            onClick={onClose}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="owner-modal__btn owner-modal__btn--primary"
                            disabled={loading}
                        >
                            {loading
                                ? "Saving..."
                                : mode === "create"
                                    ? "Create account"
                                    : "Save changes"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default OwnerFormModal;