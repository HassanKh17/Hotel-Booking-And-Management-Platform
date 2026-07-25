import axios from "axios";

const API_BASE = `${process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1"}/hotel-owner`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
});


export const getMyStatement = async () => {
    try {
        const response = await api.get("/statement", {
            withCredentials: true,
        });
        return response.data;
    } catch (error) {
        throw new Error(
            error.response?.data?.message || "Failed to load your statement."
        );
    }
};

export const getOccupancyDashboard = async () => {
    const response = await api.get("/occupancy-dashboard",
        {
            withCredentials: true,
        }
    );
    return response.data;
};