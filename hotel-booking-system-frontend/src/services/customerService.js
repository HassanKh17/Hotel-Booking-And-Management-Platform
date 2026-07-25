import axios from 'axios';

const API_BASE = `${process.env.REACT_APP_API_URL}/customer`;

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        "Content-Type": "application/json",
    },
});

const getErrorMessage = (error, fallback) => {
    const data = error.response?.data;
    if (data?.error) return data.error;
    if (data) {
        const firstError = Object.values(data)[0];
        if (firstError) return firstError;
    }
    return fallback;
};

export const register = async (username, email, password) => {
    try {
        const response = await api.post("/register",
            { username, email, password }, { withCredentials: true });
        return response.data;
    } catch (error) {
        const data = error.response?.data;
        throw new Error(typeof data === 'string' ? data : getErrorMessage(error,
            'Registration failed. Please try again.'));
    }
};


export async function getMyDetails() {
    const response = await api.get("/me", { withCredentials: true });
    return response.data;

}

export async function updateMyDetails(payload) {
    const response = await api.put("/me", payload, { withCredentials: true });
    return response.data;
}

/* Phase 2 placeholders */
export async function getMySavedCards() {
    const response = await api.get("/me/cards");
    return response.data;
}

export async function addSavedCard(payload) {
    const response = await api.post("/me/cards", payload);
    return response.data;
}

export async function removeSavedCard(cardId) {
    const response = await api.delete(`/me/cards/${cardId}`);
    return response.data;
}