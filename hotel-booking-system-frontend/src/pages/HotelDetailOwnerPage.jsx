import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { BedDouble, MapPin, MessageSquare, Pencil, Sparkles, Trash2, Users } from "lucide-react";
import { getHotelById, getReviewsByHotel, replyToReview } from "../services/hotelService";
import {
  Badge,
  BackButton,
  Button,
  Card,
  ConfirmDialog,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import {
  formatCurrency,
  formatRoomType,
  getHotelImageUrl,
  getLowestRoomPrice,
} from "../utils/formatters";
import "../styles/HotelDetailPage.css";

function HotelDetailOwnerPage() {
  const { hotelId } = useParams();
  const navigate = useNavigate();

  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [reviews, setReviews] = useState([]);
  const [reviewError, setReviewError] = useState("");
  const [replyingToReview, setReplyingToReview] = useState(null);
  const [replyText, setReplyText] = useState("");
  const [confirmAction, setConfirmAction] = useState(null);

  useEffect(() => {
    getHotelById(hotelId)
      .then(setHotel)
      .catch(() => setError("Failed to load hotel details."))
      .finally(() => setLoading(false));
  }, [hotelId]);

  useEffect(() => {
    getReviewsByHotel(hotelId)
      .then(setReviews)
      .catch(() => setReviewError("Failed to load customer reviews."));
  }, [hotelId]);

  const handleReply = (reviewId, overrideText) => {
    const text = overrideText === undefined ? replyText : overrideText;

    replyToReview(hotelId, reviewId, text)
      .then((updated) => {
        setReviews((prev) => prev.map((review) => (review.id === updated.id ? updated : review)));
        setReplyingToReview(null);
        setReplyText("");
        setReviewError("");
      })
      .catch(() => setReviewError("Failed to save owner reply."));
  };

  const requestConfirm = (title, message, onConfirm) => {
    setConfirmAction({ title, message, onConfirm });
  };

  const groupedRooms = hotel?.rooms
    ? Object.values(
        hotel.rooms.reduce((acc, room) => {
          const key = `${room.occupancyType}-${room.capacity}-${room.pricePerNight}`;

          if (!acc[key]) {
            acc[key] = {
              ...room,
              count: 0,
            };
          }

          acc[key].count += 1;

          return acc;
        }, {})
      )
    : [];

  const imageUrl = getHotelImageUrl(hotel?.photoUrl);
  const lowestPrice = getLowestRoomPrice(hotel?.rooms ?? []);

  return (
    <PageShell
      className="hotel-detail-page"
      toolbar={<BackButton label="Back to hotels" onClick={() => navigate("/hotel-owner/hotels")} />}
    >
      {loading && <LoadingState label="Loading hotel details..." />}
      {error && <EmptyState title="Hotel details could not be loaded" description={error} />}

      {hotel && (
        <>
          <Card className="hotel-hero">
            <div className="hotel-hero__media">
              <div className="hotel-image-placeholder">Hotel image coming soon</div>
              {imageUrl && (
                <img
                  src={imageUrl}
                  alt={hotel.name}
                  onError={(event) => {
                    event.currentTarget.style.display = "none";
                  }}
                />
              )}
            </div>
            <div className="hotel-hero__content">
              <div>
                <div className="hotel-card__topline">
                  <Badge tone="info">
                    <MapPin size={13} />
                    {hotel.address?.city ?? "Location TBC"}
                  </Badge>
                  {hotel.specialOfferPercentage > 0 && (
                    <Badge tone="warning">{hotel.specialOfferPercentage}% off</Badge>
                  )}
                </div>
                <h1>{hotel.name}</h1>
                <span className="star-rating" aria-label={`${hotel.starRating ?? 0} star rating`}>
                  {"★".repeat(Number(hotel.starRating ?? 0))}
                </span>
                <p className="hotel-address">
                  {hotel.address
                    ? `${hotel.address.street}, ${hotel.address.city}, ${hotel.address.postcode}`
                    : "Address to be confirmed"}
                </p>
                <p>{hotel.description || "No description provided."}</p>
              </div>

              <aside className="hotel-hero__summary" aria-label="Hotel summary">
                <div className="ui-stat-card">
                  <span>Total room records</span>
                  <strong>{hotel.rooms?.length ?? 0}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>From</span>
                  <strong>{lowestPrice ? formatCurrency(lowestPrice) : "Price TBC"}</strong>
                </div>
              </aside>
            </div>
          </Card>

          {(hotel.facilities?.length > 0 || hotel.amenities?.length > 0) && (
            <Card>
              <SectionHeader
                title="Amenities and facilities"
                description="The features currently shown to guests."
              />
              <div className="feature-chip-list">
                {[...(hotel.facilities ?? []), ...(hotel.amenities ?? [])].map((feature) => (
                  <div className="feature-chip" key={`${feature.facilityId ?? feature.amenityId}-${feature.name}`}>
                    <Sparkles size={18} />
                    <div>
                      <strong>{feature.name}</strong>
                      {feature.description && <span>{feature.description}</span>}
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          )}

          <Card>
            <SectionHeader
              title="Room inventory"
              description="Grouped by type, capacity, and nightly price."
            />

            {groupedRooms.length > 0 ? (
              <div className="room-grid">
                {groupedRooms.map((room, index) => (
                  <article className="room-card" key={`${room.occupancyType}-${index}`}>
                    <div className="room-card__title">
                      <div>
                        <h3>{formatRoomType(room.occupancyType)}</h3>
                        <p className="ui-meta">{room.count} room{room.count === 1 ? "" : "s"}</p>
                      </div>
                      <Badge tone="info">
                        <BedDouble size={13} /> Inventory
                      </Badge>
                    </div>
                    <div className="room-card__meta">
                      <span>
                        <Users size={16} /> {room.capacity} guest{room.capacity === 1 ? "" : "s"}
                      </span>
                      <span>{formatCurrency(room.pricePerNight)} per night</span>
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <EmptyState title="No rooms available" description="Add rooms from the hotel edit form." />
            )}
          </Card>

          <Card id="owner-reviews">
            <SectionHeader
              title="Customer reviews"
              description="Reply to guest comments directly from the owner portal."
              actions={<Badge tone="info">{reviews.length}</Badge>}
            />

            {reviewError && <p className="error-text">{reviewError}</p>}

            {reviews.length > 0 ? (
              <div className="review-list">
                {reviews.map((review) => (
                  <article className="review-card" key={review.id}>
                    <div className="review-card__header">
                      <div>
                        <strong>{review.customerUsername}</strong>
                        <span className="star-rating" aria-label={`${review.starRating ?? 0} star rating`}>
                          {"★".repeat(Number(review.starRating ?? 0))}
                        </span>
                      </div>
                    </div>

                    <p>{review.message}</p>

                    {review.reply && (
                      <div className="review-card__reply">
                        <strong>Owner reply</strong>
                        <p>{review.reply}</p>
                      </div>
                    )}

                    <div className="review-card__actions">
                      {replyingToReview === review.id ? (
                        <>
                          <textarea
                            value={replyText}
                            onChange={(event) => setReplyText(event.target.value)}
                            placeholder="Write your reply..."
                            className="form-control"
                            rows={3}
                          />
                          <Button onClick={() => handleReply(review.id)}>Submit reply</Button>
                          <Button
                            variant="secondary"
                            onClick={() => {
                              setReplyingToReview(null);
                              setReplyText("");
                            }}
                          >
                            Cancel
                          </Button>
                        </>
                      ) : (
                        <>
                          <Button
                            variant="secondary"
                            size="sm"
                            icon={<Pencil size={14} />}
                            onClick={() => {
                              setReplyingToReview(review.id);
                              setReplyText(review.reply ?? "");
                            }}
                          >
                            {review.reply ? "Edit reply" : "Reply"}
                          </Button>
                          {review.reply && (
                            <Button
                              variant="danger"
                              size="sm"
                              icon={<Trash2 size={14} />}
                              onClick={() =>
                                requestConfirm(
                                  "Delete owner reply?",
                                  "The reply will be removed from this customer review.",
                                  () => handleReply(review.id, "")
                                )
                              }
                            >
                              Delete reply
                            </Button>
                          )}
                        </>
                      )}
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <EmptyState
                icon={<MessageSquare size={24} />}
                title="No reviews yet"
                description="Customer reviews will appear here once guests submit them."
              />
            )}
          </Card>
        </>
      )}

      <ConfirmDialog
        open={Boolean(confirmAction)}
        title={confirmAction?.title ?? "Confirm action"}
        message={confirmAction?.message ?? ""}
        confirmLabel="Delete"
        onCancel={() => setConfirmAction(null)}
        onConfirm={() => {
          confirmAction?.onConfirm();
          setConfirmAction(null);
        }}
      />
    </PageShell>
  );
}

export default HotelDetailOwnerPage;
