import { useEffect, useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { CalendarDays, Info, Users } from "lucide-react";
import { Button, Alert, Modal } from "./ui";
import { getUnavailableDatesForRoom } from "../services/roomService";
import {
  calculateNights,
  formatCurrency,
  formatRoomType,
  getEffectiveRoomPrice,
  hasRoomDiscount,
} from "../utils/formatters";

function AddToBasketModal({ room, onClose, onConfirm }) {
  const [checkIn, setCheckIn] = useState(null);
  const [checkOut, setCheckOut] = useState(null);
  const [error, setError] = useState("");
  const [unavailableRanges, setUnavailableRanges] = useState([]);
  const [loadingAvailability, setLoadingAvailability] = useState(true);

  useEffect(() => {
    let active = true;

    const loadAvailability = async () => {
      try {
        setLoadingAvailability(true);
        setError("");

        const data = await getUnavailableDatesForRoom(room.roomId);

        if (active) {
          const parsedRanges = (data || []).map((range) => ({
            start: new Date(range.checkIn),
            end: new Date(range.checkOut),
          }));
          setUnavailableRanges(parsedRanges);
        }
      } catch (err) {
        if (active) {
          setError("Could not load latest room availability.");
        }
      } finally {
        if (active) {
          setLoadingAvailability(false);
        }
      }
    };

    loadAvailability();

    return () => {
      active = false;
    };
  }, [room.roomId]);

  const overlapsUnavailableRange = (start, end) =>
    unavailableRanges.some((range) => start < range.end && end > range.start);

  const formatLocalDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const handleConfirm = () => {
    if (!checkIn || !checkOut) {
      setError("Please select both check-in and check-out dates.");
      return;
    }

    if (checkOut <= checkIn) {
      setError("Check-out must be after check-in.");
      return;
    }

    if (overlapsUnavailableRange(checkIn, checkOut)) {
      setError("Those dates are no longer available for this room. Please choose different dates.");
      return;
    }

    setError("");

    onConfirm({
      room,
      checkIn: formatLocalDate(checkIn),
      checkOut: formatLocalDate(checkOut),
    });
  };

  const nights = calculateNights(checkIn, checkOut);
  const nightlyPrice = getEffectiveRoomPrice(room);
  const estimatedTotal = nights * nightlyPrice;
  const discounted = hasRoomDiscount(room);

  return (
    <Modal
      open
      onClose={onClose}
      title="Add room to basket"
      footer={
        <>
          <Button variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleConfirm} disabled={loadingAvailability}>
            Add to basket
          </Button>
        </>
      }
    >
      <div className="room-card room-card--modal">
        <div className="room-card__title">
          <div>
            <h3>{formatRoomType(room.occupancyType)}</h3>
            <p className="ui-meta">Room #{room.roomId}</p>
          </div>
          <div className="room-card__price price-stack">
            {discounted && <span className="price-original">{formatCurrency(room.pricePerNight)}</span>}
            <strong className="price-current">{formatCurrency(nightlyPrice)}</strong>
          </div>
        </div>
        <div className="room-card__meta">
          <span>
            <Users size={16} /> {room.capacity} guest{room.capacity === 1 ? "" : "s"}
          </span>
          <span>
            <CalendarDays size={16} /> Price per night
          </span>
        </div>
      </div>

      {loadingAvailability ? (
        <Alert type="info" title="Checking availability">
          We are loading the latest unavailable dates for this room.
        </Alert>
      ) : (
        <>
          <div className="ui-form-grid">
            <div className="ui-field">
              <label htmlFor="checkIn">Check-in</label>
              <DatePicker
                id="checkIn"
                selected={checkIn}
                onChange={(date) => {
                  setCheckIn(date);
                  if (checkOut && date >= checkOut) {
                    setCheckOut(null);
                  }
                }}
                minDate={new Date()}
                excludeDateIntervals={unavailableRanges}
                placeholderText="Select check-in date"
                className="form-control"
                dateFormat="yyyy-MM-dd"
              />
            </div>

            <div className="ui-field">
              <label htmlFor="checkOut">Check-out</label>
              <DatePicker
                id="checkOut"
                selected={checkOut}
                onChange={(date) => setCheckOut(date)}
                minDate={checkIn || new Date()}
                excludeDateIntervals={unavailableRanges}
                placeholderText="Select check-out date"
                className="form-control"
                dateFormat="yyyy-MM-dd"
              />
            </div>
          </div>

          {unavailableRanges.length > 0 && (
            <Alert type="info" title="Unavailable dates are disabled">
              Choose another date range if a date cannot be selected.
            </Alert>
          )}

          {error && <Alert type="error">{error}</Alert>}

          <div className="checkout-total-container">
            <span className="checkout-total-label">
              {nights > 0 ? `${nights} night${nights === 1 ? "" : "s"}` : "Estimated total"}
            </span>
            <span className="checkout-total-amount">
              {nights > 0 ? formatCurrency(estimatedTotal) : <Info size={18} />}
            </span>
          </div>
        </>
      )}
    </Modal>
  );
}

export default AddToBasketModal;
