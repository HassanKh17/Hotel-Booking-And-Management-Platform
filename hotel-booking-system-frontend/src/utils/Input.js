// RFC 5321/Simple Mail Transfer Protocol compliant maximum email length
const EMAIL_MAX_LENGTH = 254;
const CARD_HOLDER_NAME_MAX_LENGTH = 64;

/**
 * Basic validation step before verifying email existence/sending to the server.
 */
export function validateEmail(email) {
  if (!email || typeof email !== 'string')
    throw new Error("Email is a required field");
  
  const trimmed = email.trim();
  
  if (trimmed.length > EMAIL_MAX_LENGTH)
    throw new Error(`Email cannot exceed ${EMAIL_MAX_LENGTH} characters`);
  
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
  if (!regex.test(trimmed))
    throw new Error("Please enter an email address in a valid format");
}

/**
 * A username format is valid if it is 3–30 characters long, contains only
 * letters, numbers, underscores, or hyphens, and has no whitespace
 */
export function validateUsername(username) {
  if (!username || typeof username !== 'string')
    throw new Error("Username is a required field");

  const trimmed = username.trim();
  if (trimmed.length < 3 || trimmed.length > 30)
    throw new Error("Username must be between 3 and 30 characters");
  if (/\s/.test(trimmed))
    throw new Error("Username cannot contain whitespace");
  if (!/^[a-zA-Z0-9_-]+$/.test(trimmed))
    throw new Error(
      "Username can only contain letters, numbers, underscores, hyphens");
}

/**
 * Validates the format of a password.
 */
export function validatePassword(password) {
  if (!password || typeof password !== 'string')
    throw new Error("Password is a required field");

  if (password !== password.trim())
    throw new Error("Password cannot start or end with whitespace");
  if (password.length < 16)
    throw new Error("Password must be at least 16 characters long");
  if (password.length > 64)
    throw new Error("Password cannot exceed 64 characters");
}

/**
 * Cardholder name must be 2–64 characters and can only contain letters, spaces,
 * hyphens, and apostrophes
 */
export function validateCardholderName(name) {
    if (!name || typeof name !== "string")
        throw new Error("Cardholder name is a required field");
    const trimmed = name.trim();
    if (trimmed.length < 2 || trimmed.length > CARD_HOLDER_NAME_MAX_LENGTH)
        throw new Error(
          `Cardholder name must be between 2 and ${CARD_HOLDER_NAME_MAX_LENGTH} characters`);
    if (!/^[a-zA-Z\s'-]+$/.test(trimmed))
        throw new Error(
          "Cardholder name can only contain letters, spaces, hyphens, and apostrophes");
}

/**
 * Validates that the card number consists of exactly 16 digits (spaces allowed)
 */
export function validateCardNumber(cardNumber) {
    if (!cardNumber || typeof cardNumber !== "string")
        throw new Error("Card number is a required field");
    const digits = cardNumber.replaceAll(/\s/g, "");
    if (!/^\d{16}$/.test(digits))
        throw new Error("Enter a valid 16-digit card number");
}

/**
   * Validates that the expiry date is in MM/YY format and is not in the past
 */
export function validateCardExpiry(expiry) {
    if (!expiry || typeof expiry !== "string")
        throw new Error("Expiry date is a required field");
    if (!/^\d{2}\/\d{2}$/.test(expiry))
        throw new Error("Enter a valid expiry date (MM/YY)");
    const [month, year] = expiry.split("/").map(Number);
    if (month < 1 || month > 12)
        throw new Error("Enter a valid expiry month");
    // Check if the card has expired
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1; 
    // Convert 2-digit input (e.g., "26") to 4-digit (2026)
    const inputYear = 2000 + year;
    if (inputYear < currentYear)
        throw new Error("Has this card expired?")
    if (inputYear === currentYear && month < currentMonth)
        throw new Error("Has this card expired?");
}

export function validateCVV(cvv) {
    if (!cvv || typeof cvv !== "string")
        throw new Error("CVV is a required field");
    if (!/^\d{3,4}$/.test(cvv))
        throw new Error("Enter a valid 3 or 4 digit CVV");
}

/**
 * Street allows letters, numbers, spaces, hyphens, periods, and commas.
 * Rejects scripts, HTML tags, and strings that are only numbers/only symbols.
 */
export function validateStreet(street) {
    if (!street?.trim()) throw new Error("Street address is required");
    
    const trimmed = street.trim();
    // Basic length check
    if (trimmed.length < 5) throw new Error("Street address is too short");
    // Forbidden: HTML tags or scripts
    if (/[<>]/.test(trimmed))
      throw new Error("Invalid characters in street address");
    // Allow alphanumeric and basic punctuation like - , .
    const streetRegex = /^[a-zA-Z0-9\s.,'-]+$/;
    if (!streetRegex.test(trimmed))
      throw new Error("Street contains invalid special characters");
    // Must contain at least one letter
    if (!/[a-zA-Z]/.test(trimmed))
      throw new Error("Street address must contain a street name");
}

/**
 * Validates City: Allows only letters, spaces, hyphens, and apostrophes.
 * Rejects all numbers and most special characters.
 */
export function validateCity(city) {
    if (!city?.trim()) throw new Error("City is required");
    
    const trimmed = city.trim();
    if (trimmed.length < 2) throw new Error("City name is too short");
    // Cities should not contain numbers
    if (/\d/.test(trimmed))
        throw new Error("City name cannot contain numbers");
    // Allow letters, spaces, hyphens, apostrophes, and periods
    const cityRegex = /^[a-zA-Z\s.'-]+$/;
    if (!cityRegex.test(trimmed)) {
        throw new Error("City name contains invalid characters");
    }
}

/**
 * Basic UK postcode format validation (e.g., "SW1A 1AA")
 */
export const validateUkPostcode = (postcode) => {
    if (!postcode?.trim()) throw new Error("Postcode is required");
    if (postcode && !/^[a-z]{1,2}\d[a-z\d]?\s*\d[a-z]{2}$/i.test(postcode))
        throw new Error("Postcode must be in a valid UK format");
}