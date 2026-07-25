import axios from "axios";
const API_BASE = `${process.env.REACT_APP_API_URL}`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
});

export const getMyBookings = async () => {
    const response = await api.get("/bookings/me", { withCredentials: true });
    return response.data;
};