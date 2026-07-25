const parseError = (error, fallback = "Something went wrong.") => {
    const data = error.response?.data;

    if (typeof data === "string") return data;
    if (data?.message) return data.message;
    if (data?.error) return data.error;
    if (Array.isArray(data?.errors)) return data.errors.join(", ");
    if (typeof data?.errors === "object") return Object.values(data.errors).join(", ");
    if (error.message) return error.message;

    return fallback;
};

export default parseError;