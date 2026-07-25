import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { CalendarDays, CreditCard, ShoppingCart } from "lucide-react";

import { useBasket } from "../components/BasketContext";
import PaymentForm from "../components/PaymentForm";
import { createBooking } from "../services/bookingService";
import { getSavedCards } from "../services/cardService";
import { Alert, BackButton, Button, Card, EmptyState, PageShell, SectionHeader } from "../components/ui";
import {
  calculateNights,
  formatCurrency,
  formatDate,
  formatRoomType,
  getEffectiveRoomPrice,
  hasRoomDiscount,
} from "../utils/formatters";

import "../styles/CheckoutPage.css";

function CheckoutPage() {
  const { basket, clearBasket } = useBasket();
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState(null);
  const [savedCards, setSavedCards] = useState([]);
  const navigate = useNavigate();

  const loadCards = () => {
    getSavedCards()
      .then(setSavedCards)
      .catch(() => setSavedCards([]));
  };

  useEffect(() => {
    loadCards();
  }, []);

  const grandTotal = basket.reduce((sum, item) => {
    const nights = calculateNights(item.checkIn, item.checkOut);
    return sum + getEffectiveRoomPrice(item.room) * nights;
  }, 0);

  const handleSubmit = async (cardDetails) => {
    setLoading(true);
    setAlert(null);

    const bookingPayload = {
      ...cardDetails,
      items: basket.map((item) => ({
        roomId: item.room.roomId,
        price: getEffectiveRoomPrice(item.room),
        checkIn: item.checkIn,
        checkOut: item.checkOut,
      })),
    };

    try {
      await createBooking(bookingPayload);
      clearBasket();
      navigate("/confirmation", { state: { fromCheckout: true } });
    } catch (error) {
      const message = error.message?.toLowerCase() || "";

      if (
        message.includes("no longer available") ||
        message.includes("not available") ||
        message.includes("overlapping") ||
        message.includes("conflict") ||
        message.includes("illegalstateexception")
      ) {
        setAlert(
          "One or more rooms in your basket were just booked by someone else. Please go back, choose different dates, and try again."
        );
      } else {
        setAlert(error.message || "We could not complete your booking. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageShell
      className="checkout-page"
      eyebrow="Secure checkout"
      title="Complete your booking"
      description="Review the final details and pay securely."
      toolbar={<BackButton onClick={() => navigate(-1)} />}
    >
      <div className="booking-stepper" aria-label="Booking progress">
        <div className="booking-step">
          <span>1</span> Basket
        </div>
        <div className="booking-step active">
          <span>2</span> Payment
        </div>
        <div className="booking-step">
          <span>3</span> Confirmation
        </div>
      </div>

      {basket.length === 0 ? (
        <Card>
          <EmptyState
            icon={<ShoppingCart size={24} />}
            title="Your basket is empty"
            description="Add a room before starting checkout."
            actions={<Button to="/hotels">Browse hotels</Button>}
          />
        </Card>
      ) : (
        <>
          {alert && (
            <Alert type="error" title="Booking could not be completed">
              {alert}
              <div className="ui-actions alert-actions">
                <Button variant="secondary" size="sm" onClick={() => navigate(-1)}>
                  Edit dates
                </Button>
                <Button variant="ghost" size="sm" onClick={() => setAlert(null)}>
                  Dismiss
                </Button>
              </div>
            </Alert>
          )}

          <div className="checkout-layout">
            <Card>
              <SectionHeader title="Booking summary" description="The rooms and dates in this checkout." />

              <div className="checkout-summary-list">
                {basket.map((item, index) => {
                  const nights = calculateNights(item.checkIn, item.checkOut);
                  const nightlyPrice = getEffectiveRoomPrice(item.room);
                  const subtotal = nightlyPrice * nights;
                  const discounted = hasRoomDiscount(item.room);

                  return (
                    <article className="checkout-summary-item" key={`${item.room.roomId}-${index}`}>
                      <div>
                        <h3>{item.hotelName}</h3>
                        <p className="ui-meta">{formatRoomType(item.room.occupancyType)}</p>
                        <p>
                          <CalendarDays size={15} /> {formatDate(item.checkIn)} to {formatDate(item.checkOut)}
                        </p>
                        <p className="ui-meta">
                          {nights} night{nights === 1 ? "" : "s"} at{" "}
                          <span className={discounted ? "price-inline" : ""}>
                            {discounted && <span className="price-original">{formatCurrency(item.room.pricePerNight)}</span>}
                            <span>{formatCurrency(nightlyPrice)}</span>
                          </span>
                        </p>
                      </div>
                      <strong>{formatCurrency(subtotal)}</strong>
                    </article>
                  );
                })}
              </div>

              <div className="checkout-total-container">
                <span className="checkout-total-label">Total to pay</span>
                <span className="checkout-total-amount">{formatCurrency(grandTotal)}</span>
              </div>
            </Card>

            <Card>
              <SectionHeader
                title="Payment details"
                description="Use a saved card or enter a new payment method."
                actions={<CreditCard size={20} />}
              />
              <PaymentForm
                onSubmit={handleSubmit}
                loading={loading}
                savedCards={savedCards}
                onCardSaved={loadCards}
              />
            </Card>
          </div>
        </>
      )}
    </PageShell>
  );
}

export default CheckoutPage;
