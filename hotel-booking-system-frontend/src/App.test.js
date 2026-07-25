import {
  calculateNights,
  formatCurrency,
  formatRoomType,
  getEffectiveRoomPrice,
  getLowestEffectiveRoomPrice,
  hasRoomDiscount,
} from "./utils/formatters";

test("formats booking values for the redesigned UI", () => {
  expect(calculateNights("2026-06-01", "2026-06-04")).toBe(3);
  expect(formatCurrency(125)).toContain("125.00");
  expect(formatRoomType("DOUBLE_DELUXE")).toBe("Double Deluxe");
});

test("uses discounted room prices when special offers apply", () => {
  const discountedRoom = {
    pricePerNight: "100.00",
    discountedPricePerNight: "90.00",
    specialOfferApplied: true,
  };
  const fullPriceRoom = {
    pricePerNight: "80.00",
    discountedPricePerNight: "80.00",
    specialOfferApplied: false,
  };

  expect(getEffectiveRoomPrice(discountedRoom)).toBe(90);
  expect(hasRoomDiscount(discountedRoom)).toBe(true);
  expect(hasRoomDiscount(fullPriceRoom)).toBe(false);
  expect(getLowestEffectiveRoomPrice([discountedRoom, fullPriceRoom])).toBe(80);
});
