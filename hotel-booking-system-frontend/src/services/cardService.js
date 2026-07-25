import axios from "axios";

import parseError from "../utils/parseError.js";

const api = axios.create({
    baseURL: `${process.env.REACT_APP_API_URL}/cards`,
    headers: {
        "Content-Type": "application/json",
    },
    withCredentials: true,
});

export const getSavedCards = async () => {
    try {
        const response = await api.get("");
        return response.data;
    } catch (error) {
        // Standardise the error so the frontend can display it
        throw new Error(parseError(error, "Failed to fetch saved cards"));
    }
};

export const saveCard = async (cardData) => {
    try {
        const response = await api.post("", cardData);
        return response.data;
    } catch (error) {
        throw new Error(parseError(error, "Failed to save card"));
    }
};

export async function deleteCard(savedCardId) {
    try {
        const response = await api.delete(`/delete/${savedCardId}`);
        return response.data;
    } catch (error) {
        throw new Error(parseError(error, "Failed to delete card"));
    }
}