import axios from "axios";

const api = axios.create({
    baseURL: `${process.env.REACT_APP_API_URL}/rooms`,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
});

export const getUnavailableDatesForRoom = async (roomId) => {
    const response = await api.get(`/${roomId}/unavailable-dates`);
    return response.data;
};