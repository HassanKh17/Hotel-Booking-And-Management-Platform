import { formatCurrency, getBalanceClassName } from "../../utils/formatters";

function OwnerOverviewCard({ owner }) {
    if (!owner) return null;

    return (
        <div className="charges-card" style={{ marginBottom: "20px" }}>
            <h2 style={{ marginBottom: "16px" }}>Owner Details</h2>

            <div className="owner-overview-grid">
                <div className="owner-overview-item">
                    <span className="owner-overview-label">Owner ID: </span>
                    <span className="owner-overview-value">{owner.ownerId}</span>
                </div>

                <div className="owner-overview-item">
                    <span className="owner-overview-label">Username: </span>
                    <span className="owner-overview-value">{owner.username}</span>
                </div>

                <div className="owner-overview-item">
                    <span className="owner-overview-label">Email: </span>
                    <span className="owner-overview-value">{owner.email}</span>
                </div>

                <div className="owner-overview-item">
                    <span className="owner-overview-label">Balance: </span>
                    <span className={`owner-overview-value ${getBalanceClassName(owner.balance)}`}>
                        {formatCurrency(owner.balance)}
                    </span>
                </div>
            </div>
        </div>
    );
}

export default OwnerOverviewCard;
