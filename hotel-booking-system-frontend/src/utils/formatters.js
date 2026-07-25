const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1";
const API_ORIGIN = API_BASE_URL.replace(/\/api\/v1\/?$/, "");

export const formatCurrency = (value) =>
  new Intl.NumberFormat("en-GB", {
    style: "currency",
    currency: "GBP",
  }).format(Number(value ?? 0));

export const formatDate = (value) => {
  if (!value) return "Not set";

  return new Date(value).toLocaleDateString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

export const formatDateTime = (value) => {
  if (!value) return "-";

  return new Date(value).toLocaleString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

export const calculateNights = (checkIn, checkOut) => {
  if (!checkIn || !checkOut) return 0;

  const msPerDay = 1000 * 60 * 60 * 24;
  return Math.max(
    0,
    Math.round((new Date(checkOut) - new Date(checkIn)) / msPerDay)
  );
};

export const formatRoomType = (value) => {
  if (!value) return "Room";

  return value
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
};

export const formatStatementType = (type) => {
  if (!type) return "-";
  return formatRoomType(type);
};

export const getHotelImageUrl = (photoUrl) => {
  if (!photoUrl) return "";
  if (photoUrl.startsWith("http")) return photoUrl;
  if (photoUrl.startsWith("/api/v1")) return `${API_ORIGIN}${photoUrl}`;
  return `${API_BASE_URL}${photoUrl.startsWith("/") ? photoUrl : `/${photoUrl}`}`;
};

export const truncateText = (value, maxLength = 140) => {
  if (!value) return "";
  if (value.length <= maxLength) return value;
  return `${value.slice(0, maxLength).trim()}...`;
};

export const pluralize = (count, singular, plural = `${singular}s`) =>
  `${count} ${count === 1 ? singular : plural}`;

export const getLowestRoomPrice = (rooms = []) => {
  const prices = rooms
    .map((room) => Number(room.pricePerNight))
    .filter((price) => Number.isFinite(price) && price > 0);

  if (!prices.length) return null;
  return Math.min(...prices);
};

export const getEffectiveRoomPrice = (room = {}) => {
  const discountedPrice = Number(room.discountedPricePerNight);
  const originalPrice = Number(room.pricePerNight);

  if (room.specialOfferApplied && Number.isFinite(discountedPrice) && discountedPrice > 0) {
    return discountedPrice;
  }

  return Number.isFinite(originalPrice) ? originalPrice : 0;
};

export const hasRoomDiscount = (room = {}) => {
  const originalPrice = Number(room.pricePerNight);
  const effectivePrice = getEffectiveRoomPrice(room);

  return Boolean(
    room.specialOfferApplied &&
      Number.isFinite(originalPrice) &&
      Number.isFinite(effectivePrice) &&
      effectivePrice > 0 &&
      effectivePrice < originalPrice
  );
};

export const getLowestEffectiveRoomPrice = (rooms = []) => {
  const prices = rooms
    .map(getEffectiveRoomPrice)
    .filter((price) => Number.isFinite(price) && price > 0);

  if (!prices.length) return null;
  return Math.min(...prices);
};

export const getBalanceTone = (value) => {
  const amount = Number(value ?? 0);
  if (amount > 0) return "positive";
  if (amount < 0) return "negative";
  return "neutral";
};

export const getBalanceClassName = (value) =>
  `balance-value balance-value--${getBalanceTone(value)}`;
