import { useState } from "react";

function ChargesForm({ onSubmit, loading }) {
    const [percentage, setPercentage] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(percentage);
        setPercentage("");
    };

    return (
        <form onSubmit={handleSubmit}>
            <h3>Update Global Charges</h3>

            <div style={{ marginBottom: "12px" }}>
                <label>Percentage Increase</label>
                <input
                    type="number"
                    step="0.01"
                    value={percentage}
                    onChange={(e) => setPercentage(e.target.value)}
                    placeholder="e.g. 5"
                    required
                    style={{
                        display: "block",
                        width: "100%",
                        padding: "8px",
                        marginTop: "4px",
                    }}
                />
            </div>

            <button type="submit" disabled={loading}>
                Apply Charges
            </button>
        </form>
    );
}

export default ChargesForm;