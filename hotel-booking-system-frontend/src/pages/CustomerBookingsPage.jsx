import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { BedDouble, CalendarDays, Hotel } from "lucide-react";
import { getMyBookings } from "../services/customerBookingService";
import {
  Badge,
  BackButton,
  Card,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import { formatCurrency, formatDate } from "../utils/formatters";
import "../styles/CustomerBookingsPage.css";

function CustomerBookingsPage() {
  const [currentBookings, setCurrentBookings] = useState([]);
  const [previousBookings, setPreviousBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getMyBookings();
        setCurrentBookings(data.currentBookings || []);
        setPreviousBookings(data.previousBookings || []);
      } catch (err) {
        console.error("Failed to load bookings:", err);
        setError("Failed to load bookings.");
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, []);

  const renderBookingCard = (booking, status) => (
    <article className="customer-bookings__card" key={booking.bookingId}>
      <div className="customer-bookings__card-header">
        <div>
          <Badge tone={status === "current" ? "success" : "neutral"}>
            {status === "current" ? "Current" : "Previous"}
          </Badge>
          <h3 className="customer-bookings__booking-title">Booking #{booking.bookingId}</h3>
          <p className="customer-bookings__booking-date">Created {formatDate(booking.createdAt)}</p>
        </div>

        <div className="customer-bookings__total">{formatCurrency(booking.totalCost)}</div>
      </div>

      <div className="customer-bookings__items">
        <h4 className="customer-bookings__items-title">Booked rooms</h4>

        {booking.items?.length > 0 ? (
          booking.items.map((item, index) => (
            <div className="customer-bookings__item" key={`${booking.bookingId}-${item.roomId}-${index}`}>
              <div className="customer-bookings__item-row">
                <span className="label">
                  <Hotel size={14} /> Hotel
                </span>
                <span>{item.hotelName}</span>
              </div>

              <div className="customer-bookings__item-row">
                <span className="label">
                  <BedDouble size={14} /> Room
                </span>
                <span>#{item.roomId}</span>
              </div>

              <div className="customer-bookings__item-row">
                <span className="label">
                  <CalendarDays size={14} /> Dates
                </span>
                <span>
                  {formatDate(item.checkIn)} to {formatDate(item.checkOut)}
                </span>
              </div>

              <div className="customer-bookings__item-row">
                <span className="label">Price</span>
                <span>{formatCurrency(item.price)}</span>
              </div>
            </div>
          ))
        ) : (
          <p className="customer-bookings__empty-text">No booking items found.</p>
        )}
      </div>
    </article>
  );

  const renderBookingSection = (title, bookings, status) => (
    <Card>
      <SectionHeader
        title={title}
        description={`${bookings.length} booking${bookings.length === 1 ? "" : "s"}`}
        actions={<Badge tone={status === "current" ? "success" : "neutral"}>{bookings.length}</Badge>}
      />

      {bookings.length === 0 ? (
        <EmptyState
          title={`No ${status} bookings`}
          description={
            status === "current"
              ? "Upcoming and active bookings will appear here."
              : "Completed bookings will appear here after your stay."
          }
        />
      ) : (
        <div className="customer-bookings__grid">
          {bookings.map((booking) => renderBookingCard(booking, status))}
        </div>
      )}
    </Card>
  );

  return (
    <PageShell
      className="customer-bookings-page"
      eyebrow="Guest account"
      title="My bookings"
      description="Track current reservations and revisit previous stays."
      toolbar={<BackButton onClick={() => navigate(-1)} />}
    >
      {loading ? (
        <LoadingState label="Loading bookings..." />
      ) : error ? (
        <EmptyState title="Bookings could not be loaded" description={error} />
      ) : (
        <div className="ui-section">
          {renderBookingSection("Current bookings", currentBookings, "current")}
          {renderBookingSection("Previous bookings", previousBookings, "previous")}
        </div>
      )}
    </PageShell>
  );
}

export default CustomerBookingsPage;
