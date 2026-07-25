import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  BedDouble,
  CheckCircle2,
  MapPin,
  MessageSquare,
  Pencil,
  ShieldCheck,
  Sparkles,
  Trash2,
  Users,
} from "lucide-react";
import {
  getHotelById,
  getReviewsByHotel,
  getAverageRating,
  addReview,
  updateReview,
  deleteReview,
  canReview,
  replyToReview,
} from "../services/hotelService";
import { getMe } from "../services/authService";
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
import AddToBasketModal from "../components/AddToBasketModal";
import { useBasket } from "../components/BasketContext";
import {
  formatCurrency,
  formatRoomType,
  getEffectiveRoomPrice,
  getHotelImageUrl,
  getLowestEffectiveRoomPrice,
  getLowestRoomPrice,
  hasRoomDiscount,
} from "../utils/formatters";
import "../styles/HotelDetailPage.css";

function StarDisplay({ rating = 0, label }) {
  const value = Math.max(0, Math.min(5, Math.round(Number(rating) || 0)));
  return (
    <span className="star-rating" aria-label={label ?? `${value} star rating`}>
      {"★".repeat(value)}
    </span>
  );
}

function StarPicker({ value, onChange, label }) {
  return (
    <div className="star-input" role="radiogroup" aria-label={label}>
      {[1, 2, 3, 4, 5].map((number) => (
        <button
          key={number}
          type="button"
          className={number <= value ? "active" : ""}
          aria-label={`${number} star${number === 1 ? "" : "s"}`}
          aria-checked={value === number}
          role="radio"
          onClick={() => onChange(number)}
        >
          ★
        </button>
      ))}
    </div>
  );
}

