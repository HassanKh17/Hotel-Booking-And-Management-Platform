import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CalendarDays, Hotel, MapPin, Search, SlidersHorizontal, Star, Tag } from "lucide-react";
import { getAllHotels } from "../services/hotelService";
import {
  Badge,
  Button,
  Card,
  EmptyState,
  LoadingState,
  PageShell,
  SectionHeader,
} from "../components/ui";
import {
  formatCurrency,
  getHotelImageUrl,
  getLowestEffectiveRoomPrice,
  getLowestRoomPrice,
  truncateText,
} from "../utils/formatters";
import "../styles/HotelsPage.css";

function HotelsPage() {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState({
    search: "",
    city: "",
    minRating: "",
    maxPrice: "",
    checkIn: "",
    checkOut: "",
  });
  const navigate = useNavigate();

  useEffect(() => {
    getAllHotels()
      .then(setHotels)
      .catch(() => setError("Failed to load hotels."))
      .finally(() => setLoading(false));
  }, []);

  const updateFilter = (name, value) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const clearFilters = () => {
    setFilters({
      search: "",
      city: "",
      minRating: "",
      maxPrice: "",
      checkIn: "",
      checkOut: "",
    });
  };

  const cityOptions = [...new Set(hotels.map((hotel) => hotel.address?.city).filter(Boolean))]
    .sort((a, b) => a.localeCompare(b));

  const filteredHotels = hotels.filter((hotel) => {
    const search = filters.search.trim().toLowerCase();
    const city = hotel.address?.city ?? "";
    const lowestPrice = getLowestEffectiveRoomPrice(hotel.rooms);
    const availableRooms = hotel.rooms?.filter((room) => room.occupancyStatus === "AVAILABLE") ?? [];

    const matchesSearch =
      !search ||
      hotel.name?.toLowerCase().includes(search) ||
      city.toLowerCase().includes(search) ||
      hotel.description?.toLowerCase().includes(search);

    const matchesCity = !filters.city || city === filters.city;
    const matchesRating = !filters.minRating || Number(hotel.starRating ?? 0) >= Number(filters.minRating);
    const matchesPrice = !filters.maxPrice || (lowestPrice !== null && lowestPrice <= Number(filters.maxPrice));
    const wantsDates = Boolean(filters.checkIn && filters.checkOut);
    const matchesAvailability = !wantsDates || !hotel.rooms?.length || availableRooms.length > 0;

    return matchesSearch && matchesCity && matchesRating && matchesPrice && matchesAvailability;
  });

  const hasActiveFilters = Object.values(filters).some(Boolean);

  return (
    <PageShell
      className="hotels-page"
      eyebrow="Hotel booking"
      title="Find your next stay"
      description="Browse hotels, compare room options, and choose dates with a calmer booking experience."
      actions={
        <>
          <Button to="/bookings" variant="secondary" icon={<CalendarDays size={17} />}>
            My Bookings
          </Button>
          <Button to="/account" variant="secondary" icon={<Hotel size={17} />}>
            My Account
          </Button>
        </>
      }
    >
      <Card>
        <SectionHeader
          title="Explore hotels"
          description={`${filteredHotels.length} of ${hotels.length} hotel${hotels.length === 1 ? "" : "s"} shown`}
          actions={
            hasActiveFilters && (
              <Button variant="ghost" onClick={clearFilters}>
                Clear filters
              </Button>
            )
          }
        />

        <div className="hotel-filters" aria-label="Hotel filters">
          <div className="hotel-filter">
            <label htmlFor="hotelSearch">
              <Search size={14} /> Search
            </label>
            <input
              id="hotelSearch"
              type="search"
              placeholder="Hotel name, city, or description"
              value={filters.search}
              onChange={(event) => updateFilter("search", event.target.value)}
            />
          </div>

          <div className="hotel-filter">
            <label htmlFor="cityFilter">
              <MapPin size={14} /> City
            </label>
            <select
              id="cityFilter"
              value={filters.city}
              onChange={(event) => updateFilter("city", event.target.value)}
            >
              <option value="">Any city</option>
              {cityOptions.map((city) => (
                <option key={city} value={city}>
                  {city}
                </option>
              ))}
            </select>
          </div>

          <div className="hotel-filter">
            <label htmlFor="ratingFilter">
              <Star size={14} /> Rating
            </label>
            <select
              id="ratingFilter"
              value={filters.minRating}
              onChange={(event) => updateFilter("minRating", event.target.value)}
            >
              <option value="">Any rating</option>
              <option value="5">5 stars</option>
              <option value="4">4+ stars</option>
              <option value="3">3+ stars</option>
            </select>
          </div>

          <div className="hotel-filter">
            <label htmlFor="priceFilter">
              <Tag size={14} /> Max price
            </label>
            <input
              id="priceFilter"
              type="number"
              min="0"
              step="10"
              placeholder="Any"
              value={filters.maxPrice}
              onChange={(event) => updateFilter("maxPrice", event.target.value)}
            />
          </div>

          <div className="hotel-filter">
            <label htmlFor="checkInFilter">Check-in</label>
            <input
              id="checkInFilter"
              type="date"
              value={filters.checkIn}
              onChange={(event) => updateFilter("checkIn", event.target.value)}
            />
          </div>

          <div className="hotel-filter">
            <label htmlFor="checkOutFilter">
              <SlidersHorizontal size={14} /> Check-out
            </label>
            <input
              id="checkOutFilter"
              type="date"
              value={filters.checkOut}
              min={filters.checkIn || undefined}
              onChange={(event) => updateFilter("checkOut", event.target.value)}
            />
          </div>
        </div>
      </Card>

      <div className="ui-section">
        {loading && <LoadingState label="Loading hotels..." />}
        {error && (
          <EmptyState
            title="Hotels could not be loaded"
            description={error}
            actions={
              <Button variant="secondary" onClick={() => window.location.reload()}>
                Try again
              </Button>
            }
          />
        )}

        {!loading && !error && filteredHotels.length === 0 && (
          <EmptyState
            icon={<Hotel size={24} />}
            title="No hotels match those filters"
            description="Try widening your search, clearing the price cap, or choosing another city."
            actions={
              hasActiveFilters && (
                <Button variant="secondary" onClick={clearFilters}>
                  Clear filters
                </Button>
              )
            }
          />
        )}

        {!loading && !error && filteredHotels.length > 0 && (
          <div className="hotel-card-grid">
            {filteredHotels.map((hotel) => {
              const price = getLowestEffectiveRoomPrice(hotel.rooms);
              const originalPrice = getLowestRoomPrice(hotel.rooms);
              const imageUrl = getHotelImageUrl(hotel.photoUrl);
              const availableRooms =
                hotel.rooms?.filter((room) => room.occupancyStatus === "AVAILABLE").length ?? 0;

              return (
                <article className="hotel-card" key={hotel.hotelId}>
                  <div className="hotel-card__media">
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
                  <div className="hotel-card__content">
                    <div className="hotel-card__topline">
                      <Badge tone="info">
                        <MapPin size={13} /> {hotel.address?.city ?? "Location TBC"}
                      </Badge>
                      <span className="star-rating" aria-label={`${hotel.starRating ?? 0} star rating`}>
                        {"★".repeat(Number(hotel.starRating ?? 0))}
                      </span>
                    </div>

                    <div>
                      <h3>{hotel.name}</h3>
                      <p>{truncateText(hotel.description || "Comfortable rooms and thoughtful amenities.", 118)}</p>
                    </div>

                    <div className="hotel-card__topline">
                      {hotel.specialOfferPercentage > 0 && (
                        <Badge tone="warning">{hotel.specialOfferPercentage}% off</Badge>
                      )}
                      <Badge tone={availableRooms > 0 ? "success" : "neutral"}>
                        {availableRooms > 0 ? `${availableRooms} available` : "Check rooms"}
                      </Badge>
                    </div>

                    <div className="hotel-card__footer">
                      <div className="hotel-price">
                        <span>From</span>
                        {hotel.specialOfferPercentage > 0 && originalPrice && price && price < originalPrice && (
                          <small className="price-original">{formatCurrency(originalPrice)}</small>
                        )}
                        <strong>{price ? formatCurrency(price) : "Price TBC"}</strong>
                      </div>
                      <Button onClick={() => navigate(`/hotels/${hotel.hotelId}`)}>
                        View hotel
                      </Button>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </div>
    </PageShell>
  );
}

export default HotelsPage;
