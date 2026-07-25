import axios from "axios";

import parseError from "../utils/parseError.js";

const api = axios.create({
    baseURL: `${process.env.REACT_APP_API_URL}/bookings`,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

export const createBooking = async (bookingData) => {
    try {
        const response = await api.post("", bookingData);
        return response.data;
    } catch (error) {
        // Standardise the error so the frontend can display it
        throw new Error(parseError(error, "Payment and booking failed"));
    }
};
