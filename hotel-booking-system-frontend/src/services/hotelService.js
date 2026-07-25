import axios from "axios";

const api = axios.create({
    baseURL: `${process.env.REACT_APP_API_URL}/hotels`,
    withCredentials: true,
    // Do NOT set Content-Type here globally — multipart needs to set its own
});

// GET all hotels (public)
export const getAllHotels = async () => {
  const response = await api.get("");
  return response.data;
};

// GET single hotel by ID (public)
export const getHotelById = async (hotelId) => {
    const response = await api.get(`/${hotelId}`);
    return response.data;
};

// GET hotels belonging to the logged-in owner
export const getMyHotels = async () => {
    const response = await api.get("/my-hotels");
    return response.data;
};

// POST create hotel (multipart — hotel JSON + optional image)
export const createHotel = async (formData) => {
    const response = await api.post("", formData);
    return response.data;
};

// PUT update hotel (multipart — hotel JSON + optional image)
export const updateHotel = async (hotelId, formData) => {
    const response = await api.put(`/${hotelId}`, formData);
    return response.data;
};

// DELETE hotel
export const deleteHotel = async (hotelId) => {
    await api.delete(`/${hotelId}`);
};
export const getHotelsByCity = async (city) => {
  const response = await api.get("", { params: { city } });
  return response.data;
};

export const getReviewsByHotel = async (hotelId) => {
  const response = await api.get(`/${hotelId}/reviews`);
  return response.data;
};

export const getAverageRating = async (hotelId) => {
  const response = await api.get(`/${hotelId}/reviews/average`);
  return response.data;
};

export const addReview = async (hotelId, customerId, review) => {
  const response = await api.post(`/${hotelId}/reviews/${customerId}`, review);
  return response.data;
};

export const updateReview = async (hotelId, reviewId, review) => {
  const response = await api.put(`/${hotelId}/reviews/${reviewId}/edit`, review);
  return response.data;
};

export const deleteReview = async (hotelId, reviewId) => {
  await api.delete(`/${hotelId}/reviews/${reviewId}`);
};

export const canReview = async (hotelId, customerId) => {
  const response = await api.get(`/${hotelId}/reviews/can-review/${customerId}`);
  return response.data;
};

export const replyToReview = async (hotelId, reviewId, reply) => {
  const response = await api.put(`/${hotelId}/reviews/${reviewId}/reply`, { reply });
  return response.data;
};
