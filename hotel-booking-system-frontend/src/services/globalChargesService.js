import axios from "axios";

const API_BASE = `${process.env.REACT_APP_API_URL || "http://localhost:8080/api/v1"}/admin/global-charges`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
});

// GET current active global charges
export const getCurrentGlobalCharges = async () => {
    const response = await api.get("/current");
    return response.data;
};

export const getAllGlobalCharges = async () => {
    const response = await api.get("");
    return response.data;
};


// POST create new global charges record
export const updateGlobalCharges = async (chargesData) => {
    const response = await api.post("", chargesData);
    return response.data;
};