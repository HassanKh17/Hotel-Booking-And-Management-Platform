import { useEffect, useState } from "react";
import { CreditCard, Lock, Check, Trash2 } from "lucide-react";
import PropTypes from "prop-types";
import { saveCard, deleteCard } from "../services/cardService";
import { Alert, Button, ConfirmDialog } from "./ui";

import {
  validateCardholderName,
  validateCardNumber,
  validateCardExpiry,
  validateCVV,
  validateStreet,
  validateCity,
  validateUkPostcode,
} from "../utils/Input.js";

const emptyFields = {
  cardholderName: "",
  cardNumber: "",
  expiry: "",
  cvv: "",
  street: "",
  city: "",
  postcode: "",
};

const formatCardNumber = (value) =>
  value
    .replaceAll(/\D/g, "")
    .slice(0, 16)
    .replaceAll(/(.{4})/g, "$1 ")
    .trim();

const formatExpiry = (value) => {
  const digits = value.replaceAll(/\D/g, "").slice(0, 4);
  if (digits.length >= 3) return `${digits.slice(0, 2)}/${digits.slice(2)}`;
  return digits;
};

const Field = ({ id, label, error, children }) => (
  <div className="ui-field">
    <label htmlFor={id}>{label}</label>
    {children}
    {error && (
      <span className="field-error" role="alert">
        {error}
      </span>
    )}
  </div>
);

Field.propTypes = {
  id: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  error: PropTypes.string,
  children: PropTypes.node.isRequired,
};

const SavedCardPill = ({ card, selected, onSelect, onRemove, removable, removeLoading }) => {
  const content = (
    <>
      <div className="card-info">
        <CreditCard size={20} className="card-icon" />
        <div>
          <p className="card-name">{card.cardholderName}</p>
          <p className="card-details">Card ending {card.lastFour} | Exp: {card.expiryDate}</p>
        </div>
      </div>

      {removable ? (
        <Button
          variant="danger"
          size="sm"
          icon={<Trash2 size={14} />}
          onClick={onRemove}
          disabled={removeLoading}
        >
          {removeLoading ? "Removing..." : "Remove"}
        </Button>
      ) : (
        <div className="radio-indicator" aria-hidden="true" />
      )}
    </>
  );

  if (removable) {
    return <div className="saved-card-pill saved-card-pill--managed">{content}</div>;
  }

  return (
    <button
      type="button"
      onClick={onSelect}
      className={`saved-card-pill ${selected ? "selected" : ""}`}
      aria-pressed={selected}
    >
      {content}
    </button>
  );
};

SavedCardPill.propTypes = {
  card: PropTypes.shape({
    savedCardId: PropTypes.number.isRequired,
    cardholderName: PropTypes.string.isRequired,
    lastFour: PropTypes.string.isRequired,
    expiryDate: PropTypes.string.isRequired,
  }).isRequired,
  selected: PropTypes.bool.isRequired,
  onSelect: PropTypes.func.isRequired,
  onRemove: PropTypes.func,
  removable: PropTypes.bool,
  removeLoading: PropTypes.bool,
};

