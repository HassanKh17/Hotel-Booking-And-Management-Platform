import axios from "axios";

const API_BASE = `${process.env.REACT_APP_API_URL}/admin`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

// GET all owners
export const getOwners = async () => {
    const response = await api.get("/owners");
    return response.data;
};

// POST create owner
export const registerOwner = async (ownerData) => {
    const response = await api.post("/register-owner", ownerData);
    return response.data;
};

// PUT update owner
export const editOwner = async (ownerId, ownerData) => {
    const response = await api.put(`/owners/${ownerId}`, ownerData);
    return response.data;
};

// DELETE owner
export const removeOwner = async (ownerId) => {
    await api.delete(`/owners/${ownerId}`);
};

// PATCH update global charges
export const updateGlobalCharges = async (chargesData) => {
    const response = await api.put("/charges", chargesData);
    return response.data;
};

// GET Account Statement
export const getOwnerStatement = async (ownerId) => {
    try {
        const response = await api.get(`/owners/${ownerId}/statement`);
        return response.data;
    } catch (error) {
        throw new Error(
            error.response?.data?.message || "Failed to load owner statement."
        );
    }
};