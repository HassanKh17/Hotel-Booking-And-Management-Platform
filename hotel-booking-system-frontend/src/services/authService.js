import axios from 'axios';

const API_BASE = `${process.env.REACT_APP_API_URL}/auth`;

const api = axios.create({
    baseURL: API_BASE,
    withCredentials: true,
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

export const login = async (username, password) => {
    try {
        const response = await api.post("/login", { username, password }, { withCredentials: true });
        return response.data;
    } catch (error) {
        throw new Error(getErrorMessage(error, 'Login failed. Please try again.'));
    }
};

export const logout = async () => {
    try {
        await api.post("/logout", {}, { withCredentials: true });
    } catch (error) {
        throw new Error(getErrorMessage(error, 'Logout failed. Please try again.'));
    }
};

export const getMe = async () => {
    try {
        const response = await api.get("/me", { withCredentials: true });
        return response.data;
    } catch (error) {
        throw new Error(getErrorMessage(error, 'Failed to fetch user data. Please try again.'));
    }
};
