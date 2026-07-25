import { useEffect, useState } from "react";
import { Save } from "lucide-react";
import { getCurrentGlobalCharges, updateGlobalCharges } from "../services/globalChargesService";
import MessageAlert from "../components/admin/MessageAlert";
import parseError from "../utils/parseError";
import { BackButton, Button, Card, LoadingState, PageShell, SectionHeader } from "../components/ui";
import "../styles/AdminPage.css";

function GlobalChargesPage() {
  const [formData, setFormData] = useState({
    monthlyBasePrice: "",
    perRoomPrice: "",
    transactionFeePct: "",
    effectiveFrom: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [pageLoading, setPageLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [currentEffectiveDate, setCurrentEffectiveDate] = useState("");

  useEffect(() => {
    const loadCharges = async () => {
      try {
        setPageLoading(true);
        setError("");
        const data = await getCurrentGlobalCharges();

        setFormData({
          monthlyBasePrice: data.monthlyBasePrice ?? "",
          perRoomPrice: data.perRoomPrice ?? "",
          transactionFeePct: data.transactionFeePct ?? "",
          effectiveFrom: data.effectiveFrom ?? "",
        });

        setCurrentEffectiveDate(data.effectiveFrom ?? "");
      } catch (err) {
        setError(parseError(err));
      } finally {
        setPageLoading(false);
      }
    };

    loadCharges();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    setFieldErrors((prev) => ({
      ...prev,
      [name]: "",
    }));
  };

  const validateForm = () => {
    const errors = {};

    if (!formData.monthlyBasePrice) {
      errors.monthlyBasePrice = "Monthly base price is required.";
    } else if (Number.isNaN(Number(formData.monthlyBasePrice))) {
      errors.monthlyBasePrice = "Monthly base price must be a valid number.";
    } else if (Number(formData.monthlyBasePrice) < 0) {
      errors.monthlyBasePrice = "Monthly base price cannot be negative.";
    }

    if (!formData.perRoomPrice) {
      errors.perRoomPrice = "Per room price is required.";
    } else if (Number.isNaN(Number(formData.perRoomPrice))) {
      errors.perRoomPrice = "Per room price must be a valid number.";
    } else if (Number(formData.perRoomPrice) < 0) {
      errors.perRoomPrice = "Per room price cannot be negative.";
    }

    if (!formData.transactionFeePct) {
      errors.transactionFeePct = "Transaction fee percentage is required.";
    } else if (Number.isNaN(Number(formData.transactionFeePct))) {
      errors.transactionFeePct = "Transaction fee percentage must be a valid number.";
    } else if (Number(formData.transactionFeePct) < 0) {
      errors.transactionFeePct = "Transaction fee percentage cannot be negative.";
    } else if (Number(formData.transactionFeePct) > 100) {
      errors.transactionFeePct = "Transaction fee percentage cannot exceed 100.";
    }

    if (!formData.effectiveFrom) {
      errors.effectiveFrom = "Effective date is required.";
    } else {
      const selectedDate = new Date(formData.effectiveFrom);
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      if (selectedDate < today) {
        errors.effectiveFrom = "Effective date cannot be in the past.";
      } else if (formData.effectiveFrom === currentEffectiveDate) {
        errors.effectiveFrom = "A global charge record already exists for this effective date.";
      }
    }

    return errors;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    const validationErrors = validateForm();

    if (Object.keys(validationErrors).length > 0) {
      setFieldErrors(validationErrors);
      return;
    }

    setFieldErrors({});

    try {
      setSaving(true);

      await updateGlobalCharges({
        monthlyBasePrice: Number(formData.monthlyBasePrice),
        perRoomPrice: Number(formData.perRoomPrice),
        transactionFeePct: Number(formData.transactionFeePct),
        effectiveFrom: formData.effectiveFrom,
      });

      setSuccess("Global charges updated successfully.");
      setCurrentEffectiveDate(formData.effectiveFrom);
    } catch (err) {
      setError(parseError(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <PageShell
      className="admin-page"
      eyebrow="Admin settings"
      title="Global charges"
      description="Update pricing rules for hotel owners from one controlled screen."
      toolbar={<BackButton to="/admin" label="Back to dashboard" />}
      narrow
    >
      <Card>
        <SectionHeader title="Pricing rules" description="Changes apply from the effective date you choose." />

        <MessageAlert type="error" message={error} />
        <MessageAlert type="success" message={success} />

        {pageLoading ? (
          <LoadingState label="Loading charges..." />
        ) : (
          <form onSubmit={handleSubmit} className="charges-form-modern" noValidate>
            <div className="ui-form-grid">
              <div className="charges-field">
                <label htmlFor="monthlyBasePrice">Monthly base price</label>
                <input
                  id="monthlyBasePrice"
                  name="monthlyBasePrice"
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.monthlyBasePrice}
                  onChange={handleChange}
                  placeholder="e.g. 100.00"
                  required
                />
                {fieldErrors.monthlyBasePrice && (
                  <span className="field-error">{fieldErrors.monthlyBasePrice}</span>
                )}
              </div>

              <div className="charges-field">
                <label htmlFor="perRoomPrice">Per room price</label>
                <input
                  id="perRoomPrice"
                  name="perRoomPrice"
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.perRoomPrice}
                  onChange={handleChange}
                  placeholder="e.g. 10.00"
                  required
                />
                {fieldErrors.perRoomPrice && <span className="field-error">{fieldErrors.perRoomPrice}</span>}
              </div>

              <div className="charges-field">
                <label htmlFor="transactionFeePct">Transaction fee %</label>
                <input
                  id="transactionFeePct"
                  name="transactionFeePct"
                  type="number"
                  step="0.01"
                  min="0"
                  max="100"
                  value={formData.transactionFeePct}
                  onChange={handleChange}
                  placeholder="e.g. 5.00"
                  required
                />
                {fieldErrors.transactionFeePct && (
                  <span className="field-error">{fieldErrors.transactionFeePct}</span>
                )}
              </div>

              <div className="charges-field">
                <label htmlFor="effectiveFrom">Effective from</label>
                <input
                  id="effectiveFrom"
                  name="effectiveFrom"
                  type="date"
                  value={formData.effectiveFrom}
                  onChange={handleChange}
                  required
                />
                {fieldErrors.effectiveFrom && <span className="field-error">{fieldErrors.effectiveFrom}</span>}
              </div>
            </div>

            <Button as="button" type="submit" icon={<Save size={17} />} disabled={saving}>
              {saving ? "Saving..." : "Save charges"}
            </Button>
          </form>
        )}
      </Card>
    </PageShell>
  );
}

export default GlobalChargesPage;