const PaymentForm = ({
  onSubmit,
  loading = false,
  savedCards = [],
  onCardSaved,
  manageMode = false,
}) => {
  const [useSaved, setUseSaved] = useState(savedCards.length > 0);
  const [selectedCardIndex, setSelectedCardIndex] = useState(0);
  const [removingId, setRemovingId] = useState(null);
  const [cardToRemove, setCardToRemove] = useState(null);
  const [fields, setFields] = useState(emptyFields);
  const [errors, setErrors] = useState({});
  const [saveLoading, setSaveLoading] = useState(false);
  const [isSaved, setIsSaved] = useState(false);

  useEffect(() => {
    if (savedCards.length === 0) {
      setUseSaved(false);
      setSelectedCardIndex(0);
      return;
    }

    if (selectedCardIndex >= savedCards.length) {
      setSelectedCardIndex(0);
    }

    const hasTypedNewCard = Object.values(fields).some(Boolean);
    if (!manageMode && !hasTypedNewCard) {
      setUseSaved(true);
    }
  }, [fields, manageMode, savedCards.length, selectedCardIndex]);

  const handleChange = (field, formatter) => (event) => {
    const value = formatter ? formatter(event.target.value) : event.target.value;
    setFields((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: "", form: "" }));
  };

  const validateFields = () => {
    const newErrors = {};
    try {
      validateCardholderName(fields.cardholderName);
    } catch (err) {
      newErrors.cardholderName = err.message;
    }
    try {
      validateCardNumber(fields.cardNumber);
    } catch (err) {
      newErrors.cardNumber = err.message;
    }
    try {
      validateCardExpiry(fields.expiry);
    } catch (err) {
      newErrors.expiry = err.message;
    }
    try {
      validateCVV(fields.cvv);
    } catch (err) {
      newErrors.cvv = err.message;
    }
    try {
      validateStreet(fields.street);
    } catch (err) {
      newErrors.street = err.message;
    }
    try {
      validateCity(fields.city);
    } catch (err) {
      newErrors.city = err.message;
    }
    try {
      validateUkPostcode(fields.postcode);
    } catch (err) {
      newErrors.postcode = err.message;
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSaveCard = async () => {
    if (!validateFields()) return;
    setSaveLoading(true);
    try {
      const payload = {
        cardholderName: fields.cardholderName,
        cardNumber: fields.cardNumber.replaceAll(/\s/g, ""),
        expiryDate: fields.expiry,
        billingAddress: { street: fields.street, city: fields.city, postcode: fields.postcode },
      };
      await saveCard(payload);
      setIsSaved(true);
      onCardSaved?.();
      setTimeout(() => {
        if (manageMode) {
          setFields(emptyFields);
        }
        setUseSaved(true);
        setIsSaved(false);
      }, 1000);
    } catch {
      setErrors({ form: "Failed to save card. Please try again." });
    } finally {
      setSaveLoading(false);
    }
  };

  const confirmRemoveCard = async () => {
    if (!cardToRemove) return;
    setRemovingId(cardToRemove.savedCardId);
    try {
      await deleteCard(cardToRemove.savedCardId);
      onCardSaved?.();
      setCardToRemove(null);
    } catch {
      setErrors({ form: "Could not remove the card. Please try again." });
    } finally {
      setRemovingId(null);
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (useSaved && savedCards.length > 0) {
      onSubmit?.({ savedCardId: savedCards[selectedCardIndex].savedCardId });
      return;
    }
    if (!validateFields()) return;
    onSubmit?.({
      cardholderName: fields.cardholderName,
      cardNumber: fields.cardNumber.replaceAll(/\s/g, ""),
      expiry: fields.expiry,
      cvv: fields.cvv,
      address: { street: fields.street, city: fields.city, postcode: fields.postcode },
    });
  };

  const cardFormFields = (
    <>
      <Field id="cardholderName" label="Cardholder name" error={errors.cardholderName}>
        <input
          id="cardholderName"
          className="pf-input"
          type="text"
          placeholder="Jane Smith"
          value={fields.cardholderName}
          onChange={handleChange("cardholderName")}
          autoComplete="cc-name"
          disabled={loading || saveLoading}
          required
        />
      </Field>

      <Field id="cardNumber" label="Card number" error={errors.cardNumber}>
        <input
          id="cardNumber"
          className="pf-input"
          type="text"
          inputMode="numeric"
          placeholder="1234 5678 9012 3456"
          value={fields.cardNumber}
          onChange={handleChange("cardNumber", formatCardNumber)}
          autoComplete="cc-number"
          disabled={loading || saveLoading}
          required
        />
      </Field>

      <div className="ui-form-grid">
        <Field id="expiry" label="Expiry date" error={errors.expiry}>
          <input
            id="expiry"
            className="pf-input"
            type="text"
            inputMode="numeric"
            placeholder="MM/YY"
            value={fields.expiry}
            onChange={handleChange("expiry", formatExpiry)}
            autoComplete="cc-exp"
            disabled={loading || saveLoading}
            required
          />
        </Field>
        <Field id="cvv" label="CVV" error={errors.cvv}>
          <input
            id="cvv"
            className="pf-input"
            type="text"
            inputMode="numeric"
            placeholder="123"
            value={fields.cvv}
            onChange={handleChange("cvv", (value) => value.replaceAll(/\D/g, "").slice(0, 4))}
            autoComplete="cc-csc"
            disabled={loading || saveLoading}
            required
          />
        </Field>
      </div>

      <h3 className="payment-subtitle">Billing address</h3>

      <Field id="street" label="Street" error={errors.street}>
        <input
          id="street"
          className="pf-input"
          type="text"
          placeholder="123 Example Street"
          value={fields.street}
          onChange={handleChange("street")}
          autoComplete="address-line1"
          disabled={loading || saveLoading}
          required
        />
      </Field>

      <div className="ui-form-grid">
        <Field id="city" label="City" error={errors.city}>
          <input
            id="city"
            className="pf-input"
            type="text"
            placeholder="London"
            value={fields.city}
            onChange={handleChange("city")}
            autoComplete="address-level2"
            disabled={loading || saveLoading}
            required
          />
        </Field>
        <Field id="postcode" label="Postcode" error={errors.postcode}>
          <input
            id="postcode"
            className="pf-input"
            type="text"
            placeholder="SW1A 1AA"
            value={fields.postcode}
            onChange={handleChange("postcode")}
            autoComplete="postal-code"
            disabled={loading || saveLoading}
            required
          />
        </Field>
      </div>
    </>
  );

  return (
    <>
      <form onSubmit={handleSubmit} className="payment-form">
        {errors.form && <Alert type="error">{errors.form}</Alert>}

        {savedCards.length > 0 && (
          <div className="payment-tabs" role="tablist" aria-label="Payment method">
            <button
              type="button"
              className={`payment-tab ${useSaved ? "active" : ""}`}
              onClick={() => setUseSaved(true)}
            >
              {manageMode ? "Saved cards" : "Use saved card"}
            </button>
            <button
              type="button"
              className={`payment-tab ${!useSaved ? "active" : ""}`}
              onClick={() => setUseSaved(false)}
            >
              {manageMode ? "Add new card" : "Enter new card"}
            </button>
          </div>
        )}

        {useSaved && savedCards.length > 0 && (
          <div className="saved-card-list">
            {savedCards.map((card, index) => (
              <SavedCardPill
                key={card.savedCardId}
                card={card}
                selected={selectedCardIndex === index}
                onSelect={() => !manageMode && setSelectedCardIndex(index)}
                removable={manageMode}
                onRemove={() => setCardToRemove(card)}
                removeLoading={removingId === card.savedCardId}
              />
            ))}
          </div>
        )}

        {!useSaved && (
          <>
            {cardFormFields}

            {manageMode ? (
              <Button
                type="button"
                onClick={handleSaveCard}
                disabled={saveLoading}
                icon={isSaved ? <Check size={16} /> : <Lock size={16} />}
              >
                {saveLoading ? "Saving..." : isSaved ? "Card saved" : "Save card"}
              </Button>
            ) : (
              <Button
                type="button"
                variant="secondary"
                onClick={handleSaveCard}
                disabled={loading || saveLoading}
                icon={isSaved ? <Check size={16} /> : <CreditCard size={14} />}
              >
                {saveLoading ? "Saving..." : "Save card for future use"}
              </Button>
            )}
          </>
        )}

        {!manageMode && (
          <button type="submit" className="pf-submit" disabled={loading || saveLoading}>
            <Lock size={16} />
            {loading ? "Processing..." : "Pay securely"}
          </button>
        )}
      </form>

      <ConfirmDialog
        open={Boolean(cardToRemove)}
        title="Remove saved card?"
        message={`Remove the card ending in ${cardToRemove?.lastFour ?? ""}? This cannot be undone.`}
        confirmLabel="Remove"
        onCancel={() => setCardToRemove(null)}
        onConfirm={confirmRemoveCard}
      />
    </>
  );
};

PaymentForm.propTypes = {
  onSubmit: PropTypes.func,
  loading: PropTypes.bool,
  savedCards: PropTypes.arrayOf(
    PropTypes.shape({
      savedCardId: PropTypes.number.isRequired,
      cardholderName: PropTypes.string.isRequired,
      lastFour: PropTypes.string.isRequired,
      expiryDate: PropTypes.string.isRequired,
    })
  ),
  onCardSaved: PropTypes.func,
  manageMode: PropTypes.bool,
};

export default PaymentForm;
