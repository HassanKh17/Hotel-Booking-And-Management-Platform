import axios from "axios";

const API_BASE = `${process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1"}/admin/owners`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
});

export const getOwnerOverview = async (ownerId) => {
    const response = await api.get(`/${ownerId}/overview`);
    return response.data;
};