import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { BedDouble, CalendarDays, ShoppingCart, Trash2 } from "lucide-react";
import { useBasket } from "../components/BasketContext";
import {
  Badge,
  BackButton,
  Button,
  Card,
  ConfirmDialog,
  EmptyState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import {
  calculateNights,
  formatCurrency,
  formatDate,
  formatRoomType,
  getEffectiveRoomPrice,
  hasRoomDiscount,
} from "../utils/formatters";

function BasketPage() {
  const { basket, removeFromBasket } = useBasket();
  const [removeIndex, setRemoveIndex] = useState(null);
  const navigate = useNavigate();

  const grandTotal = basket.reduce((sum, item) => {
    const nights = calculateNights(item.checkIn, item.checkOut);
    return sum + getEffectiveRoomPrice(item.room) * nights;
  }, 0);

  return (
    <PageShell
      className="basket-page"
      eyebrow="Booking basket"
      title="Review your stay"
      description="Check your selected rooms and dates before moving to payment."
      toolbar={<BackButton onClick={() => navigate(-1)} />}
    >
      <div className="booking-stepper" aria-label="Booking progress">
        <div className="booking-step active">
          <span>1</span> Basket
        </div>
        <div className="booking-step">
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
            description="Choose a room from a hotel page to start a booking."
            actions={<Button to="/hotels">Browse hotels</Button>}
          />
        </Card>
      ) : (
        <div className="checkout-layout">
          <Card>
            <SectionHeader
              title="Selected rooms"
              description={`${basket.length} room${basket.length === 1 ? "" : "s"} ready for checkout.`}
            />

            <div className="basket-grid">
              {basket.map((item, index) => {
                const nights = calculateNights(item.checkIn, item.checkOut);
                const nightlyPrice = getEffectiveRoomPrice(item.room);
                const subtotal = nightlyPrice * nights;
                const discounted = hasRoomDiscount(item.room);

                return (
                  <article className="basket-item" key={`${item.room.roomId}-${item.checkIn}-${index}`}>
                    <div>
                      <div className="room-card__title">
                        <div>
                          <h3>{item.hotelName}</h3>
                          <p className="ui-meta">{formatRoomType(item.room.occupancyType)}</p>
                        </div>
                        <Badge tone="info">
                          <BedDouble size={13} /> Room #{item.room.roomId}
                        </Badge>
                      </div>

                      <div className="basket-item__details">
                        <div>
                          <span>Check-in</span>
                          <strong>{formatDate(item.checkIn)}</strong>
                        </div>
                        <div>
                          <span>Check-out</span>
                          <strong>{formatDate(item.checkOut)}</strong>
                        </div>
                        <div>
                          <span>Nights</span>
                          <strong>{nights}</strong>
                        </div>
                        <div>
                          <span>Nightly rate</span>
                          <strong className={discounted ? "price-inline" : ""}>
                            {discounted && <span className="price-original">{formatCurrency(item.room.pricePerNight)}</span>}
                            <span>{formatCurrency(nightlyPrice)}</span>
                          </strong>
                        </div>
                        <div>
                          <span>Subtotal</span>
                          <strong>{formatCurrency(subtotal)}</strong>
                        </div>
                      </div>
                    </div>

                    <Button
                      variant="danger"
                      size="sm"
                      icon={<Trash2 size={14} />}
                      onClick={() => setRemoveIndex(index)}
                    >
                      Remove
                    </Button>
                  </article>
                );
              })}
            </div>
          </Card>

          <Card>
            <SectionHeader title="Price summary" description="Final payment total before taxes or fees." />
            <div className="checkout-total-container">
              <span className="checkout-total-label">Total to pay</span>
              <span className="checkout-total-amount">{formatCurrency(grandTotal)}</span>
            </div>
            <Button
              className="checkout-primary-action"
              size="lg"
              icon={<CalendarDays size={18} />}
              onClick={() => navigate("/checkout")}
            >
              Proceed to checkout
            </Button>
          </Card>
        </div>
      )}

      <ConfirmDialog
        open={removeIndex !== null}
        title="Remove room?"
        message="This room will be removed from your basket. You can add it again from the hotel page."
        confirmLabel="Remove"
        onCancel={() => setRemoveIndex(null)}
        onConfirm={() => {
          removeFromBasket(removeIndex);
          setRemoveIndex(null);
        }}
      />
    </PageShell>
  );
}

export default BasketPage;