function HotelDetailPage() {
  const { hotelId } = useParams();
  const navigate = useNavigate();
  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedRoom, setSelectedRoom] = useState(null);
  const { addToBasket, isRoomAdded } = useBasket();
  const [currentUser, setCurrentUser] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [averageRating, setAverageRating] = useState(null);
  const [newReview, setNewReview] = useState({ starRating: 0, message: "" });
  const [editingReview, setEditingReview] = useState(null);
  const [canLeaveReview, setCanLeaveReview] = useState(false);
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
    getMe()
      .then((user) => {
        setCurrentUser(user);
        if (user.role === "ROLE_CUSTOMER") {
          canReview(hotelId, user.id).then(setCanLeaveReview).catch(() => setCanLeaveReview(false));
        }
      })
      .catch(() => setCurrentUser(null));

    getReviewsByHotel(hotelId).then(setReviews).catch(() => setReviews([]));
    getAverageRating(hotelId).then((val) => setAverageRating(val ? Number.parseFloat(val) : null));
  }, [hotelId]);

  const refreshAverageRating = () => {
    getAverageRating(hotelId).then((val) => setAverageRating(val ? Number.parseFloat(val) : null));
  };

  const handleSubmitReview = () => {
    addReview(hotelId, currentUser.id, newReview)
      .then((saved) => {
        setReviews((prev) => [...prev, saved]);
        setNewReview({ starRating: 0, message: "" });
        setCanLeaveReview(false);
        refreshAverageRating();
      })
      .catch(() => setReviewError("Failed to submit review. Please try again."));
  };

  const handleDeleteReview = (reviewId) => {
    deleteReview(hotelId, reviewId)
      .then(() => {
        setReviews((prev) => prev.filter((review) => review.id !== reviewId));
        refreshAverageRating();
      })
      .catch(() => setReviewError("Failed to delete review. Please try again."));
  };

  const handleReply = (reviewId, overrideText) => {
    const text = overrideText === undefined ? replyText : overrideText;
    replyToReview(hotelId, reviewId, text)
      .then((updated) => {
        setReviews((prev) => prev.map((review) => (review.id === updated.id ? updated : review)));
        setReplyingToReview(null);
        setReplyText("");
      })
      .catch(() => setReviewError("Failed to submit reply. Please try again."));
  };

  const handleUpdateReview = () => {
    updateReview(hotelId, editingReview.id, editingReview)
      .then((updated) => {
        setReviews((prev) => prev.map((review) => (review.id === updated.id ? updated : review)));
        setEditingReview(null);
        refreshAverageRating();
      })
      .catch(() => setReviewError("Failed to update review. Please try again."));
  };

  const requestConfirm = (title, message, onConfirm) => {
    setConfirmAction({ title, message, onConfirm });
  };

  const rooms = hotel?.rooms || [];
  const imageUrl = getHotelImageUrl(hotel?.photoUrl);
  const lowestPrice = getLowestEffectiveRoomPrice(rooms);
  const lowestOriginalPrice = getLowestRoomPrice(rooms);
  const availableRooms = rooms.filter((room) => room.occupancyStatus === "AVAILABLE").length;

  return (
    <PageShell
      className="hotel-detail-page"
      toolbar={<BackButton label="Back to hotels" onClick={() => navigate("/hotels")} />}
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
                <StarDisplay rating={hotel.starRating} />
                <p className="hotel-address">
                  {hotel.address
                    ? `${hotel.address.street}, ${hotel.address.city}, ${hotel.address.postcode}`
                    : "Address to be confirmed"}
                </p>
                <p>{hotel.description || "A comfortable stay with practical amenities and room options."}</p>
                <a href="#reviews" className="hotel-review-link">
                  See customer reviews
                </a>
              </div>

              <aside className="hotel-hero__summary" aria-label="Hotel summary">
                <div className="ui-stat-card">
                  <span>Rooms available</span>
                  <strong>{availableRooms}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>From</span>
                  {hotel.specialOfferPercentage > 0 &&
                    lowestOriginalPrice &&
                    lowestPrice &&
                    lowestPrice < lowestOriginalPrice && (
                      <small className="price-original">{formatCurrency(lowestOriginalPrice)}</small>
                    )}
                  <strong>{lowestPrice ? formatCurrency(lowestPrice) : "Price TBC"}</strong>
                </div>
                <div className="ui-stat-card">
                  <span>Average rating</span>
                  <strong>{averageRating !== null ? `${averageRating.toFixed(1)} / 5` : "New"}</strong>
                </div>
              </aside>
            </div>
          </Card>

          {(hotel.facilities?.length > 0 || hotel.amenities?.length > 0) && (
            <Card>
              <SectionHeader
                title="Amenities and facilities"
                description="Quickly scan what is included before you choose a room."
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
              title="Choose a room"
              description="Unavailable rooms stay visible so guests understand current capacity."
            />
            {rooms.length > 0 ? (
              <div className="room-grid">
                {rooms.map((room) => {
                  const isAdded = isRoomAdded(room.roomId);
                  const isAvailable = room.occupancyStatus === "AVAILABLE";
                  const nightlyPrice = getEffectiveRoomPrice(room);
                  const discounted = hasRoomDiscount(room);

                  return (
                    <article className="room-card" key={room.roomId}>
                      <div className="room-card__title">
                        <div>
                          <h3>{formatRoomType(room.occupancyType)}</h3>
                          <p className="ui-meta">Room #{room.roomId}</p>
                        </div>
                        <Badge tone={isAvailable ? "success" : "neutral"}>
                          {isAvailable ? "Available" : "Unavailable"}
                        </Badge>
                      </div>

                      <div className="room-card__meta">
                        <span>
                          <Users size={16} /> {room.capacity} guest{room.capacity === 1 ? "" : "s"}
                        </span>
                        <span>
                          <BedDouble size={16} /> {formatRoomType(room.occupancyStatus)}
                        </span>
                      </div>

                      <div className="room-card__price price-stack">
                        {discounted && <span className="price-original">{formatCurrency(room.pricePerNight)}</span>}
                        <strong className="price-current">{formatCurrency(nightlyPrice)}</strong>
                      </div>

                      <Button
                        variant={!isAvailable || isAdded ? "secondary" : "primary"}
                        onClick={() => setSelectedRoom({ ...room, roomId: room.roomId })}
                        disabled={!isAvailable || isAdded}
                      >
                        {!isAvailable ? "Unavailable" : isAdded ? "Added to basket" : "Add to basket"}
                      </Button>
                    </article>
                  );
                })}
              </div>
            ) : (
              <EmptyState title="No rooms available" description="This hotel has not published room options yet." />
            )}
          </Card>

          <Card id="reviews">
            <SectionHeader
              title="Customer ratings"
              description={
                averageRating !== null
                  ? `Average rating ${averageRating.toFixed(1)} out of 5 from guest reviews.`
                  : "No guest ratings have been submitted yet."
              }
            />

            {reviewError && <p className="error-text">{reviewError}</p>}

            {reviews.length > 0 ? (
              <div className="review-list">
                {reviews.map((review) => (
                  <article className="review-card" key={review.id}>
                    {editingReview?.id === review.id ? (
                      <div>
                        <StarPicker
                          value={editingReview.starRating}
                          label="Edit star rating"
                          onChange={(starRating) => setEditingReview({ ...editingReview, starRating })}
                        />
                        <textarea
                          value={editingReview.message}
                          onChange={(event) =>
                            setEditingReview({ ...editingReview, message: event.target.value })
                          }
                          className="form-control"
                          rows={3}
                        />
                        <div className="review-card__actions">
                          <Button onClick={handleUpdateReview}>Save review</Button>
                          <Button variant="secondary" onClick={() => setEditingReview(null)}>
                            Cancel
                          </Button>
                        </div>
                      </div>
                    ) : (
                      <>
                        <div className="review-card__header">
                          <div>
                            <strong>{review.customerUsername}</strong>
                            <StarDisplay rating={review.starRating} />
                          </div>
                          {currentUser?.username === review.customerUsername && (
                            <div className="review-card__actions">
                              <Button
                                variant="secondary"
                                size="sm"
                                icon={<Pencil size={14} />}
                                onClick={() => setEditingReview(review)}
                              >
                                Edit
                              </Button>
                              <Button
                                variant="danger"
                                size="sm"
                                icon={<Trash2 size={14} />}
                                onClick={() =>
                                  requestConfirm(
                                    "Delete review?",
                                    "This review will be removed from the hotel page.",
                                    () => handleDeleteReview(review.id)
                                  )
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          )}
                        </div>

                        <p>{review.message}</p>

                        {review.reply && (
                          <div className="review-card__reply">
                            <strong>Owner reply</strong>
                            <p>{review.reply}</p>
                          </div>
                        )}

                        {currentUser &&
                          currentUser.role === "ROLE_HOTEL_OWNER" &&
                          hotel.ownerUsername === currentUser.username && (
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
                          )}
                      </>
                    )}
                  </article>
                ))}
              </div>
            ) : (
              <EmptyState
                icon={<MessageSquare size={24} />}
                title="No reviews yet"
                description="Verified guests can leave a review after a completed stay."
              />
            )}

            {canLeaveReview && (
              <div className="review-card review-card--form">
                <SectionHeader title="Leave a review" description="Share a concise rating for future guests." />
                <StarPicker
                  value={newReview.starRating}
                  label="Choose star rating"
                  onChange={(starRating) => setNewReview({ ...newReview, starRating })}
                />
                <textarea
                  placeholder="Write your review..."
                  value={newReview.message}
                  onChange={(event) => setNewReview({ ...newReview, message: event.target.value })}
                  className="form-control"
                  rows={4}
                />
                <Button onClick={handleSubmitReview} disabled={newReview.starRating === 0}>
                  Submit review
                </Button>
              </div>
            )}

            {!canLeaveReview &&
              currentUser &&
              currentUser.role === "ROLE_CUSTOMER" &&
              !reviews.some((review) => review.customerUsername === currentUser.username) && (
                <div className="review-card__reply">
                  <ShieldCheck size={18} />
                  You need a previous booking at this hotel before leaving a review.
                </div>
              )}

            {!canLeaveReview && !currentUser && (
              <div className="review-card__reply">
                <CheckCircle2 size={18} />
                Please <a href="/login/customer">sign in</a> to leave a review.
              </div>
            )}
          </Card>
        </>
      )}

      {selectedRoom && (
        <AddToBasketModal
          room={selectedRoom}
          onClose={() => setSelectedRoom(null)}
          onConfirm={(item) => {
            addToBasket({ ...item, hotelName: hotel.name });
            setSelectedRoom(null);
          }}
        />
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

export default HotelDetailPage;
