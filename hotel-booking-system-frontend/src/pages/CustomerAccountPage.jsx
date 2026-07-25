import { useEffect, useState } from "react";
import { CalendarDays, CreditCard, Hotel, User } from "lucide-react";
import { validateEmail, validatePassword } from "../utils/Input";
import { getMyDetails, updateMyDetails } from "../services/customerService";
import parseError from "../utils/parseError";
import "../styles/CustomerAccountPage.css";
import PaymentForm from "../components/PaymentForm";
import { getSavedCards } from "../services/cardService";
import {
  Alert,
  Button,
  Card,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";

function CustomerAccountPage() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [savedCards, setSavedCards] = useState([]);

  useEffect(() => {
    const loadDetails = async () => {
      setLoading(true);
      setError("");

      try {
        const data = await getMyDetails();
        setFormData((prev) => ({
          ...prev,
          username: data.username || "",
          email: data.email || "",
        }));
      } catch (err) {
        setError(parseError(err));
      } finally {
        setLoading(false);
      }
    };

    loadDetails();
  }, []);

  const loadCards = () => {
    getSavedCards()
      .then(setSavedCards)
      .catch(() => setSavedCards([]));
  };

  useEffect(() => {
    loadCards();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    setSaving(true);

    try {
      validateEmail(formData.email);

      if (formData.password.trim()) {
        if (formData.password !== formData.confirmPassword) {
          setError("Passwords do not match");
          setSaving(false);
          return;
        }

        validatePassword(formData.password);
      }

      const payload = {
        email: formData.email,
      };

      if (formData.password.trim()) {
        payload.password = formData.password;
      }

      await updateMyDetails(payload);

      setSuccess("Account details updated successfully.");
      setFormData((prev) => ({
        ...prev,
        password: "",
        confirmPassword: "",
      }));
    } catch (err) {
      setError(parseError(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <PageShell
      className="customer-account-page"
      eyebrow="Guest profile"
      title="My account"
      description="Keep contact details and saved payment methods up to date."
      actions={
        <>
          <Button to="/bookings" variant="secondary" icon={<CalendarDays size={17} />}>
            My bookings
          </Button>
          <Button to="/hotels" variant="secondary" icon={<Hotel size={17} />}>
            View hotels
          </Button>
        </>
      }
    >
      {loading ? (
        <LoadingState label="Loading account details..." />
      ) : (
        <div className="account-layout">
          <Card>
            <SectionHeader
              title="Profile details"
              description="Username is fixed, but you can update your email and password."
              actions={<User size={20} />}
            />

            {error && <Alert type="error">{error}</Alert>}
            {success && <Alert type="success">{success}</Alert>}

            <form onSubmit={handleSubmit} className="customer-account-form">
              <div className="account-field">
                <label htmlFor="username">Username</label>
                <input id="username" name="username" type="text" value={formData.username} readOnly disabled />
                <small>Username cannot be changed.</small>
              </div>

              <div className="account-field">
                <label htmlFor="email">Email address</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="ui-form-grid">
                <div className="account-field">
                  <label htmlFor="password">New password</label>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Leave blank to keep current password"
                  />
                </div>

                <div className="account-field">
                  <label htmlFor="confirmPassword">Confirm new password</label>
                  <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Repeat new password"
                  />
                </div>
              </div>

              <div className="account-actions">
                <Button as="button" type="submit" disabled={saving}>
                  {saving ? "Saving..." : "Save changes"}
                </Button>
              </div>
            </form>
          </Card>

          <Card>
            <SectionHeader
              title="Saved cards"
              description="Manage cards used for faster checkout."
              actions={<CreditCard size={20} />}
            />
            <PaymentForm savedCards={savedCards} onCardSaved={loadCards} manageMode />
          </Card>
        </div>
      )}
    </PageShell>
  );
}

export default CustomerAccountPage;
